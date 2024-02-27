package com.barribob.MaelstromMod.blocks;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.init.ModBlocks;
import com.barribob.MaelstromMod.init.ModItems;
import com.barribob.MaelstromMod.util.IHasModel;
import net.minecraft.block.BlockOre;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Represents all of the possible ores in the azure dimension
 */
public class BlockAzureOre extends BlockOre implements IHasModel {
    public BlockAzureOre(String name) {
        super();
        setUnlocalizedName(name);
        setRegistryName(name);

        // Add both an item as a block and the block itself
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new BlockItem(this).setRegistryName(this.getRegistryName()));
    }

    public BlockAzureOre(String name, float hardness, float resistance, SoundType soundType) {
        this(name);
        setHardness(hardness);
        setResistance(resistance);
        this.setSoundType(soundType);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        if (this == ModBlocks.AZURE_COAL_ORE) {
            return Items.COAL;
        } else if (this == ModBlocks.AZURE_DIAMOND_ORE) {
            return Items.DIAMOND;
        } else if (this == ModBlocks.AZURE_LAPIS_ORE) {
            return Items.DYE;
        } else if (this == ModBlocks.AZURE_EMERALD_ORE) {
            return Items.EMERALD;
        }

        return Item.getItemFromBlock(this);
    }

    @Override
    public int getExpDrop(BlockState state, BlockGetter world, BlockPos pos, int fortune) {
        Random rand = world instanceof Level ? ((Level) world).rand : new Random();
        if (this.getItemDropped(state, rand, fortune) != Item.getItemFromBlock(this)) {
            int i = 0;

            if (this == ModBlocks.AZURE_COAL_ORE) {
                i = Mth.getInt(rand, 0, 2);
            } else if (this == ModBlocks.AZURE_DIAMOND_ORE) {
                i = Mth.getInt(rand, 3, 7);
            } else if (this == ModBlocks.AZURE_EMERALD_ORE) {
                i = Mth.getInt(rand, 3, 7);
            } else if (this == ModBlocks.AZURE_LAPIS_ORE) {
                i = Mth.getInt(rand, 2, 5);
            }

            return i;
        }
        return 0;
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    @Override
    public int damageDropped(BlockState state) {
        return this == ModBlocks.AZURE_LAPIS_ORE ? DyeColor.BLUE.getDyeDamage() : 0;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random random) {
        return this == ModBlocks.AZURE_LAPIS_ORE ? 4 + random.nextInt(5) : 1;
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}
