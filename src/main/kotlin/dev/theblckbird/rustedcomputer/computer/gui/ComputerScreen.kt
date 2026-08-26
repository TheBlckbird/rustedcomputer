package dev.theblckbird.rustedcomputer.computer.gui

import com.mojang.blaze3d.platform.InputConstants
import dev.theblckbird.rustedcomputer.RustedComputer
import dev.theblckbird.rustedcomputer.computer.networking.toserver.closescreen.CloseScreenRequest
import dev.theblckbird.rustedcomputer.computer.networking.toserver.openscreen.OpenScreenRequest
import dev.theblckbird.rustedcomputer.computer.networking.toserver.stdin.StdinData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.PacketDistributor

class ComputerScreen(val computerPosition: BlockPos) :
    Screen(Component.translatable("screen.${RustedComputer.MODID}.computer")) {
    companion object {
        private val BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(RustedComputer.MODID, "textures/gui/computer_screen.png")

        private const val BACKGROUND_WIDTH = 393
        private const val BACKGROUND_HEIGHT = 253
        private const val MARGIN = 17

        private const val TERMINAL_CHARACTERS = 60
        private const val TERMINAL_LINES = 17

        const val X = 10
        const val Y = 10
    }

    lateinit var terminalWidget: TerminalWidget
    lateinit var inputWidget: EditBox
    val terminal = Terminal(TERMINAL_CHARACTERS, TERMINAL_LINES)

    override fun init() {
        super.init()

        PacketDistributor.sendToServer(
            OpenScreenRequest(
                computerPosition,
                terminal.lines,
            )
        )

        terminalWidget = TerminalWidget(
            X, Y,
            MARGIN,
            terminal,
        )
        this.addRenderableWidget(terminalWidget)

        inputWidget = EditBox(
            Minecraft.getInstance().font,
            X + MARGIN,
            Y + terminalWidget.height,
            terminalWidget.width - 2 * MARGIN,
            18,
            Component.literal("Input..."),
        )
        inputWidget.setMaxLength(128)

        this.addRenderableWidget(inputWidget)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)

        guiGraphics.blit(
            BACKGROUND,
            X, Y,
            0F, 0F,
            BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
            512, 512,
        )

        for (renderable in renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick)
        }
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun onClose() {
        PacketDistributor.sendToServer(CloseScreenRequest(computerPosition))
        super.onClose()
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val key = InputConstants.Type.KEYSYM.getOrCreate(keyCode)

        if (inputWidget.isFocused && (key.value == InputConstants.KEY_RETURN || key.value == InputConstants.KEY_NUMPADENTER)) {
            onSubmit()
            return true
        }

        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun onSubmit() {
        val stdin = inputWidget.value
        inputWidget.value = ""

        PacketDistributor.sendToServer(StdinData(computerPosition, stdin))
    }
}