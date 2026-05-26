package dev.theblckbird.rustedcomputer.computer.gui

import dev.theblckbird.rustedcomputer.RustedComputer
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_HEIGHT
import dev.theblckbird.rustedcomputer.computer.gui.TerminalFontRenderer.CHAR_WIDTH
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class TerminalWidget(
    x: Int,
    y: Int,
    val terminal: Terminal,
    description: Component = Component.translatable("gui.${RustedComputer.MODID}.terminal"),
) : AbstractWidget(
    x, y, terminal.width * CHAR_WIDTH, terminal.height * CHAR_HEIGHT, description,
) {
    companion object {
        private const val MARGIN = 0
    }

    private val innerX: Int = x + MARGIN
    private val innerY: Int = y + MARGIN
    private val innerWidth: Int = width - MARGIN
    private val innerHeight: Int = height - MARGIN

    /*val width: Int
        get() {
            return innerWidth + MARGIN * 2
        }

    val height: Int
        get() {
            return innerHeight + MARGIN * 2
        }*/

    override fun renderWidget(
        guiGraphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float,
    ) {
        TerminalFontRenderer.drawString(
            guiGraphics,
            terminal.getBuffer(),
            innerX, innerY,
        )
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, message);
    }
}