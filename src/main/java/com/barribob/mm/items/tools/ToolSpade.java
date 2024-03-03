package com.barribob.mm.items.tools;

import com.barribob.mm.init.ModCreativeTabs;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;

public class ToolSpade extends ShovelItem {
    public ToolSpade(String name, Tier material) {
        super(material, 0, 0, new Item.Properties().tab(ModCreativeTabs.ITEMS).stacksTo(1));
    }
}
