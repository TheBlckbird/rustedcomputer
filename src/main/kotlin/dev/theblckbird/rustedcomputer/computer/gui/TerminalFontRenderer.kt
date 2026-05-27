package dev.theblckbird.rustedcomputer.computer.gui

import dev.theblckbird.rustedcomputer.RustedComputer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

object TerminalFontRenderer {
    private val FONT = ResourceLocation.fromNamespaceAndPath(RustedComputer.MODID, "textures/gui/terminal_font.png")

    /**
     * Texture width of the font
     */
    private const val FONT_WIDTH = 520

    /**
     * Texture height of the font
     */
    private const val FONT_HEIGHT = 10

    /**
     * Height of a single character in the font texture
     */
    const val CHAR_HEIGHT = 10

    /**
     * Width of a single character in the font texture
     */
    const val CHAR_WIDTH = 5

    /**
     * Spacing between the characters when rendered
     */
    const val CHAR_SPACING = 1

    /**
     * Spacing between the lines when rendered
     */
    const val LINE_SPACING = 1

    /**
     * Map of all available characters and their index on the texture
     */
    private const val CHARACTERS =
        "�ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜẞabcdefghijklmnopqrstuvwxyzäöüß()[]{}.,;:!?<>=/\\\"'|#1234567890-+*$%@…_&~^ "

    /**
     * Draw a string at the specified position.
     *
     * Can handle newlines.
     */
    fun drawString(
        graphics: GuiGraphics,
        text: String,
        x: Int, y: Int,
        width: Int, height: Int,
    ) {
        graphics.pose().pushPose()
        var currentX = x
        var currentY = y

        for (char in text) {
            if (char == '\n' || (currentX + CHAR_SPACING + CHAR_SPACING) > width) {
                currentY += CHAR_HEIGHT + LINE_SPACING
                currentX = x
                if (currentY > height) {
                    break
                } else if (char == '\n') {
                    continue
                }
            }

            val textureIndex = (getTextureIndex(char) ?: getUnknownCharacterIndex()).toFloat()

            graphics.blit(
                FONT,
                currentX,
                currentY,
                textureIndex,
                0F,
                CHAR_WIDTH,
                CHAR_HEIGHT,
                FONT_WIDTH,
                FONT_HEIGHT,
            )

            currentX += CHAR_WIDTH + CHAR_SPACING
        }

        graphics.pose().popPose()
    }

    /**
     * Returns the starting x index of a given character.
     *
     * Returns `null` if the character doesn't exist.
     */
    private fun getTextureIndex(char: Char): Int? {
        return CHARACTERS.indexOf(char).let { index ->
            if (index == -1) null else index * CHAR_WIDTH
        }
    }

    /**
     * Returns the index of the unknown character (� in ASCII) in the font file
     */
    private fun getUnknownCharacterIndex(): Int {
        return getTextureIndex('�')!!
    }
}