package com.barribob.mm.blocks;

import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.level.Level;

import java.util.Random;

import com.barribob.mm.Main;
import com.barribob.mm.entity.tileentity.TileEntityMalestromSpawner;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.IHasModel;

/**
 * The maelstrom mob spawner. Also prevent decay of the maelstrom block
 */
public class BlockMaelstromCore extends BaseContainerBlockEntity implements IHasModel {
    private Item itemDropped;

    public BlockMaelstromCore(String name, Material material, Item itemDropped) {
        super(material);
        setUnlocalizedName(name);
        setRegistryName(name);

        // Add both an item as a block and the block itself
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new BlockItem(this).setRegistryName(this.getRegistryName()));
        this.hasTileEntity = true;
        this.itemDropped = itemDropped;
    }

    public BlockMaelstromCore(String name, Material material, float hardness, float resistance, SoundType soundType, Item itemDropped) {
        this(name, material, itemDropped);
        setHardness(hardness);
        setResistance(resistance);
        setSoundType(soundType);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }

    /**
     * Tile Entity methods to make the tile entity spawner work
     */
    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public BlockEntity createNewTileEntity(Level worldIn, int meta) {
        return new TileEntityMalestromSpawner();
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model,
     * MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to
     * skip all rendering
     */
    @Override
    public EnumBlockRenderType getRenderType(BlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return itemDropped;
    }
}
