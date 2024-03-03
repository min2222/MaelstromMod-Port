package com.barribob.mm.entity.util;

import com.barribob.mm.init.ModEntities;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.world.gen.WorldGenCustomStructures;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class EntityCrimsonTowerSpawner extends Entity {
    public EntityCrimsonTowerSpawner(Level worldIn) {
        super(ModEntities.CRIMSON_TOWER_SPAWNER.get(), worldIn);
        this.setNoGravity(true);
    }

    public EntityCrimsonTowerSpawner(Level worldIn, float x, float y, float z) {
        this(worldIn);
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();

        if (level.isClientSide) {
            return;
        }

        this.level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);

        if (this.tickCount == 160) {
            level.explode(this, getX(), getY(), getZ(), 2, BlockInteraction.DESTROY);
        } else if (this.tickCount == 200) {
            level.explode(this, getX(), getY() - 1, getZ(), 3, BlockInteraction.DESTROY);
        } else if (this.tickCount == 250) {
            level.explode(this, getX(), getY() - 3, getZ(), 4, BlockInteraction.DESTROY);
        } else if (this.tickCount > 300 && this.tickCount < 390 && this.tickCount % 5 == 0) {
            level.explode(this, getX() + ModRandom.getFloat(5), getY() + ModRandom.getFloat(10), getZ() + ModRandom.getFloat(5), 4, BlockInteraction.DESTROY);
        } else if (this.tickCount == 400) {
            WorldGenCustomStructures.CRIMSON_TOWER.generate(level, random, this.blockPosition().offset(-30, 0, -30));
            this.discard();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            this.spawnParticles();
        }
        super.handleEntityEvent(id);
    }

    protected void spawnParticles() {
        Vec3 color = this.tickCount > 100 ? ModColors.MAELSTROM : ModColors.RED;
        int offset = 0;
        int sectors = 90;
        int degreesPerSector = 360 / sectors;
        double size = 3;
        for (int i = 0; i < sectors; i++) {
            double x = this.getX() + 0.5 + Math.cos(i * degreesPerSector) * Math.sin(this.tickCount) * size + offset;
            double y = this.getY() + 3.5 + Math.sin(i * degreesPerSector) * Math.cos(this.tickCount) * size + offset;
            double z = this.getZ() + 0.5 + Math.cos(i * degreesPerSector) * Math.sin(this.tickCount) * size + offset;
            ParticleManager.spawnEffect(level, new Vec3(x, y, this.getZ() + 0.5), color);
            ParticleManager.spawnEffect(level, new Vec3(this.getX() + 0.5, y, z), color);
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
