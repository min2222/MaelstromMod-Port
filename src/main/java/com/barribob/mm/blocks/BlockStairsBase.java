package com.barribob.mm.blocks;

import net.minecraft.world.level.block.StairBlock;

import com.barribob.mm.Main;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.IHasModel;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStairsBase extends StairBlock implements IHasModel {
    protected BlockStairsBase(BlockState modelState) {
        super(modelState);
    }

    public BlockStairsBase(String name, BlockState modelState) {
        super(modelState);
        setUnlocalizedName(name);
        setRegistryName(name);

        // Add both an item as a block and the block itself
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new BlockItem(this).setRegistryName(this.getRegistryName()));
    }

    public BlockStairsBase(String name, BlockState modelState, float hardness, float resistance, SoundType soundType) {
        this(name, modelState);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(soundType);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}
