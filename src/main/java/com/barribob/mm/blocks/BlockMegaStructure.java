package com.barribob.mm.blocks;

import net.minecraft.world.level.block.StructureBlock;

import com.barribob.mm.Main;
import com.barribob.mm.entity.tileentity.TileEntityMegaStructure;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.IHasModel;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;

/**
 * A structure block that lets me save structures larger than 32 x 32 x 32
 * It is hardcoded at 100 x 100 x 100 because trying to make it work properly is
 * not worth the effort for what I'm using it for.
 */
public class BlockMegaStructure extends StructureBlock implements IHasModel {
    public BlockMegaStructure(String name) {
        setUnlocalizedName(name);
        setRegistryName(name);

        // Add both an item as a block and the block itself
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new BlockItem(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public BlockEntity createNewTileEntity(Level worldIn, int meta) {
        return new TileEntityMegaStructure();
    }
}
