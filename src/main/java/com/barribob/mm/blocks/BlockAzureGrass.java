package com.barribob.mm.blocks;

import java.util.List;

import com.barribob.mm.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.Material;

public class BlockAzureGrass extends BlockBase implements BonemealableBlock {

    public BlockAzureGrass(String name, Material material, float hardness, float resistance, SoundType soundType) {
        super(BlockBehaviour.Properties.of(material).strength(hardness, resistance).sound(soundType).randomTicks());
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
    	return true;
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }

    /**
     * Grow azure foliage when bonemeal is used
     */
    @Override
    public void performBonemeal(ServerLevel worldIn, RandomSource rand, BlockPos pos, BlockState state) {
        BlockPos blockpos = pos.above();

        for (int i = 0; i < 128; ++i) {
            BlockPos blockpos1 = blockpos;
            int j = 0;

            while (true) {
                if (j >= i / 16) {
                    if (worldIn.isEmptyBlock(blockpos1)) {
                        Holder<PlacedFeature> holder;
                        if (rand.nextInt(8) == 0) {
                        	List<ConfiguredFeature<?, ?>> list = worldIn.getBiome(blockpos1).value().getGenerationSettings().getFlowerFeatures();
                        	if (list.isEmpty()) {
                        		continue;
                        	}

                        	holder = ((RandomPatchConfiguration)list.get(0).config()).feature();
                            holder.value().place(worldIn, worldIn.getChunkSource().getGenerator(), rand, blockpos1);
                        } else {
                        	if (ModBlocks.BROWNED_GRASS.canSurvive(state, worldIn, blockpos1)) {
                        		worldIn.setBlock(blockpos1, ModBlocks.BROWNED_GRASS.defaultBlockState(), 3);
                            }
                        }
                    }

                    break;
                }

                blockpos1 = blockpos1.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

                if (worldIn.getBlockState(blockpos1.below()).getBlock() != ModBlocks.AZURE_GRASS || worldIn.getBlockState(blockpos1).isCollisionShapeFullBlock(worldIn, blockpos)) {
                    break;
                }

                ++j;
            }
        }
    }

    /*
     * Makes grass spread to other blocks
     */
    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        if (!worldIn.isClientSide) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return; // Forge: prevent loading unloaded chunks when checking neighbor's light and
            // spreading
            if (worldIn.getMaxLocalRawBrightness(pos.above()) < 4 && worldIn.getBlockState(pos.above()).getLightBlock(worldIn, pos.above()) > 2) {
                worldIn.setBlockAndUpdate(pos, ModBlocks.DARK_AZURE_STONE.defaultBlockState());
            } else {
                if (worldIn.getMaxLocalRawBrightness(pos.above()) >= 9) {
                    for (int i = 0; i < 4; ++i) {
                        BlockPos blockpos = pos.offset(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);

                        if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isLoaded(blockpos)) {
                            return;
                        }

                        BlockState iblockstate = worldIn.getBlockState(blockpos.above());
                        BlockState iblockstate1 = worldIn.getBlockState(blockpos);

                        if (iblockstate1.getBlock() == ModBlocks.DARK_AZURE_STONE && worldIn.getMaxLocalRawBrightness(blockpos.above()) >= 4
                                && iblockstate.getLightBlock(worldIn, pos.above()) <= 2) {
                            worldIn.setBlockAndUpdate(blockpos, ModBlocks.AZURE_GRASS.defaultBlockState());
                        }
                    }
                }
            }
        }
    }
}
