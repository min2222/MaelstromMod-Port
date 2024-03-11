package com.barribob.mm.blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

import com.barribob.mm.init.ModBlocks;

public class BlockPlumLeaves extends BlockLeavesBase {
    public BlockPlumLeaves(String name, float hardness, float resistance, SoundType soundType) {
        super(name, hardness, resistance, soundType);
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.PLUM_SAPLING);
    }
}