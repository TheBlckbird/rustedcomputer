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

class TerminalWidget(
    x: Int,
    y: Int,
    margin: Int,
    val terminal: Terminal,
    val onSubmit: (String) -> Unit,
    description: Component = Component.translatable("gui.${RustedComputer.MODID}.terminal"),
) : AbstractWidget(
    x, y, terminal.characters * CHAR_WIDTH, terminal.lines * CHAR_HEIGHT, description,
) {
    private val innerX = x + margin
    private val innerY = y + margin
    private val innerWidth = width - margin
    private val innerHeight = height - margin

    private var frameTicks: Long = 0

    /**
     * The focus state in the last render frame
     */
    private var wasFocusedBefore = isFocused

    /**
     * Whether the cursor rendering should be forced in the next frame regardless of its blinking state
     */
    private var forceShowCursor = false

    /**
     * Call this function every tick from the Screen
     */
    fun tick() {
        frameTicks += 1
    }

    override fun renderWidget(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float,
    ) {
        TerminalFontRenderer.drawString(
            guiGraphics,
            terminal.stdout + terminal.stdin,
            innerX, innerY,
            innerWidth, innerHeight,
        )

        if (isFocused) {
            TerminalFontRenderer.drawCursor(
                guiGraphics,
                innerX, innerY,
                terminal.cursorChar, terminal.cursorLine,
                frameTicks,
                isFocused != wasFocusedBefore || forceShowCursor,
            )
        }

        forceShowCursor = false
        wasFocusedBefore = isFocused
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, message)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val key = InputConstants.Type.KEYSYM.getOrCreate(keyCode)

        // Let Minecraft handle the exit event when `ESC` is pressed
        if (key.value == InputConstants.KEY_ESCAPE) return false

        forceShowCursor = true

        if (Screen.isPaste(keyCode)) {
            terminal.insertStdin(Minecraft.getInstance().keyboardHandler.clipboard)

            return true
        }

        when (key.value) {
            InputConstants.KEY_BACKSPACE -> terminal.backspaceStdin()
            InputConstants.KEY_RETURN -> {
                terminal.newlineStdin()

                if (!Screen.hasShiftDown()) {
                    onSubmit(terminal.stdin)
                    terminal.clearStdin()
                }
            }

            InputConstants.KEY_LEFT -> terminal.moveCursorLeft()
            InputConstants.KEY_RIGHT -> terminal.moveCursorRight()
        }

        return false
    }

    override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
        forceShowCursor = true
        terminal.insertStdin(codePoint)
        return true
    }
}