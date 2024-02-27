package com.barribob.MaelstromMod.items;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemTradable extends ItemBase {
    public ItemTradable(String name, CreativeModeTab tab) {
        super(name, tab);
    }

    public ItemTradable(String name) {
        super(name);
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        tooltip.add("Used For Trading");
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
