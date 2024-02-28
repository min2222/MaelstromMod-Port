package com.barribob.mm.gui;

import java.util.function.Supplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MaelstromCreativeTab extends CreativeModeTab {
    Supplier<Item> icon;

    public MaelstromCreativeTab(String label, Supplier<Item> icon) {
        super(label);
        this.icon = icon;
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(icon.get());
    }
}
