package com.barribob.mm.entity.action;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

public class ActionRayAttack implements IAction {
    Supplier<ModProjectile> projectileSupplier;
    float velocity;

    public ActionRayAttack(Supplier<ModProjectile> projectileSupplier, float velocity) {
        this.projectileSupplier = projectileSupplier;
        this.velocity = velocity;
    }

    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        Vec3 targetPos = target.getEyePosition(1);
        Vec3 fromTargetToActor = actor.position().subtract(targetPos);

        Vec3 lineDirection = ModUtils.rotateVector2(
                fromTargetToActor.cross(ModUtils.Y_AXIS),
                fromTargetToActor,
                ModRandom.range(0, 180))
                .normalize()
                .scale(6);

        Vec3 lineStart = targetPos.subtract(lineDirection);
        Vec3 lineEnd = targetPos.add(lineDirection);

        ModUtils.lineCallback(lineStart, lineEnd, 10, (pos, i) -> {
            ModProjectile projectile = projectileSupplier.get();
            projectile.setTravelRange(30);
            projectile.setNoGravity(true);

            ModUtils.throwProjectile(actor, pos, projectile, 0, velocity);
        });
    }
}
