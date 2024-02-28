package com.barribob.mm.entity.action;

import java.util.function.Supplier;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.projectile.ModProjectile;

import net.minecraft.world.entity.LivingEntity;

public class ActionGroundSlash implements IAction {
    public final Supplier<ModProjectile> supplier;

    public ActionGroundSlash(Supplier<ModProjectile> p) {
        supplier = p;
    }

    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        float inaccuracy = 0.0f;
        float speed = 0.8f;
        float pitch = 0; // Projectiles aim straight ahead always
        ModProjectile projectile = supplier.get();
        projectile.setPos(actor.getX(), actor.getY(), actor.getZ());
        projectile.shoot(actor, pitch, actor.getYRot(), 0.0F, speed, inaccuracy);
        projectile.setTravelRange(20f);
        actor.level.addFreshEntity(projectile);
    }
}
