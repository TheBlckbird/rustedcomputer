package dev.theblckbird.rustedcomputer.computer.block

import com.dylibso.chicory.runtime.ImportValues
import com.dylibso.chicory.runtime.Instance
import com.dylibso.chicory.runtime.Store
import com.dylibso.chicory.wasi.WasiOptions
import com.dylibso.chicory.wasi.WasiPreview1
import com.dylibso.chicory.wasm.Parser
import com.dylibso.chicory.wasm.WasmModule
import com.dylibso.chicory.wasm.types.MemoryLimits
import dev.theblckbird.rustedcomputer.ModBlockEntities
import dev.theblckbird.rustedcomputer.RelativeDirection
import dev.theblckbird.rustedcomputer.RustedComputer
import dev.theblckbird.rustedcomputer.computer.ComputerObservations
import dev.theblckbird.rustedcomputer.computer.ComputerScreenHolder
import dev.theblckbird.rustedcomputer.computer.MinecraftTimeClock
import dev.theblckbird.rustedcomputer.computer.hostfunctions.infrastructure.FutureFunctions
import dev.theblckbird.rustedcomputer.computer.hostfunctions.redstone.RedstoneFunctions
import dev.theblckbird.rustedcomputer.computer.networking.toclient.stdout.StdoutData
import dev.theblckbird.rustedcomputer.helpers.SaveFileHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.network.PacketDistributor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.*
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

class ComputerBlockEntity(position: BlockPos, state: BlockState) :
    BlockEntity(ModBlockEntities.COMPUTER.get(), position, state) {
    var uuid = UUID.randomUUID()
    var powerLevels: HashMap<RelativeDirection, Int> = hashMapOf(
        RelativeDirection.TOP to 0,
        RelativeDirection.BOTTOM to 0,
        RelativeDirection.LEFT to 0,
        RelativeDirection.RIGHT to 0,
        RelativeDirection.FRONT to 0,
        RelativeDirection.BACK to 0,
    )

    private var stdout = ""
    private var stdoutWriter = ByteArrayOutputStream()
    private var stdoutLastLength = 0
    private var stdinWriter = PipedOutputStream()

    private var runner: Job? = null

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)

        uuid = tag.getUUID("uuid")
        for ((index, powerLevel) in tag.getIntArray("powerLevels").withIndex()) {
            powerLevels[RelativeDirection.fromInt(index)!!] = powerLevel
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)

        tag.putUUID("uuid", uuid)

        val powerLevelsInt = intArrayOf(0, 0, 0, 0, 0, 0)
        for ((relativeDirection, powerLevel) in powerLevels) {
            powerLevelsInt[relativeDirection.toInt()] = powerLevel
        }

        tag.putIntArray("powerLevels", powerLevelsInt)
    }

    /**
     * Starts a program.
     *
     * If this computer is already executing another program, it is stopped before the new one starts.
     */
    fun startProgram(level: ServerLevel, fileName: String, vararg args: String) {
        this.stopProgram()

        val fakeStdin = PipedInputStream(stdinWriter)
        val clock = MinecraftTimeClock(level)

        val modDataDir = SaveFileHelper.getModDataDir(level).toPath().toString()
        File("$modDataDir/$uuid").mkdir()
        val computerPath = SaveFileHelper.readFile(level, uuid.toString()).toPath()

        val options =
            WasiOptions.builder()
                .withDirectory("/", computerPath)
                .withClock(clock)
                .withStdin(fakeStdin)
                .withStdout(stdoutWriter)
                .withStderr(stdoutWriter)
                .withArguments(listOf(*args))
                .build()

        val wasi = WasiPreview1.builder().withOptions(options).build()

        val redstoneFunctions = RedstoneFunctions(level, blockPos)
        val futureFunctions = FutureFunctions()

        val store = Store()
            .addFunction(*wasi.toHostFunctions())
            .addFunction(*redstoneFunctions.toHostFunctions())
            .addFunction(*futureFunctions.toHostFunctions())

        val resourceManager = level.server.resourceManager
        val romFileLocation = ResourceLocation.fromNamespaceAndPath(RustedComputer.MODID, "rom/$fileName")
        val romFile = resourceManager.getResource(romFileLocation).getOrNull()

        val computerFile = SaveFileHelper.readFile(level, "$uuid/$fileName")

        var wasmModule: WasmModule? = null

        if (romFile != null) {
            wasmModule = Parser.parse(romFile.open())
        } else if (computerFile.exists() && computerFile.isFile && computerFile.canRead()) {
            wasmModule = Parser.parse(computerFile)
        }

        if (wasmModule != null) {
            runner = CoroutineScope(Dispatchers.Default).launch {
                store.instantiate(
                    UUID.randomUUID().toString()
                ) { imports ->
                    Instance
                        .builder(wasmModule)
                        .withImportValues(imports)
                        .withMemoryLimits(MemoryLimits(17, 32))
                        .build()
                }
            }
        } else {
            writelnStdout("Can't find file $fileName")
        }
    }

    /**
     * Run when the block is broken.
     */
    override fun setRemoved() {
        if (level?.isClientSide == true) {
            Minecraft.getInstance().execute {
                ComputerScreenHolder.screen = null
                Minecraft.getInstance().setScreen(null)
            }
        } else {
            this.stopProgram()
            ComputerObservations.removeComputerPosition(blockPos)
        }

        super.setRemoved()
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: ComputerBlockEntity) {
        if (level.isClientSide) {
            return
        }

        val output = stdoutWriter.toString()

        if (output.length > stdoutLastLength) {
            val newText = output.substring(stdoutLastLength)
            stdoutLastLength = output.length
            stdout += newText

            for (playerUuid in ComputerObservations.getListObserving(pos)) {
                PacketDistributor.sendToPlayer(
                    level.getPlayerByUUID(playerUuid) as ServerPlayer, StdoutData(pos, newText)
                )
            }
        }
    }

    /**
     * Stops the program.
     */
    fun stopProgram() {
        runner?.cancel()
        stdinWriter.close()
        stdoutWriter.close()
        runner = null
        stdinWriter = PipedOutputStream()
        stdoutWriter = ByteArrayOutputStream()
        stdoutLastLength = 0
    }

    fun isProgramRunning(): Boolean {
        if (runner?.isActive == false) {
            runner = null
        }

        return runner != null
    }

    fun writeStdin(content: String) {
        stdinWriter.write(content.toByteArray())
    }

    fun getStdout(): String {
        return stdout
    }

    fun writeStdout(content: String) {
        stdout += content

        for (playerUuid in ComputerObservations.getListObserving(blockPos)) {
            PacketDistributor.sendToPlayer(
                level!!.getPlayerByUUID(playerUuid) as ServerPlayer, StdoutData(blockPos, content)
            )
        }
    }

    fun writelnStdout(content: String) {
        writeStdout("$content\n")
    }
}
