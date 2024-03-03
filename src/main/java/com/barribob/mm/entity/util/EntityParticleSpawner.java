package com.barribob.mm.entity.util;

import com.barribob.mm.util.ModUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

/**
 * A class that exists to spawn particles. It is to circumvent the less flexible
 * enum particle spawning method.
 */
public abstract class EntityParticleSpawner extends Entity {
    private boolean spawnedParticles = false;

    public EntityParticleSpawner(EntityType<? extends EntityParticleSpawner> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        this.discard();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            spawnParticles();
        }
        super.handleEntityEvent(id);
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract void spawnParticles();

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
    
    @Override
    public Packet<?> getAddEntityPacket() {
    	return NetworkHooks.getEntitySpawningPacket(this);
    }
}
