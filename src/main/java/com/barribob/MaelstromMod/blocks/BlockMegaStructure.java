package com.barribob.MaelstromMod.blocks;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.entity.tileentity.TileEntityMegaStructure;
import com.barribob.MaelstromMod.init.ModBlocks;
import com.barribob.MaelstromMod.init.ModItems;
import com.barribob.MaelstromMod.util.IHasModel;
import net.minecraft.world.level.block.StructureBlock;
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
