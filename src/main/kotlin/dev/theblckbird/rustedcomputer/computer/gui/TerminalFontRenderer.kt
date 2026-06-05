package dev.theblckbird.rustedcomputer.computer.gui

import dev.theblckbird.rustedcomputer.RustedComputer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation

object TerminalFontRenderer {

    private val CURSOR = ResourceLocation.fromNamespaceAndPath(RustedComputer.MODID, "textures/gui/terminal_cursor.png")

    /**
     * Width of the cursor
     */
    private const val CURSOR_WIDTH = 5

    /**
     * Height of the cursor
     */
    private const val CURSOR_HEIGHT = 12

    /**
     * Length of a complete blink cycle in ticks
     */
    private const val CURSOR_BLINK_CYCLE = 20

    private val FONT = ResourceLocation.fromNamespaceAndPath(RustedComputer.MODID, "textures/gui/terminal_font.png")

    /**
     * Texture width of the font
     */
    private const val FONT_WIDTH = 500

    /**
     * Texture height of the font
     */
    private const val FONT_HEIGHT = 156

    /**
     * Height of a single character in the font texture
     */
    const val CHAR_HEIGHT = 12

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
    const val LINE_SPACING = 0

    /**
     * Map of all available characters and their index on the texture
     */
    private const val CHARACTERS =
        """ !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~¡¢£¥¦§¨ª¬°±²³´µ·¸¹º¿ÅÆÇ×ØÞßæçìíîïð÷øĄąĘęĢģĩīĭıĵĶķĻļŁłŅņŒœŞşťŮǐĵȘșȚțЀЁÄÖÜäöüЂЃЄЅІЇЈЊЋЌЍЎЏАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшъыьэюяѐёђѓєѕіїјњћќѝўџҐґ€←↑→↓⇐⇑⇒⇓∀∷☺♥‘’“”„⁊‚†‡•′″‵‶‹›‼‽⁎⁏⁑⁒‑þ¯₡₮₰₲₵₶₸₹₼₿≠−∓∩∃∄∉∋∌⊂⊃⊄⊅∧∨⊻⊼⊽∥⋆∑⊤⊥⊢⊨∁∴∵∂⋃⊆⊇∫∮һІҮүӨөӀѲѳқғҰұӘәҺΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩαβγδεζηθικλμνξοπρςτυφχψ;·ϛ＋ƏƐƆƎɅǝƷǷƿȜȝȤȥǀǃǂǁƩƲƚƛȠƞƟƧƨƪƸƹƻƼƽƾȴȶȺȻȼɆȾɁɂəɛɪʔʕʬɔɟɥɾʞɯɹʇʌʍʎʒʙɢʜʟɴʀʏɱʈɡʡɕʑɸʝʢʁɦʋɰɬɮʘɓɧɫɨʊɘɵɤɜɞɑɒɺʗʖɭɿʅʓʚɼʻˌ˙ĸĲĳẞẟẜẝỼỽỾ⁰Ԁɐ֏ſᚠᴀႠჿⴀ₠⁴⁵⁶⁷⁸⁹⁺⁻⁼⁽⁾ⁱ₀₁₂₃₄₅₆₇₈₉₊₋₌₍₎Ⅎ⅁⅄⅋☲☵♀♂⚥♠♣♦♩♪♭♮♯⚡☺☹♤♧♡♢☰☱☳☴☶☷אבגדהוזחטיכלמםנןסעפףצץקרשתך׳״װױײ־׃׆❣⸘⸮⸵⸸⹁⹋⥝ᘔᗺᗡ߈ㄥⱯⱦⱭꞰꞀꝹꞁꝚꝛꜰꞯꜱꜧꜦꞩᴚᵷᴉᴄᴅᴇᴊᴋᴍᴏᴘᴛᴜᴠᴡᴢ⟘ԱԲԶԷԻԼԾԿՀՁՃՅՆՇՈՉՋՍՎՏՐՑՒՓՔՕՖՙաբեէըթիլծկհձճյշոչպջռսրցւօֆԸ՚՛՜՝՞՟ՠԵՊ֊⏏⏴⏵⏶⏷⏸⏹⏺⏽⏳▲▼●◦◘□△▽◆◇○ﬁﬅתּשׂפֿפּכּײַיִוֹוּבֿבּᚢᚣᚤᚥᚦᚧᚨᚩᚪᚫᚬᚭᚮᚯᚰᚱᚲᚳᚴᚶᚷᚸᚹᚺᚻᚼᚽᚾᚿᛀᛁᛂᛄᛅᛆᛇᛈᛉᛊᛋᛌᛍᛎᛏᛐᛑᛒᛓᛔᛕᛖᛗᛘᛙᛚᛛᛜᛝᛞᛡᛣᛤᛥᛦᛧᛨᛩ᛫᛬᛭ᛮᛯᛰᛱᛲᛴᛵᛶᛷᛸႣႤႨႩႬႱႲႴႵႶႷႸႹႺႼႽႾႿჁჂჃჄჇჍაბგდევზთიკმნოპჟრსტუფქღყშჩცძწჭხჯჰჲჳჴჵჶჷჸჹჺ჻ჼჽჾⴃⴄⴅⴡⴇⴈⴉⴊⴋⴌⴢⴍⴐⴑⴒⴣⴓⴔⴕⴖⴗⴘⴙⴚⴛⴜⴞⴤⴟⴠⴥ〒�"""

