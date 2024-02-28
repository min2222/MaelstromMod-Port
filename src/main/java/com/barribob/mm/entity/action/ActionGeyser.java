package com.barribob.mm.entity.action;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.projectile.EntityGeyser;

import net.minecraft.world.entity.LivingEntity;

public class ActionGeyser implements IAction {
    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        float zeroish = 0.001f;
        EntityGeyser projectile = new EntityGeyser(actor.level, actor, actor.getAttack());
        projectile.setPos(target.getX(), target.getY(), target.getZ());
        projectile.shoot(zeroish, zeroish, zeroish, zeroish, zeroish);
        projectile.setTravelRange(25);
        actor.level.addFreshEntity(projectile);
    }
}
