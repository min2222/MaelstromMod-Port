package com.barribob.mm.items;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemTradable extends ItemBase {
    public ItemTradable(String name, CreativeModeTab tab) {
        super(name, tab);
    }

    public ItemTradable(String name) {
        super(name);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal("Used For Trading"));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
