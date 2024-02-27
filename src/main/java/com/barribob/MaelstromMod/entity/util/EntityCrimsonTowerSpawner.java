package com.barribob.MaelstromMod.entity.util;

import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import com.barribob.MaelstromMod.world.gen.WorldGenCustomStructures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityCrimsonTowerSpawner extends Entity {
    public EntityCrimsonTowerSpawner(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public EntityCrimsonTowerSpawner(Level worldIn, float x, float y, float z) {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (world.isRemote) {
            return;
        }

        this.world.setEntityState(this, ModUtils.PARTICLE_BYTE);

        if (this.ticksExisted == 160) {
            world.createExplosion(this, posX, posY, posZ, 2, true);
        } else if (this.ticksExisted == 200) {
            world.createExplosion(this, posX, posY - 1, posZ, 3, true);
        } else if (this.ticksExisted == 250) {
            world.createExplosion(this, posX, posY - 3, posZ, 4, true);
        } else if (this.ticksExisted > 300 && this.ticksExisted < 390 && this.ticksExisted % 5 == 0) {
            world.createExplosion(this, posX + ModRandom.getFloat(5), posY + ModRandom.getFloat(10), posZ + ModRandom.getFloat(5), 4, true);
        } else if (this.ticksExisted == 400) {
            WorldGenCustomStructures.CRIMSON_TOWER.generate(world, rand, this.getPosition().add(-30, 0, -30));
            this.setDead();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            this.spawnParticles();
        }
        super.handleStatusUpdate(id);
    }

    protected void spawnParticles() {
        Vec3 color = this.ticksExisted > 100 ? ModColors.MAELSTROM : ModColors.RED;
        int offset = 0;
        int sectors = 90;
        int degreesPerSector = 360 / sectors;
        double size = 3;
        for (int i = 0; i < sectors; i++) {
            double x = this.posX + 0.5 + Math.cos(i * degreesPerSector) * Math.sin(this.ticksExisted) * size + offset;
            double y = this.posY + 3.5 + Math.sin(i * degreesPerSector) * Math.cos(this.ticksExisted) * size + offset;
            double z = this.posZ + 0.5 + Math.cos(i * degreesPerSector) * Math.sin(this.ticksExisted) * size + offset;
            ParticleManager.spawnEffect(world, new Vec3(x, y, this.posZ + 0.5), color);
            ParticleManager.spawnEffect(world, new Vec3(this.posX + 0.5, y, z), color);
        }
    }

    @Override
    protected void entityInit() {
    }

    @Override
    protected void readEntityFromNBT(CompoundTag compound) {
    }

    @Override
    protected void writeEntityToNBT(CompoundTag compound) {
    }
}
