package com.barribob.mm.blocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Replicates redstone ore for the azure dimension
 */
public class BlockAzureRedstoneOre extends RedStoneOreBlock {
    public BlockAzureRedstoneOre(String name, float hardness, float resistance, SoundType soundType) {
        super(BlockBehaviour.Properties.copy(Blocks.REDSTONE_ORE).strength(hardness, resistance).sound(soundType));
    }
}
