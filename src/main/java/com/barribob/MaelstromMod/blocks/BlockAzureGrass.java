package com.barribob.MaelstromMod.blocks;

import java.util.Random;

import com.barribob.MaelstromMod.init.ModBlocks;

import net.minecraft.block.IGrowable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockAzureGrass extends BlockBase implements IGrowable {

    public BlockAzureGrass(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(name, material, hardness, resistance, soundType);
        this.setTickRandomly(true);
        this.setHarvestLevel("shovel", 1);
    }

    @Override
    public boolean canGrow(Level worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(Level worldIn, Random rand, BlockPos pos, BlockState state) {
        return true;
    }

    /**
     * Grow azure foliage when bonemeal is used
     */
    @Override
    public void grow(Level worldIn, Random rand, BlockPos pos, BlockState state) {
        BlockPos blockpos = pos.up();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isAirBlock(blockpos1)) {
                        if (rand.nextInt(8) == 0) {
                            worldIn.getBiome(blockpos1).plantFlower(worldIn, rand, blockpos1);
                        } else {
                            if (ModBlocks.BROWNED_GRASS.canPlaceBlockAt(worldIn, blockpos1)) {
                                worldIn.setBlockState(blockpos1, ModBlocks.BROWNED_GRASS.getDefaultState(), 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(blockpos1.down()).getBlock() != ModBlocks.AZURE_GRASS || worldIn.getBlockState(blockpos1).isNormalCube()) {
                    break;
                }

                ++j;
            }
        }
    }

    /*
     * Makes grass spread to other blocks
     */
    public void updateTick(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        if (!worldIn.isRemote) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light and
            // spreading
            if (worldIn.getLightFromNeighbors(pos.up()) < 4 && worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2) {
                worldIn.setBlockState(pos, ModBlocks.DARK_AZURE_STONE.getDefaultState());
            } else {
                if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
                    for (int i = 0; i < 4; ++i) {
                        BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

                        if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isBlockLoaded(blockpos)) {
                            return;
                        }

                        BlockState iblockstate = worldIn.getBlockState(blockpos.above());
                        BlockState iblockstate1 = worldIn.getBlockState(blockpos);

                        if (iblockstate1.getBlock() == ModBlocks.DARK_AZURE_STONE && worldIn.getLightFromNeighbors(blockpos.up()) >= 4
                                && iblockstate.getLightOpacity(worldIn, pos.above()) <= 2) {
                            worldIn.setBlockState(blockpos, ModBlocks.AZURE_GRASS.getDefaultState());
                        }
                    }
                }
            }
        }
    }

}
