package com.barribob.mm.world.gen.foliage;

import net.minecraft.world.level.block.VineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

import com.barribob.mm.init.ModBlocks;

/**
 * Generates azure vines in a row until the conditions are broken, then generates a new row.
 */
public class WorldGenAzureVines extends WorldGenerator {
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        int maxY = position.getY() + rand.nextInt(25) + 5;
        BlockPos newPos = position;
        for (; newPos.getY() < maxY; newPos = newPos.up()) {
            if (worldIn.isAirBlock(newPos)) {
                for (Direction enumfacing : Direction.Plane.HORIZONTAL.facings()) {
                    if (ModBlocks.AZURE_VINES.canPlaceBlockOnSide(worldIn, newPos, enumfacing)) {
                        BlockState iblockstate = ModBlocks.AZURE_VINES.getDefaultState().withProperty(VineBlock.SOUTH, Boolean.valueOf(enumfacing == Direction.NORTH)).withProperty(VineBlock.WEST, Boolean.valueOf(enumfacing == Direction.EAST)).withProperty(VineBlock.NORTH, Boolean.valueOf(enumfacing == Direction.SOUTH)).withProperty(VineBlock.EAST, Boolean.valueOf(enumfacing == Direction.WEST));
                        worldIn.setBlockState(newPos, iblockstate, 2);
                        break;
                    }
                }
            } else {
                // Find a new position to place the vine row
                newPos = new BlockPos(rand.nextInt(4) - rand.nextInt(4) + position.getX(), newPos.getY(), rand.nextInt(4) - rand.nextInt(4) + position.getZ());
            }
        }

        return true;
    }
}