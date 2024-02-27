package com.barribob.MaelstromMod.entity.entities.gauntlet;

import com.barribob.MaelstromMod.entity.entities.EntityLeveledMob;
import com.barribob.MaelstromMod.entity.projectile.Projectile;
import com.barribob.MaelstromMod.entity.util.IPitch;
import com.barribob.MaelstromMod.init.ModBBAnimations;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

public class FireballThrowAction<T extends EntityLeveledMob & IPitch> implements IGauntletAction {

    private final Function<LivingEntity, Vec3> target;
    private final Supplier<Projectile> projectileSupplier;
    private final T entity;

    public FireballThrowAction(Function<LivingEntity, Vec3> target, Supplier<Projectile> projectileSupplier, T entity) {
        this.target = target;
        this.projectileSupplier = projectileSupplier;
        this.entity = entity;
    }

    @Override
    public void doAction() {
        LivingEntity attackTarget = entity.getAttackTarget();
        if(attackTarget == null) return;

        ModBBAnimations.animation(entity, "gauntlet.fireball", false);
        Projectile proj = projectileSupplier.get();

        entity.addEvent(() -> entity.world.spawnEntity(proj), 10);

        // Hold the fireball in place
        for (int i = 10; i < 27; i++) {
            entity.addEvent(() -> {
                Vec3 fireballPos = entity.getPositionEyes(1).add(ModUtils.getAxisOffset(ModUtils.getLookVec(entity.getPitch(), entity.renderYawOffset), new Vec3(1, 0, 0)));
                ModUtils.setEntityPosition(proj, fireballPos);
            }, i);
        }

        // Throw the fireball
        entity.addEvent(() -> {
            Vec3 vel = target.apply(attackTarget).subtract(ModUtils.yVec(1)).subtract(proj.getPositionVector());
            proj.shoot(vel.x, vel.y, vel.z, 0.8f, 0.3f);
            ModUtils.addEntityVelocity(entity, vel.normalize().scale(-0.8));
        }, 27);
    }

    @Override
    public int attackLength() {
        return 52;
    }
}
