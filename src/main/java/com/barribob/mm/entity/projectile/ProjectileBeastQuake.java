package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileBeastQuake extends ProjectileQuake {
    public ProjectileBeastQuake(Level worldIn, LivingEntity throwerIn, float baseDamage) {
        super(worldIn, throwerIn, baseDamage, null);
        this.updates = 10;
    }

    public ProjectileBeastQuake(Level worldIn) {
        super(worldIn);
    }

    public ProjectileBeastQuake(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        for(int j = 0; j < 10; j++) {
            int randHeight = ModRandom.range(4, 6);
            for (int i = 0; i < randHeight; i++) {
                ParticleManager.spawnDust(level,
                        position()
                                .add(ModUtils.Y_AXIS.scale(i * 0.5))
                                .add(ModRandom.randVec().scale(0.5))
                                .add(ModUtils.planeProject(ModUtils.getEntityVelocity(this), ModUtils.Y_AXIS).scale(2)),
                        ModColors.MAELSTROM,
                        Vec3.ZERO,
                        ModRandom.range(10, 15));
            }
        }
    }

    @Override
    protected void onQuakeUpdate() {
        if(this.shootingEntity != null && !level.isClientSide) {
            DamageSource source = ModDamageSource.builder()
                    .directEntity(this)
                    .indirectEntity(shootingEntity)
                    .type(ModDamageSource.MOB)
                    .stoppedByArmorNotShields()
                    .element(getElement())
                    .build();

            for(int i = 0; i < 4; i++) {
                ModUtils.handleAreaImpact(0.5f,
                        e -> this.getDamage(),
                        this.shootingEntity,
                        position().add(ModUtils.Y_AXIS.scale(i * 0.5f)),
                        source,
                        0.25f, 0, false);
            }
        }
    }
}
