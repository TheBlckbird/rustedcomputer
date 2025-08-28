package dev.theblckbird.rustedcomputer.computer

import dev.theblckbird.rustedcomputer.RustedComputer
import dev.theblckbird.rustedcomputer.computer.networking.toserver.closescreen.CloseScreenRequest
import dev.theblckbird.rustedcomputer.computer.networking.toserver.openscreen.OpenScreenRequest
import dev.theblckbird.rustedcomputer.computer.networking.toserver.stdin.StdinData
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.network.PacketDistributor

class ComputerScreen(val computerPosition: BlockPos) :
    Screen(Component.translatable("screen.${RustedComputer.MODID}.computer")) {
    lateinit var messageBox: EditBox

    var stdout = ""

    override fun init() {
        super.init()

        PacketDistributor.sendToServer(OpenScreenRequest(computerPosition, 20)) // 20 is what we currently show on screen

        val width = Minecraft.getInstance().screen!!.width
        val height = Minecraft.getInstance().screen!!.height

        this.addRenderableWidget(
            Button.builder(Component.literal("Ok"), {
                PacketDistributor.sendToServer(StdinData(computerPosition, messageBox.value + "\n"))
                messageBox.value = ""
            }).pos(width - 20 - 10, height - 20 - 10).size(20, 20).build()
        )

        messageBox = EditBox(
            Minecraft.getInstance().font,
            width - 20 - 150 - 10,
            height - 20 - 10,
            150,
            20,
            Component.literal("Name?"),
        )

        this.addRenderableWidget(messageBox)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)

        super.render(guiGraphics, mouseX, mouseY, partialTick)

        for ((index, substring) in stdout.lines().withIndex()) {
            guiGraphics.drawString(
                Minecraft.getInstance().font, substring, 10, 10 * index + 10, 0xFFFFFF
            )
        }
    }

    override fun isPauseScreen(): Boolean {
        return false;
    }

    override fun onClose() {
        PacketDistributor.sendToServer(CloseScreenRequest(computerPosition))
        super.onClose()
    }
}