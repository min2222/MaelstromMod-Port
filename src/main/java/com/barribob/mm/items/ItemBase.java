package com.barribob.mm.items;

import com.barribob.mm.init.ModCreativeTabs;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

/**
 * The base class for basic mod foods
 */
public class ItemBase extends Item {
    public ItemBase(String name, CreativeModeTab tab) {
    	super(new Item.Properties().tab(tab));
    }
    
    public ItemBase(Item.Properties properties) {
    	super(properties.tab(ModCreativeTabs.ITEMS));
    }

    public ItemBase(String name) {
        this(name, ModCreativeTabs.ITEMS);
    }
}
