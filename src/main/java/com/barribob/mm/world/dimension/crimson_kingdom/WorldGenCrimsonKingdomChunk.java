package com.barribob.mm.world.dimension.crimson_kingdom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.world.level.Level;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

import com.barribob.mm.entity.entities.*;
import com.barribob.mm.entity.entities.gauntlet.EntityMaelstromGauntlet;
import com.barribob.mm.entity.tileentity.TileEntityMobSpawner;
import com.barribob.mm.entity.tileentity.MobSpawnerLogic.MobSpawnData;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.util.handlers.LootTableHandler;
import com.barribob.mm.world.gen.WorldGenStructure;

public class WorldGenCrimsonKingdomChunk extends WorldGenStructure {

    public WorldGenCrimsonKingdomChunk(int x, int z) {
        super("crimson_kingdom/crimson_kingdom_" + x + "_" + z);
    }

    @Override
    public boolean generate(Level worldIn, Random rand, BlockPos position) {
        generateStructure(worldIn, position, Rotation.NONE);
        return true;
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, Level worldIn, Random rand) {
        super.handleDataMarker(function, pos, worldIn, rand);
        int spawnRange = 25;
        worldIn.setBlockToAir(pos);
        if (function.startsWith("enemy 4") || function.startsWith("enemy 5") | function.startsWith("enemy 6")) {
            int level = ModUtils.tryParseInt(function.split(" ")[1], 5);
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState(), 2);
            BlockEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(
                        new MobSpawnData[]{
                                new MobSpawnData(ModEntities.getID(EntityShade.class), new Element[]{Element.CRIMSON, Element.NONE}, new int[]{1, 3}, 1),
                                new MobSpawnData(ModEntities.getID(EntityMaelstromLancer.class), new Element[]{Element.CRIMSON, Element.NONE}, new int[]{1, 4}, 1),
                                new MobSpawnData(ModEntities.getID(EntityMaelstromMage.class), new Element[]{Element.CRIMSON, Element.NONE}, new int[]{1, 4}, 1)
                        },
                        new int[]{2, 2, 2},
                        ModRandom.range(2, 4),
                        level,
                        spawnRange);
            }
        } else if (function.startsWith("ranger 5") || function.startsWith("ranger 6")) {
            int level = ModUtils.tryParseInt(function.split(" ")[1], 5);
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState(), 2);
            BlockEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(
                        new MobSpawnData(ModEntities.getID(EntityMaelstromMage.class), new Element[]{Element.CRIMSON, Element.NONE}, new int[]{1, 2}, 1),
                        1,
                        level,
                        spawnRange);
            }
        } else if (function.startsWith("miniboss")) {
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState(), 2);
            BlockEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(
                        new MobSpawnData(ModEntities.getID(EntityIronShade.class), Element.CRIMSON),
                        1,
                        LevelHandler.CRIMSON_END,
                        spawnRange);
            }
        } else if (function.startsWith("beast")) {
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState(), 2);
            BlockEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(
                        new MobSpawnData(ModEntities.getID(EntityBeast.class), Element.CRIMSON),
                        1,
                        LevelHandler.CRIMSON_END,
                        spawnRange);
            }
        } else if (function.startsWith("healer")) {
            int level = ModUtils.tryParseInt(function.split(" ")[1], 5);
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState(), 2);
            BlockEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(
                        new MobSpawnData(ModEntities.getID(EntityMaelstromHealer.class), Element.NONE),
                        1,
                        level,
                        spawnRange);
            }
        } else if (function.startsWith("chest minecart")) {
            BlockEntity tileentity = worldIn.getTileEntity(pos.down());

            if (tileentity instanceof TileEntityLockableLoot) {
                ((TileEntityLockableLoot) tileentity).setLootTable(LootTableList.CHESTS_ABANDONED_MINESHAFT, rand.nextLong());
            }
        } else if (function.startsWith("garbage")) {
            BlockEntity tileentity = worldIn.getTileEntity(pos.down());

            if (tileentity instanceof TileEntityLockableLoot) {
                ((TileEntityLockableLoot) tileentity).setLootTable(LootTableList.CHESTS_SPAWN_BONUS_CHEST, rand.nextLong());
            }
        } else if (function.startsWith("chest")) {
            int level = ModUtils.tryParseInt(function.split(" ")[1], 5);
            ResourceLocation loot = level == 5 ? LootTableHandler.CRIMSON_5_CHEST : LootTableHandler.CRIMSON_6_CHEST;
            BlockEntity tileentity = worldIn.getTileEntity(pos.down());

            if (tileentity instanceof TileEntityLockableLoot) {
                ((TileEntityLockableLoot) tileentity).setLootTable(loot, rand.nextLong());
            }
        } else if (function.startsWith("artifact 1")) {
            BlockEntity tileentity = worldIn.getTileEntity(pos.down());

            if (tileentity instanceof TileEntityLockableLoot) {
                // 13 is the middle of the shulker box
                ((TileEntityLockableLoot) tileentity).setInventorySlotContents(13, new ItemStack(ModItems.ENERGIZED_CADUCEUS));
            }
        } else if (function.startsWith("artifact 2")) {
            BlockEntity tileentity = worldIn.getTileEntity(pos.down());

            if (tileentity instanceof TileEntityLockableLoot) {
                ((TileEntityLockableLoot) tileentity).setInventorySlotContents(13, new ItemStack(ModItems.ELYSIUM_WINGS));
            }
        } else if (function.startsWith("artifact 3")) {
            BlockEntity tileentity = worldIn.getTileEntity(pos.down());

            if (tileentity instanceof TileEntityLockableLoot) {
                ((TileEntityLockableLoot) tileentity).setInventorySlotContents(13, new ItemStack(ModItems.TUNING_FORK));
            }
        } else if (function.startsWith("boss")) {
            worldIn.setBlockState(pos, ModBlocks.BOSS_SPAWNER.getDefaultState(), 2);
            BlockEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityMobSpawner) {
                CompoundTag compound = new CompoundTag();
                compound.setString("id", ModEntities.getID(EntityMaelstromGauntlet.class));
                compound.setBoolean("isImmovable", true);

                ((TileEntityMobSpawner) tileentity).getSpawnerBaseLogic().setData(new MobSpawnData(compound), 1, LevelHandler.CRIMSON_END, 60);
            }
        }
    }
}