package com.barribob.mm.items;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.util.ModUtils;

public class ItemKey extends ItemBase {
    private String info_loc;

    public ItemKey(String name, String info_loc, CreativeModeTab tab) {
        super(name, tab);
        this.info_loc = info_loc;
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc(info_loc));
    }
}
