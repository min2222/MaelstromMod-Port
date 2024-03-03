package com.barribob.mm.entity.entities.gauntlet;

import java.util.function.Supplier;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PunchAction implements IGauntletAction {

    private final String animation;
    private final Supplier<Vec3> targetSupplier;
    private final Runnable whilePunching;
    private final EntityLeveledMob entity;
    private final Entity fistHitbox;
    private boolean isPunching;
    public static final int punchStopTime = 50;

    public PunchAction (String animation, Supplier<Vec3> targetSupplier, Runnable whilePunching, EntityLeveledMob entity, Entity fistHitbox) {
        this.animation = animation;
        this.targetSupplier = targetSupplier;
        this.whilePunching = whilePunching;
        this.entity = entity;
        this.fistHitbox = fistHitbox;
    }

    @Override
    public void doAction() {
        ModBBAnimations.animation(entity, animation, false);
        entity.push(0, 0.5, 0);
        entity.addEvent(() -> {
            Vec3 target = targetSupplier.get();
            if (target == null) return;
            isPunching = true;
            fistHitbox.width = 2.5f;
            fistHitbox.height = 4.5f;
            entity.height = 2;
            for (int i = 0; i < 10; i++) {
                entity.addEvent(() -> {
                    Vec3 dir = ModUtils.direction(entity.getEyePosition(1), target);
                    Vec3 velocityCorrection = ModUtils.planeProject(ModUtils.getEntityVelocity(entity), dir);
                    ModUtils.addEntityVelocity(entity, dir.subtract(velocityCorrection).scale(0.32));
                }, i);
            }
        }, 16);
        entity.addEvent(() -> isPunching = false, 40);
        entity.addEvent(() -> {
            fistHitbox.width = 0;
            fistHitbox.height = 0;
            entity.height = 4;
        }, punchStopTime);
    }

    @Override
    public void update() {
        if (this.isPunching) {
            double vel = ModUtils.getEntityVelocity(entity).length();
            AABB box = entity.getBoundingBox().inflate(0.3, 0.3, 0.3);
            ModUtils.destroyBlocksInAABB(box, entity.level, entity);
            float punchDamage = entity.getAttack() * (float) vel * entity.getConfigFloat("punch_damage");

            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.MOB)
                    .directEntity(entity)
                    .stoppedByArmorNotShields()
                    .element(entity.getElement()).build();

            ModUtils.handleAreaImpact(1.3f, (e) -> punchDamage, entity,
                    entity.getEyePosition(1), source, (float) vel, 0, false);

            whilePunching.run();
        }
    }

    @Override
    public boolean shouldExplodeUponImpact() {
        return isPunching;
    }

    @Override
    public boolean isImmuneToDamage() {
        return isPunching;
    }

    @Override
    public int attackLength() {
        return 53;
    }
}
