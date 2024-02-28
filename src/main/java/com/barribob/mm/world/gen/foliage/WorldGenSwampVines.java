package com.barribob.mm.world.gen.foliage;

import net.minecraft.world.level.block.VineBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/*
 * Generates vines of shorter length
 */
public class WorldGenSwampVines extends WorldGenerator {
    private static final int maxVineLength = 20;

    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        int y = 0;
        boolean placed = true;
        while (y < this.maxVineLength && worldIn.isAirBlock(position.add(new BlockPos(0, y, 0))) && placed) {
            placed = false;
            for (Direction enumfacing : Direction.Plane.HORIZONTAL.facings()) {
                if (Blocks.VINE.canPlaceBlockOnSide(worldIn, position.add(new BlockPos(0, y, 0)), enumfacing)) {
                    BlockState iblockstate = Blocks.VINE.getDefaultState()
                            .withProperty(VineBlock.SOUTH, Boolean.valueOf(enumfacing == Direction.NORTH))
                            .withProperty(VineBlock.WEST, Boolean.valueOf(enumfacing == Direction.EAST))
                            .withProperty(VineBlock.NORTH, Boolean.valueOf(enumfacing == Direction.SOUTH))
                            .withProperty(VineBlock.EAST, Boolean.valueOf(enumfacing == Direction.WEST));
                    worldIn.setBlockState(position.add(new BlockPos(0, y, 0)), iblockstate, 2);
                    placed = true;
                    break;
                }
            }
            y++;
        }

        return true;
    }
}