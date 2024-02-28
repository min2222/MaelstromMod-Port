package com.barribob.mm.world.gen.cliff;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

import com.barribob.mm.init.ModBlocks;

public class WorldGenSmallLedge extends WorldGenCliffLedge {
    public WorldGenSmallLedge(String name, int yOffset) {
        super(name, yOffset);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, Level worldIn, Random rand) {
        if (rand.nextInt(5) == 0) {
            worldIn.setBlockToAir(pos);
        } else if (rand.nextInt(5) == 0) {
            worldIn.setBlockState(pos, ModBlocks.CRACKED_SWAMP_BRICK.getDefaultState());
        } else {
            worldIn.setBlockState(pos, ModBlocks.SWAMP_BRICK.getDefaultState());
        }
    }
}
