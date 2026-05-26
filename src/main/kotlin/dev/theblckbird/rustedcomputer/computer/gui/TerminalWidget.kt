package dev.theblckbird.rustedcomputer.computer.gui

import com.mojang.blaze3d.platform.InputConstants
import dev.theblckbird.rustedcomputer.RustedComputer
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_HEIGHT
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_WIDTH
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW

class TerminalWidget(
    x: Int,
    y: Int,
    val margin: Int,
    val terminal: Terminal,
    val onSubmit: (String) -> Unit,
    description: Component = Component.translatable("gui.${RustedComputer.MODID}.terminal"),
) : AbstractWidget(
    x, y, terminal.characters * CHAR_WIDTH, terminal.lines * CHAR_HEIGHT, description,
) {
    private val innerX: Int = x + margin
    private val innerY: Int = y + margin
    private val innerWidth: Int = width - margin
    private val innerHeight: Int = height - margin

    override fun renderWidget(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float,
    ) {
        TerminalFontRenderer.drawString(
            guiGraphics,
            terminal.getStdout() + terminal.getStdin(),
            innerX, innerY,
            innerWidth, innerHeight,
        )
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, message);
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) return false

        if (Screen.isPaste(keyCode)) {
            terminal.appendStdin(Minecraft.getInstance().keyboardHandler.clipboard)

            return true
        }

        val key = InputConstants.Type.KEYSYM.getOrCreate(keyCode)

        when (key.value) {
            InputConstants.KEY_BACKSPACE -> terminal.backspaceStdin()
            InputConstants.KEY_RETURN -> {
                terminal.newlineStdin()

                if (!Screen.hasShiftDown()) {
                    onSubmit(terminal.getStdin())
                    terminal.clearStdin()
                }
            }
        }

        return false
    }

    override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
        terminal.appendStdin(codePoint)
        return true
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return super.keyReleased(keyCode, scanCode, modifiers)
    }
}