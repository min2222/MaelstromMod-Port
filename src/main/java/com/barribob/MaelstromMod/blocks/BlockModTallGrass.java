package com.barribob.MaelstromMod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;

import java.util.Random;

/**
 * Represents a single-block-tall grass block
 */
public class BlockModTallGrass extends BlockModBush {
    public BlockModTallGrass(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(name, material, Blocks.GRASS, hardness, resistance, soundType);
    }

    protected static final AABB TALL_GRASS_AABB = new AABB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D,
            0.8999999761581421D);

    @Override
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return TALL_GRASS_AABB;
    }

    /**
     * Whether this Block can be replaced directly by other blocks (true for e.g.
     * tall grass)
     */
    @Override
    public boolean isReplaceable(BlockGetter worldIn, BlockPos pos) {
        return true;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return null;
    }
}
