package com.barribob.MaelstromMod.world.dimension.crimson_kingdom;

import com.barribob.MaelstromMod.init.ModItems;
import com.barribob.MaelstromMod.util.handlers.LootTableHandler;
import com.barribob.MaelstromMod.world.gen.WorldGenStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.world.level.Level;

import java.util.Random;

public class WorldGenGauntletSpike extends WorldGenStructure {

    public WorldGenGauntletSpike() {
        super("crimson_kingdom/gauntlet_spike");
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, Level worldIn, Random rand) {
        super.handleDataMarker(function, pos, worldIn, rand);
        worldIn.setBlockToAir(pos);
        if (function.startsWith("chest")) {
            BlockEntity tileentity = worldIn.getTileEntity(pos.down());

            if (tileentity instanceof TileEntityLockableLoot) {
                ((TileEntityLockableLoot) tileentity).setLootTable(LootTableHandler.GAUNTLET_CHEST, rand.nextLong());
            }
        } else if (function.startsWith("boss chest")) {
            BlockEntity tileentity = worldIn.getTileEntity(pos.down());

            if (tileentity instanceof TileEntityLockableLoot) {
                ((TileEntityLockableLoot) tileentity).setInventorySlotContents(13, new ItemStack(ModItems.MAELSTROM_KEY_FRAGMENT));
            }
        }

    }
}
