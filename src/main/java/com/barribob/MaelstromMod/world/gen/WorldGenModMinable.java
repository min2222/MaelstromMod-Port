package com.barribob.MaelstromMod.world.gen;

import com.google.common.base.Predicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * Generates ores in the azure dimension
 */
public class WorldGenModMinable extends WorldGenerator {
    private final BlockState oreBlock;
    private final BlockState stoneBlock;
    /**
     * The number of blocks to generate.
     */
    private final int numberOfBlocks;
    private final Predicate<BlockState> predicate;

    public WorldGenModMinable(BlockState state, Block stoneBlock, int blockCount) {
        this(state, stoneBlock, blockCount, new Predicate<BlockState>() {
            @Override
            public boolean apply(BlockState p_apply_1_) {
                if (p_apply_1_ != null && p_apply_1_.getBlock() == stoneBlock) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public WorldGenModMinable(BlockState state, Block stoneBlock, int blockCount, Predicate<BlockState> p_i45631_3_) {
        this.oreBlock = state;
        this.numberOfBlocks = blockCount;
        this.predicate = p_i45631_3_;
        this.stoneBlock = stoneBlock.getDefaultState();
    }

    @Override
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        float f = rand.nextFloat() * (float) Math.PI;
        double d0 = position.getX() + 8 + Mth.sin(f) * this.numberOfBlocks / 8.0F;
        double d1 = position.getX() + 8 - Mth.sin(f) * this.numberOfBlocks / 8.0F;
        double d2 = position.getZ() + 8 + Mth.cos(f) * this.numberOfBlocks / 8.0F;
        double d3 = position.getZ() + 8 - Mth.cos(f) * this.numberOfBlocks / 8.0F;
        double d4 = position.getY() + rand.nextInt(3) - 2;
        double d5 = position.getY() + rand.nextInt(3) - 2;

        for (int i = 0; i < this.numberOfBlocks; ++i) {
            float f1 = (float) i / (float) this.numberOfBlocks;
            double d6 = d0 + (d1 - d0) * f1;
            double d7 = d4 + (d5 - d4) * f1;
            double d8 = d2 + (d3 - d2) * f1;
            double d9 = rand.nextDouble() * this.numberOfBlocks / 16.0D;
            double d10 = (Mth.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (Mth.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            int j = Mth.floor(d6 - d10 / 2.0D);
            int k = Mth.floor(d7 - d11 / 2.0D);
            int l = Mth.floor(d8 - d10 / 2.0D);
            int i1 = Mth.floor(d6 + d10 / 2.0D);
            int j1 = Mth.floor(d7 + d11 / 2.0D);
            int k1 = Mth.floor(d8 + d10 / 2.0D);

            for (int l1 = j; l1 <= i1; ++l1) {
                double d12 = (l1 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        double d13 = (i2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int j2 = l; j2 <= k1; ++j2) {
                                double d14 = (j2 + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    BlockPos blockpos = new BlockPos(l1, i2, j2);

                                    BlockState state = worldIn.getBlockState(blockpos);
                                    if (state.getBlock().isReplaceableOreGen(state, worldIn, blockpos, this.predicate)) {
                                        worldIn.setBlockState(blockpos, this.oreBlock, 2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}