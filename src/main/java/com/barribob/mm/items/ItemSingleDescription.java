package com.barribob.mm.items;

import java.util.List;

import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemSingleDescription extends ItemBase {
    private String desc;

    public ItemSingleDescription(String name, String desc, CreativeModeTab tab) {
        super(name, tab);
        this.desc = desc;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(ModUtils.translateDesc(desc).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
