package com.barribob.MaelstromMod.entity.action;

import com.barribob.MaelstromMod.entity.entities.EntityLeveledMob;
import com.barribob.MaelstromMod.entity.projectile.Projectile;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ActionRayAttack implements IAction {
    Supplier<Projectile> projectileSupplier;
    float velocity;

    public ActionRayAttack(Supplier<Projectile> projectileSupplier, float velocity) {
        this.projectileSupplier = projectileSupplier;
        this.velocity = velocity;
    }

    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        Vec3 targetPos = target.getPositionEyes(1);
        Vec3 fromTargetToActor = actor.getPositionVector().subtract(targetPos);

        Vec3 lineDirection = ModUtils.rotateVector2(
                fromTargetToActor.crossProduct(ModUtils.Y_AXIS),
                fromTargetToActor,
                ModRandom.range(0, 180))
                .normalize()
                .scale(6);

        Vec3 lineStart = targetPos.subtract(lineDirection);
        Vec3 lineEnd = targetPos.add(lineDirection);

        ModUtils.lineCallback(lineStart, lineEnd, 10, (pos, i) -> {
            Projectile projectile = projectileSupplier.get();
            projectile.setTravelRange(30);
            projectile.setNoGravity(true);

            ModUtils.throwProjectile(actor, pos, projectile, 0, velocity);
        });
    }
}
