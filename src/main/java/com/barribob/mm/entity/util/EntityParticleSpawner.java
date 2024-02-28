package com.barribob.mm.entity.util;

import com.barribob.mm.util.ModUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A class that exists to spawn particles. It is to circumvent the less flexible
 * enum particle spawning method.
 */
public abstract class EntityParticleSpawner extends Entity {
    private boolean spawnedParticles = false;

    public EntityParticleSpawner(Level worldIn) {
        super(worldIn);
    }

    @Override
    public void tick() {
        super.onUpdate();
        level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        this.setDead();
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
    protected void entityInit() {
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void writeEntityToNBT(CompoundTag compound) {
    }
}
