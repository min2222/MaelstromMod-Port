package com.barribob.mm.items.tools;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

public class ToolAxe extends AxeItem {
    public ToolAxe(String name, Tier material) {
        super(material, 5.0f, -3.0f, new Item.Properties().tab(CreativeModeTab.TAB_COMBAT));
    }
}
