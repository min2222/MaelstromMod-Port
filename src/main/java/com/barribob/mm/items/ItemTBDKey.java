package com.barribob.mm.items;

import java.util.List;

import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * The promo item for the next dimensions
 */
public class ItemTBDKey extends ItemBase {
    public ItemTBDKey(String name, CreativeModeTab tab) {
        super(name, tab);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(ModUtils.translateDesc("tbd_key").withStyle(ChatFormatting.GRAY));
    }
}
