package com.barribob.mm.world.dimension.azure_dimension;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;

import com.barribob.mm.init.ModBlocks;

import net.minecraft.world.gen.MapGenCaves;

public class MapGenAzureCaves extends MapGenCaves {
    @Override
    protected boolean canReplaceBlock(BlockState state, BlockState above) {
        if (above.getMaterial().equals(Material.WATER)) {
            return false;
        }
        Block block = state.getBlock();
        if (block.equals(ModBlocks.DARK_AZURE_STONE) || block.equals(ModBlocks.DARK_AZURE_STONE_1) || block.equals(ModBlocks.DARK_AZURE_STONE_2)
                || block.equals(ModBlocks.DARK_AZURE_STONE_3) || block.equals(ModBlocks.DARK_AZURE_STONE_4) || block.equals(ModBlocks.DARK_AZURE_STONE_5)
                || block.equals(Blocks.PRISMARINE)) {
            return true;
        }

        return super.canReplaceBlock(state, above);
    }
}
