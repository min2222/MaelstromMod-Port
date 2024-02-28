package com.barribob.mm.blocks;

import java.util.Random;

import com.barribob.mm.init.ModBlocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;

public class BlockAzureLeaves extends BlockLeavesBase {
    public BlockAzureLeaves(String name, float hardness, float resistance, SoundType soundType) {
        super(name, hardness, resistance, soundType);
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Item.byBlock(ModBlocks.AZURE_SAPLING);
    }
}
