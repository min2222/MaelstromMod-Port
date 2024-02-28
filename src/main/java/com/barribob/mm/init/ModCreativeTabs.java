package com.barribob.mm.init;

import com.barribob.mm.gui.MaelstromCreativeTab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ModCreativeTabs {
    public static CreativeModeTab ITEMS = new MaelstromCreativeTab("items", () -> ModItems.MAELSTROM_KEY);
    public static CreativeModeTab BLOCKS = new MaelstromCreativeTab("blocks", () -> Item.byBlock(ModBlocks.MAELSTROM_BRICKS));
}
