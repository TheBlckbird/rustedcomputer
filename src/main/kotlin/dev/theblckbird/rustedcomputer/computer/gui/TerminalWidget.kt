package dev.theblckbird.rustedcomputer.computer.gui

import com.mojang.blaze3d.platform.InputConstants
import dev.theblckbird.rustedcomputer.RustedComputer
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_HEIGHT
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_SPACING
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_WIDTH
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.LINE_SPACING
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

class TerminalWidget(
    x: Int,
    y: Int,
    margin: Int,
    val terminal: Terminal,
    val onSubmit: (String) -> Unit,
    description: Component = Component.translatable("gui.${RustedComputer.MODID}.terminal"),
) : AbstractWidget(
    x,
    y,
    terminal.characters * (CHAR_WIDTH + CHAR_SPACING) - CHAR_SPACING + 2 * margin,
    terminal.lines * (CHAR_HEIGHT + LINE_SPACING) - LINE_SPACING + 2 * margin,
    description,
) {
    private val innerX = x + margin
    private val innerY = y + margin

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
            terminal.visibleSegments.joinToString("\n"),
            innerX, innerY,
            terminal.characters, terminal.lines,
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
            InputConstants.KEY_UP -> terminal.moveCursorUp()
            InputConstants.KEY_DOWN -> terminal.moveCursorDown()
        }

        return false
    }

    override fun charTyped(codePoint: Char, modifiers: Int): Boolean {
        forceShowCursor = true
        terminal.insertStdin(codePoint)
        return true
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        val perLine = 5
        val lines = (scrollY / perLine).roundToInt()
        RustedComputer.LOGGER.debug(lines.toString())

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }
}