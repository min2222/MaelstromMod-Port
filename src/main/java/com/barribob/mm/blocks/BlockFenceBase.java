package com.barribob.mm.blocks;

import net.minecraft.world.level.block.FenceBlock;

import com.barribob.mm.Main;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.IHasModel;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;

public class BlockFenceBase extends FenceBlock implements IHasModel {

    public BlockFenceBase(Material materialIn, MaterialColor mapColorIn) {
        super(materialIn, mapColorIn);
    }

    public BlockFenceBase(String name, Material material) {
        super(material, MaterialColor.WOOD);
        setUnlocalizedName(name);
        setRegistryName(name);

        // Add both an item as a block and the block itself
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new BlockItem(this).setRegistryName(this.getRegistryName()));
    }

    public BlockFenceBase(String name, Material material, float hardness, float resistance, SoundType soundType) {
        this(name, material);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(soundType);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}
