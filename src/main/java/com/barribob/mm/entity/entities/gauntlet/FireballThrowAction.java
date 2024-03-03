package com.barribob.mm.entity.entities.gauntlet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.util.IPitch;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.util.ModUtils;

public class FireballThrowAction<T extends EntityLeveledMob & IPitch> implements IGauntletAction {

    private final Function<LivingEntity, Vec3> target;
    private final Supplier<ModProjectile> projectileSupplier;
    private final T entity;

    public FireballThrowAction(Function<LivingEntity, Vec3> target, Supplier<ModProjectile> projectileSupplier, T entity) {
        this.target = target;
        this.projectileSupplier = projectileSupplier;
        this.entity = entity;
    }

    @Override
    public void doAction() {
        LivingEntity attackTarget = entity.getTarget();
        if(attackTarget == null) return;

        ModBBAnimations.animation(entity, "gauntlet.fireball", false);
        ModProjectile proj = projectileSupplier.get();

        entity.addEvent(() -> entity.level.addFreshEntity(proj), 10);

        // Hold the fireball in place
        for (int i = 10; i < 27; i++) {
            entity.addEvent(() -> {
                Vec3 fireballPos = entity.getEyePosition(1).add(ModUtils.getAxisOffset(ModUtils.getLookVec(entity.getPitch(), entity.yBodyRot), new Vec3(1, 0, 0)));
                ModUtils.setEntityPosition(proj, fireballPos);
            }, i);
        }

        // Throw the fireball
        entity.addEvent(() -> {
            Vec3 vel = target.apply(attackTarget).subtract(ModUtils.yVec(1)).subtract(proj.position());
            proj.shoot(vel.x, vel.y, vel.z, 0.8f, 0.3f);
            ModUtils.addEntityVelocity(entity, vel.normalize().scale(-0.8));
        }, 27);
    }

    @Override
    public int attackLength() {
        return 52;
    }
}
