package com.barribob.mm.entity.projectile;

import com.barribob.mm.entity.entities.EntityShade;
import com.barribob.mm.entity.tileentity.MobSpawnerLogic.MobSpawnData;
import com.barribob.mm.init.ModBlocks;
import com.barribob.mm.init.ModEntities;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.world.gen.WorldGenMaelstrom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ProjectileMaelstromMeteor extends ModProjectile {
    public ProjectileMaelstromMeteor(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
        this.setNoGravity(true);
    }

    public ProjectileMaelstromMeteor(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileMaelstromMeteor(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        this.playSound(SoundEvents.FIRE_EXTINGUISH, 0.5f, ModRandom.getFloat(0.2f) + 1.0f);
        if (this.tickCount > 400) {
            this.discard();
        }
        super.tick();
    }

    @Override
    protected void spawnParticles() {
        float size = 0.25f;
        ModUtils.performNTimes(10, (i) -> {
            ParticleManager.spawnMaelstromLargeSmoke(level, random, position().add(ModRandom.randVec().scale(size)));
        });
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        // Go through entities
        if (result.getEntity() != null) {
            return;
        }

        if (!level.isClientSide) {
            new WorldGenMaelstrom(ModBlocks.DECAYING_MAELSTROM, ModBlocks.MAELSTROM_CORE, (tileEntity) -> tileEntity.getSpawnerBaseLogic().setData(
                    new MobSpawnData(ModEntities.getID(EntityShade.class), Element.NONE),
                    2,
                    LevelHandler.INVASION,
                    16))
                    .generate(level, random, this.blockPosition());
        }
        super.onHitEntity(result);
    }
}
