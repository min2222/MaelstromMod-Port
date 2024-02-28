package com.barribob.mm.blocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockAzureVines extends VineBlock {
    public BlockAzureVines(String name, float hardness, float resistance, SoundType soundType) {
        super(BlockBehaviour.Properties.copy(Blocks.VINE).strength(hardness, resistance).sound(soundType));
    }
}
