package com.barribob.mm.world.gen.foliage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

import com.barribob.mm.blocks.BlockDoubleBrownedGrass;
import com.barribob.mm.init.ModBlocks;

public class WorldGenAzureDoublePlant extends WorldGenerator {
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        boolean flag = false;
        for (BlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position)) {
            position = position.down();
        }

        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

            if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.isNether() || blockpos.getY() < 254) && ModBlocks.DOUBLE_BROWNED_GRASS.canPlaceBlockAt(worldIn, blockpos)) {
                ((BlockDoubleBrownedGrass) ModBlocks.DOUBLE_BROWNED_GRASS).placeAt(worldIn, blockpos, 2);
                flag = true;
            }
        }

        return flag;
    }
}