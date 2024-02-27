package com.barribob.MaelstromMod.gui;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class MaelstromCreativeTab extends CreativeModeTab {
    Supplier<Item> icon;

    public MaelstromCreativeTab(int index, String label, Supplier<Item> icon) {
        super(index, label);
        this.icon = icon;
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(icon.get());
    }
}
