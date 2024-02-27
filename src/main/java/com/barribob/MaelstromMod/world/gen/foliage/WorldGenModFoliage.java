package com.barribob.MaelstromMod.world.gen.foliage;

import com.barribob.MaelstromMod.blocks.BlockModBush;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * generates foliage of type BlockAzureBush into the azure dimension
 */
public class WorldGenModFoliage extends WorldGenerator {
    private final BlockModBush[] foliage;
    private final int amount;

    public WorldGenModFoliage(BlockModBush[] tallGrass, int amount) {
        this.foliage = tallGrass;
        this.amount = amount;
    }

    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        if (foliage.length <= 0) return false;
        for (BlockState iblockstate = worldIn.getBlockState(position); (iblockstate.getBlock().isAir(iblockstate, worldIn, position) || iblockstate.getBlock().isLeaves(iblockstate, worldIn, position)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position)) {
            position = position.down();
        }

        for (int i = 0; i < this.amount; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            BlockModBush randomFoliage = foliage[rand.nextInt(foliage.length)];

            if (worldIn.isAirBlock(blockpos) && randomFoliage.canBlockStay(worldIn, blockpos, randomFoliage.getDefaultState())) {
                worldIn.setBlockState(blockpos, randomFoliage.getDefaultState(), 2);
            }
        }

        return true;
    }
}