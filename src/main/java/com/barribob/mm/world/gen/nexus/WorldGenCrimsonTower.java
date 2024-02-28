package com.barribob.mm.world.gen.nexus;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

import com.barribob.mm.entity.entities.EntityChaosKnight;
import com.barribob.mm.entity.entities.EntityIronShade;
import com.barribob.mm.entity.entities.EntityShade;
import com.barribob.mm.entity.tileentity.TileEntityMobSpawner;
import com.barribob.mm.entity.tileentity.MobSpawnerLogic.MobSpawnData;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.world.gen.WorldGenStructure;

public class WorldGenCrimsonTower extends WorldGenStructure {
    public WorldGenCrimsonTower() {
        super("nexus/crimson_tower");
    }

    @Override
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        this.generateStructure(worldIn, position, Rotation.CLOCKWISE_180);
        return true;
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, Level worldIn, Random rand) {
        worldIn.setBlockToAir(pos);
        if (function.startsWith("chest")) {
            worldIn.setBlockToAir(pos);
            BlockPos blockpos = pos.down();

            BlockEntity tileentity = worldIn.getTileEntity(blockpos);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setLootTable(LootTableList.CHESTS_STRONGHOLD_LIBRARY, rand.nextLong());
            }
        } else if (function.startsWith("scout")) {
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState());
            BlockEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(
                        new MobSpawnData[]{
                                new MobSpawnData(ModEntities.getID(EntityShade.class), new Element[]{Element.CRIMSON, Element.NONE}, new int[]{1, 3}, 1)
                        },
                        new int[]{1},
                        2,
                        LevelHandler.CRIMSON_START,
                        15);
            }
        } else if (function.startsWith("exe")) {
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState());
            BlockEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(
                        new MobSpawnData[]{
                                new MobSpawnData(ModEntities.getID(EntityIronShade.class), new Element[]{Element.CRIMSON}, new int[]{1}, 1)
                        },
                        new int[]{1},
                        1,
                        LevelHandler.CLIFF_ENDGAME,
                        8);
            }
        } else if (function.startsWith("boss")) {
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState());
            BlockEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(
                        new MobSpawnData[]{
                                new MobSpawnData(ModEntities.getID(EntityChaosKnight.class), new Element[]{Element.CRIMSON}, new int[]{1}, 1)
                        },
                        new int[]{1},
                        1,
                        LevelHandler.CLIFF_ENDGAME,
                        20);
            }
        }
    }
}
