package com.barribob.mm.entity.action;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.util.ModUtils;

public class ActionRingAttack implements IAction {
    Supplier<ModProjectile> projectileSupplier;

    public ActionRingAttack(Supplier<ModProjectile> projectileSupplier) {
        this.projectileSupplier = projectileSupplier;
    }

    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        Vec3 direction = target.getEyePosition(1).subtract(actor.position());
        float f1 = (float) Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        ModUtils.circleCallback(4, 12, (pos) -> {
            Vec3 rotatedPos = pos.xRot((float) (Mth.atan2(direction.y, f1))).yRot((float) (Mth.atan2(direction.x, direction.z)));
            ModProjectile projectile = projectileSupplier.get();
            ModUtils.setEntityPosition(projectile, rotatedPos.add(actor.position()));
            actor.level.addFreshEntity(projectile);
            actor.addEvent(() -> {
                ModUtils.throwProjectileNoSpawn(target.getEyePosition(1), projectile, 0, 0.4f);
            }, 10);
        });
    }
}
