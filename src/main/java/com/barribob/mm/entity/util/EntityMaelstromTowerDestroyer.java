package com.barribob.mm.entity.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.barribob.mm.Main;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.world.gen.WorldGenCustomStructures;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class EntityMaelstromTowerDestroyer extends Entity {
    List<Vec3> blocksToDestroy = new ArrayList<>();

    public EntityMaelstromTowerDestroyer(Level worldIn) {
        super(ModEntities.MAELSTROM_TOWER_DESTORYER.get(), worldIn);
        this.setNoGravity(true);
    }

    public EntityMaelstromTowerDestroyer(Level worldIn, Vec3 pos) {
        this(worldIn);
        ModUtils.setEntityPosition(this, pos);
        BlockPos towerSize = WorldGenCustomStructures.invasionTower.getSize(level);
        BlockPos max = towerSize.offset(new BlockPos(pos));
        BlockPos min = new BlockPos(pos);
        blocksToDestroy = ModUtils.cubePoints(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()).stream()
                .filter(vec3d -> worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == Blocks.OBSIDIAN ||
                        worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == ModBlocks.AZURE_MAELSTROM ||
                        worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == ModBlocks.MAELSTROM_BRICKS ||
                        worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == ModBlocks.MAELSTROM_BRICK_STAIRS ||
                        worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == ModBlocks.MAELSTROM_STONEBRICK ||
                        worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == ModBlocks.MAELSTROM_STONEBRICK_STAIRS ||
                        worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == ModBlocks.MAELSTROM_STONEBRICK_FENCE ||
                        worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == Blocks.NETHERRACK ||
                        worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == ModBlocks.CRIMSON_MAELSTROM_BRICKS ||
                        worldIn.getBlockState(new BlockPos(vec3d)).getBlock() == ModBlocks.BOSS_SPAWNER)
                .collect(Collectors.toList());
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            return;
        }

        int towerDisintegrationSpeed = Math.max(1, Main.invasionsConfig.getInt("tower_disintegration_speed"));
        for (int i = 0; i < towerDisintegrationSpeed; i++) {
            if (this.blocksToDestroy.size() == 0) {
                this.discard();
                return;
            }

            this.level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);

            int rand = this.random.nextInt(blocksToDestroy.size());
            BlockPos pos = new BlockPos(blocksToDestroy.get(rand));
            if (level.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, false) != null) {
                level.destroyBlock(pos, false);
            } else {
                level.setBlockToAir(pos);
            }
            blocksToDestroy.remove(rand);
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
    	return NetworkHooks.getEntitySpawningPacket(this);
    }
}
