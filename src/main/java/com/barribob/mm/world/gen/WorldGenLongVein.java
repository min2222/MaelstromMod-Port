package com.barribob.mm.world.gen;

import net.minecraft.block.BlockStone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.util.ModRandom;

public class WorldGenLongVein extends WorldGenerator {
    private final BlockState block;

    public WorldGenLongVein() {
        BlockState stone = Blocks.STONE.getDefaultState();
        this.block = ModRandom.choice(new BlockState[]{
                stone,
                ModBlocks.RED_CLIFF_STONE.getDefaultState(),
                stone.withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE),
                stone.withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE),
                stone.withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE)});
    }

    @Override
    public boolean generate(Level worldIn, Random rand, BlockPos pos) {
        int size = 200;
        for (int y = 0; y < size; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos newPos = new BlockPos(x, -y, z).add(pos);
                    if (worldIn.rand.nextFloat() > 0.25 && worldIn.getBlockState(newPos).getBlock() == ModBlocks.CLIFF_STONE) {
                        worldIn.setBlockState(newPos, block);
                    }
                }
            }
            if (worldIn.rand.nextFloat() > 0.975) {
                pos = pos.add(new BlockPos(ModRandom.randSign(), 0, 0));
            }
            if (worldIn.rand.nextFloat() > 0.975) {
                pos = pos.add(new BlockPos(0, 0, ModRandom.randSign()));
            }
        }
        return true;
    }
}
