package com.barribob.MaelstromMod.world.gen.foliage;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Arrays;
import java.util.Random;

public class WorldGenWaterfall extends WorldGenerator {
    private static Block stoneBlock;

    public WorldGenWaterfall(Block stoneBlock) {
        super(false);
        this.stoneBlock = stoneBlock;
    }

    @Override
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        while (!this.isAirNearby(worldIn, position)) {
            position = position.up();
        }

        if (worldIn.getBlockState(position.up()).getBlock().equals(stoneBlock) && worldIn.getBlockState(position.down()).getBlock().equals(stoneBlock)) {
            this.setBlockAndNotifyAdequately(worldIn, position, Blocks.WATER.getDefaultState());
            worldIn.neighborChanged(position, Blocks.WATER, position);
        }
        return true;
    }

    private boolean isAirNearby(Level world, BlockPos pos) {
        for (BlockPos dir : Arrays.asList(pos.east(), pos.west(), pos.north(), pos.south())) {
            if (world.getBlockState(dir).getBlock() == Blocks.AIR) {
                return true;
            }
        }
        return false;
    }
}