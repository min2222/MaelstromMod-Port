package com.barribob.mm.entity.action;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.projectile.ProjectileBlackFireball;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class ActionFireball implements IAction {
    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        actor.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.4F / (actor.level.random.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 2.0f;
        float velocity = 0.5f;

        ProjectileBlackFireball projectile = new ProjectileBlackFireball(actor.level, actor, actor.getAttack());
        double d0 = target.getY() + (double) target.getEyeHeight() - 2;
        double xDir = target.getX() - actor.getX();
        double yDir = d0 - projectile.getY();
        double zDir = target.getZ() - actor.getZ();
        projectile.shoot(xDir, yDir, zDir, velocity, inaccuracy);
        projectile.setTravelRange(25);
        actor.level.addFreshEntity(projectile);
    }
}
