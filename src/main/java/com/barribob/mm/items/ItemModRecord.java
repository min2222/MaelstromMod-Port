package com.barribob.mm.items;

import java.util.function.Supplier;

import com.barribob.mm.init.ModCreativeTabs;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

public class ItemModRecord extends RecordItem {
	//TODO
    public ItemModRecord(String name, Supplier<SoundEvent> event) {
        super(0, event, new Item.Properties().tab(ModCreativeTabs.ITEMS), 0);
    }
}
