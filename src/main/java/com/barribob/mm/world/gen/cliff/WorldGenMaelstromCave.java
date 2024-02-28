package com.barribob.mm.world.gen.cliff;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Random;

import com.barribob.mm.entity.entities.EntityMaelstromMage;
import com.barribob.mm.entity.tileentity.TileEntityMobSpawner;
import com.barribob.mm.entity.tileentity.MobSpawnerLogic.MobSpawnData;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.util.handlers.LootTableHandler;

public class WorldGenMaelstromCave extends WorldGenCliffLedge {
    public WorldGenMaelstromCave() {
        super("cliff/maelstrom_cave", -6);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, Level worldIn, Random rand) {
        if (function.startsWith("enemy")) {
            worldIn.setBlockState(pos, ModBlocks.DISAPPEARING_SPAWNER.getDefaultState(), 2);
            BlockEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(
                        new MobSpawnData[]{
                                new MobSpawnData(ModEntities.getID(EntityMaelstromMage.class), new Element[]{Element.NONE, Element.GOLDEN}, new int[]{4, 1}, 1)
                        },
                        new int[]{1},
                        3,
                        LevelHandler.CLIFF_OVERWORLD,
                        16);
            }
        } else if (function.startsWith("chest")) {
            worldIn.setBlockToAir(pos);
            pos = pos.down();
            BlockEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setLootTable(LootTableHandler.MAELSTROM_RUINS, rand.nextLong());
            }
        } else {
            if (isBlockNearby(worldIn, pos)) {
                worldIn.setBlockState(pos, ModBlocks.AZURE_MAELSTROM.getDefaultState());
            } else {
                worldIn.setBlockToAir(pos);
            }
        }
    }

    ;

    private boolean isBlockNearby(Level world, BlockPos pos) {
        for (BlockPos dir : Arrays.asList(pos.up(), pos.east(), pos.west(), pos.north(), pos.south(), pos.down())) {
            if (world.getBlockState(dir).getBlock() == ModBlocks.CLIFF_STONE) {
                return true;
            }
        }
        return false;
    }
}
