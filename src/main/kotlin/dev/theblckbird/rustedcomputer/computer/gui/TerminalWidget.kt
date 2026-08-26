package dev.theblckbird.rustedcomputer.computer.gui

import dev.theblckbird.rustedcomputer.RustedComputer
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_HEIGHT
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_SPACING
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_WIDTH
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.LINE_SPACING
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

class TerminalWidget(
    x: Int,
    y: Int,
    margin: Int,
    val terminal: Terminal,
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
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, message)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        val perLine = 5
        val lines = (scrollY / perLine).roundToInt()
        RustedComputer.LOGGER.debug(lines.toString())

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }
}