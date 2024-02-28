package com.barribob.mm.world.gen.foliage;

import net.minecraft.block.BlockOldLeaf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

import com.barribob.mm.init.ModBlocks;

public class WorldGenPlumTree extends WorldGenAbstractTree {
    private static final BlockState LOG = ModBlocks.PLUM_LOG.getDefaultState();
    private static final BlockState LEAF = ModBlocks.PLUM_LEAVES.getDefaultState().withProperty(BlockOldLeaf.CHECK_DECAY, Boolean.valueOf(false));
    private static final BlockState PLUM = ModBlocks.PLUM_FILLED_LEAVES.getDefaultState().withProperty(BlockOldLeaf.CHECK_DECAY, Boolean.valueOf(false));

    public static final int plumLeafChance = 15;
    public static final int plumLeafDensity = 10;

    private final boolean useExtraRandomHeight;

    public WorldGenPlumTree(boolean notify, boolean useExtraRandomHeightIn) {
        super(notify);
        this.useExtraRandomHeight = useExtraRandomHeightIn;
    }

    @Override
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        int i = rand.nextInt(3) + 5;
        int generatePlums = rand.nextInt(plumLeafChance);

        if (this.useExtraRandomHeight) {
            i += rand.nextInt(7);
        }

        boolean flag = true;

        if (position.getY() >= 1 && position.getY() + i + 1 <= 256) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;

                if (j == position.getY()) {
                    k = 0;
                }

                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
                }

                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < worldIn.getHeight()) {
                            if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(l, j, i1))) {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag) {
                return false;
            } else {
                BlockPos down = position.down();
                BlockState state = worldIn.getBlockState(down);
                boolean isSoil = state.getBlock() == Blocks.GRASS;

                if (isSoil && position.getY() < worldIn.getHeight() - i - 1) {
                    state.getBlock().onPlantGrow(state, worldIn, down, position);

                    for (int i2 = position.getY() - 3 + i; i2 <= position.getY() + i; ++i2) {
                        int k2 = i2 - (position.getY() + i);
                        int l2 = 1 - k2 / 2;

                        for (int i3 = position.getX() - l2; i3 <= position.getX() + l2; ++i3) {
                            int j1 = i3 - position.getX();

                            for (int k1 = position.getZ() - l2; k1 <= position.getZ() + l2; ++k1) {
                                int l1 = k1 - position.getZ();

                                if (Math.abs(j1) != l2 || Math.abs(l1) != l2 || rand.nextInt(2) != 0 && k2 != 0) {
                                    BlockPos blockpos = new BlockPos(i3, i2, k1);
                                    BlockState state2 = worldIn.getBlockState(blockpos);

                                    if (state2.getBlock().isAir(state2, worldIn, blockpos) || state2.getBlock().isAir(state2, worldIn, blockpos)) {
                                        if (generatePlums == 0 && rand.nextInt(plumLeafDensity) == 0) {
                                            this.setBlockAndNotifyAdequately(worldIn, blockpos, PLUM);
                                        } else {
                                            this.setBlockAndNotifyAdequately(worldIn, blockpos, LEAF);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (int j2 = 0; j2 < i; ++j2) {
                        BlockPos upN = position.up(j2);
                        BlockState state2 = worldIn.getBlockState(upN);

                        if (state2.getBlock().isAir(state2, worldIn, upN) || state2.getBlock().isLeaves(state2, worldIn, upN)) {
                            this.setBlockAndNotifyAdequately(worldIn, position.up(j2), LOG);
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}