package com.barribob.MaelstromMod.entity.util;

import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.entity.Entity;
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
    public void onUpdate() {
        super.onUpdate();
        if (this.ticksExisted % 10 == 0) {
            world.setEntityState(this, ModUtils.PARTICLE_BYTE);
        }
        if (this.ticksExisted > 600) {
            this.setDead();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            ModUtils.performNTimes(20, (i) -> {
                ModUtils.circleCallback(i * 2, 600 - this.ticksExisted, (pos) -> {
                    pos = pos.scale(1.0f + ModRandom.getFloat(0.03f));
                    ParticleManager.spawnEffect(world, new Vec3(pos.x, i * 5, pos.y).add(getPositionVector()), ModColors.WHITE);
                });
            });
        }
        super.handleStatusUpdate(id);
    }

    @Override
    protected void readEntityFromNBT(CompoundTag compound) {
    }

    @Override
    protected void writeEntityToNBT(CompoundTag compound) {
    }

    @Override
    protected void entityInit() {
    }
}
