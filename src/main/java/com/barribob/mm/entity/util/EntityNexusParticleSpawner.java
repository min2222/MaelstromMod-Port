package com.barribob.mm.entity.util;

import net.minecraft.world.entity.Entity;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityNexusParticleSpawner extends Entity {
    public EntityNexusParticleSpawner(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
        this.setSize(0.1f, 0.1f);
    }

    public EntityNexusParticleSpawner(Level worldIn, float x, float y, float z) {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    @Override
    public void tick() {
        super.onUpdate();
        if (this.tickCount % 10 == 0) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }
        if (this.tickCount > 600) {
            this.setDead();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            ModUtils.performNTimes(20, (i) -> {
                ModUtils.circleCallback(i * 2, 600 - this.tickCount, (pos) -> {
                    pos = pos.scale(1.0f + ModRandom.getFloat(0.03f));
                    ParticleManager.spawnEffect(world, new Vec3(pos.x, i * 5, pos.y).add(position()), ModColors.WHITE);
                });
            });
        }
        super.handleEntityEvent(id);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
    }

    @Override
    protected void writeEntityToNBT(CompoundTag compound) {
    }

    @Override
    protected void entityInit() {
    }
}
