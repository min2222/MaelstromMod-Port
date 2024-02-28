package com.barribob.mm.blocks;

import java.util.Random;

import com.barribob.mm.init.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

/**
 * Represents all of the possible ores in the azure dimension
 */
public class BlockAzureOre extends DropExperienceBlock {
    public BlockAzureOre(String name, float hardness, float resistance, SoundType soundType) {
        super(BlockBehaviour.Properties.of(Material.STONE).strength(hardness, resistance).sound(soundType).requiresCorrectToolForDrops());
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(BlockState state, RandomSource rand, int fortune) {
        if (this == ModBlocks.AZURE_COAL_ORE) {
            return Items.COAL;
        } else if (this == ModBlocks.AZURE_DIAMOND_ORE) {
            return Items.DIAMOND;
        } else if (this == ModBlocks.AZURE_LAPIS_ORE) {
            return Items.LAPIS_LAZULI;
        } else if (this == ModBlocks.AZURE_EMERALD_ORE) {
            return Items.EMERALD;
        }

        return Item.BY_BLOCK.get(this);
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader world, RandomSource rand, BlockPos pos, int fortune, int silktouch) {
        if (this.getItemDropped(state, rand, fortune) != Item.BY_BLOCK.get(this)) {
            int i = 0;

            if (this == ModBlocks.AZURE_COAL_ORE) {
                i = Mth.nextInt(rand, 0, 2);
            } else if (this == ModBlocks.AZURE_DIAMOND_ORE) {
                i = Mth.nextInt(rand, 3, 7);
            } else if (this == ModBlocks.AZURE_EMERALD_ORE) {
                i = Mth.nextInt(rand, 3, 7);
            } else if (this == ModBlocks.AZURE_LAPIS_ORE) {
                i = Mth.nextInt(rand, 2, 5);
            }

            return i;
        }
        return 0;
    }
    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random random) {
        return this == ModBlocks.AZURE_LAPIS_ORE ? 4 + random.nextInt(5) : 1;
    }
}
