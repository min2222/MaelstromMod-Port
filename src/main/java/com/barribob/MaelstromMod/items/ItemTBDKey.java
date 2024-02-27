package com.barribob.MaelstromMod.items;

import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * The promo item for the next dimensions
 */
public class ItemTBDKey extends ItemBase {
    public ItemTBDKey(String name, CreativeModeTab tab) {
        super(name, tab);
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("tbd_key"));
    }
}