    private var showCursorUntilNextCycle = false

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
        charsPersLine: Int,
    ) {
        graphics.pose().pushPose()
        var currentX = x
        var currentY = y

        var currentChar = 0

        for (char in text) {
            if (char == '\n' || currentChar == charsPersLine) {
                currentChar = 0

                currentY += CHAR_HEIGHT + LINE_SPACING
                currentX = x
                if (currentY > height) {
                    break
                } else if (char == '\n') {
                    continue
                }
            }

            currentChar += 1
            val (u, v) = (getTextureUV(char) ?: getUnknownCharacterUV())

            graphics.blit(
                FONT,
                currentX,
                currentY,
                u, v,
                CHAR_WIDTH,
                CHAR_HEIGHT,
                FONT_WIDTH,
                FONT_HEIGHT,
            )

            currentX += CHAR_WIDTH + CHAR_SPACING
        }

        graphics.pose().popPose()
    }

    fun drawCursor(
        graphics: GuiGraphics,
        x: Int, y: Int,
        char: Int, line: Int,
        frameTicks: Long,
        showUntilNextCycle: Boolean,
    ) {
        if (!showCursorUntilNextCycle && showUntilNextCycle) {
            showCursorUntilNextCycle = true
        }

        var shouldRender = false

        if (frameTicks % CURSOR_BLINK_CYCLE > (CURSOR_BLINK_CYCLE / 2)) {
            showCursorUntilNextCycle = false
            shouldRender = true
        } else if (showCursorUntilNextCycle) {
            shouldRender = true
        }

        if (shouldRender) {
            graphics.pose().pushPose()

            val xPosition = char * (CHAR_WIDTH + CHAR_SPACING) + x
            val yPosition = line * (CHAR_HEIGHT + LINE_SPACING) + y

            graphics.blit(
                CURSOR,
                xPosition, yPosition,
                0F, 0F,
                CURSOR_WIDTH, CURSOR_HEIGHT,
                CURSOR_WIDTH, CURSOR_HEIGHT,
            )

            graphics.pose().popPose()
        }
    }

    /**
     * Returns the starting x index of a given character.
     *
     * Returns `null` if the character doesn't exist.
     */
    private fun getTextureUV(char: Char): Pair<Float, Float>? {
        return CHARACTERS.indexOf(char).let { index ->
            if (index == -1) {
                null
            } else {
                (index * CHAR_WIDTH % FONT_WIDTH).toFloat() to (index * CHAR_WIDTH / FONT_WIDTH * CHAR_HEIGHT).toFloat()
            }
        }
    }

    /**
     * Returns the index of the unknown character (� in ASCII) in the font file
     */
    private fun getUnknownCharacterUV(): Pair<Float, Float> {
        return getTextureUV('�')!!
    }
}