package com.barribob.mm.world.gen.maelstrom_castle;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.level.Level;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

import com.barribob.mm.entity.entities.EntityShade;
import com.barribob.mm.entity.tileentity.TileEntityMobSpawner;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.world.gen.WorldGenStructure;

/**
 * The world generator for the maelstrom castle, which just offsets it a bit, and replaces certain data blocks
 */
public class WorldGenMaelstromCastle extends WorldGenStructure {
    public WorldGenMaelstromCastle(String name) {
        super(name);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, Level worldIn, Random rand) {
        if (function.startsWith("chest")) {
            worldIn.setBlockToAir(pos);
            BlockPos blockpos = pos.down();

            BlockEntity tileentity = worldIn.getTileEntity(blockpos);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_STRONGHOLD_CORRIDOR, rand.nextLong());
            }
        } else if (function.startsWith("portal")) {
            worldIn.setBlockState(pos, ModBlocks.AZURE_PORTAL.getDefaultState(), 2);
        } else if (function.startsWith("enemy")) {
            worldIn.setBlockState(pos, ModBlocks.DISAPPEARING_SPAWNER.getDefaultState(), 2);
            BlockEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(ModEntities.getID(EntityShade.class), 1, 1.0f, 16);
            }

        }
    }
}
