package com.barribob.mm.items;

import java.util.List;

import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemKey extends ItemBase {
    private String info_loc;

    public ItemKey(String name, String info_loc, CreativeModeTab tab) {
        super(name, tab);
        this.info_loc = info_loc;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(ModUtils.translateDesc(info_loc).withStyle(ChatFormatting.GRAY));
    }
}
