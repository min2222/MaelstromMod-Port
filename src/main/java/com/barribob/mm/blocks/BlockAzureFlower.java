package com.barribob.mm.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;

/**
 * Represents azure dimension flowers
 */
public class BlockAzureFlower extends BlockModBush {
    public BlockAzureFlower(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(name, material, Blocks.GRASS, hardness, resistance, soundType);
    }

    @Override
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return super.getBoundingBox(state, source, pos).move(state.getOffset(source, pos));
    }
}
