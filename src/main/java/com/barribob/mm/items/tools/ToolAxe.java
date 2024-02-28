package com.barribob.mm.items.tools;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;

public class ToolAxe extends AxeItem {
    public ToolAxe(String name, ToolMaterial material) {
        super(material, 5.0f, -3.0f);
        setCreativeTab(CreativeModeTab.COMBAT);
    }
}
