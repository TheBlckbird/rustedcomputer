package dev.theblckbird.rustedcomputer.computer.block

import dev.theblckbird.rustedcomputer.DataComponents
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block

class ComputerBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag,
    ) {
        val uuid = stack.components.get(DataComponents.COMPUTER_COMPONENT.get())?.uuid ?: return

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(
                Component
                    .literal("[").withColor(0x4a7057)
                    .append(Component.literal("Shift").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("]").withColor(0x4a7057))
            )

            tooltipComponents.add(
                Component
                    .literal("UUID: ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(uuid).withColor(0xC1666B))
            )
        } else {
            tooltipComponents.add(
                Component
                    .literal("[").withColor(0x4a7057)
                    .append(Component.literal("Shift").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal("]").withColor(0x4a7057))
            )
        }
    }
}