package com.barribob.MaelstromMod.init;

import com.barribob.MaelstromMod.gui.MaelstromCreativeTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ModCreativeTabs {
    public static CreativeModeTab ITEMS = new MaelstromCreativeTab(CreativeModeTab.getNextID(), "items", () -> ModItems.MAELSTROM_KEY);
    public static CreativeModeTab BLOCKS = new MaelstromCreativeTab(CreativeModeTab.getNextID(), "blocks", () -> Item.getItemFromBlock(ModBlocks.MAELSTROM_BRICKS));
}
