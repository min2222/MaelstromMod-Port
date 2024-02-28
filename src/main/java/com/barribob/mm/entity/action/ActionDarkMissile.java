package com.barribob.mm.entity.action;

import net.minecraft.world.entity.LivingEntity;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.projectile.ProjectileMaelstromMissile;

import net.minecraft.sounds.SoundEvents;

public class ActionDarkMissile implements IAction {
    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        float inaccuracy = 4.0f;
        float velocity = 0.5f;

        ProjectileMaelstromMissile projectile = new ProjectileMaelstromMissile(actor.level, actor, actor.getAttack());
        double d0 = target.getY() + (double) target.getEyeHeight() - 1.0f;
        double d1 = target.getX() - actor.getX();
        double d2 = d0 - actor.getY();
        double d3 = target.getZ() - actor.getZ();
        projectile.shoot(d1, d2, d3, velocity, inaccuracy);
        projectile.setTravelRange(20f);
        actor.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F / (actor.getRandom().nextFloat() * 0.4F + 0.8F));
        actor.level.addFreshEntity(projectile);
    }
}
