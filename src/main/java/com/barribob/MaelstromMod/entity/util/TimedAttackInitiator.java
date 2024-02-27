package com.barribob.MaelstromMod.entity.util;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;

public class TimedAttackInitiator<T extends Mob & IAttack> implements IAttackInitiator {
    private final T entity;
    private int attackTime;
    private final int attackCooldown;

    public TimedAttackInitiator(T entity, int startingCooldown) {
        this.entity = entity;
        this.attackTime = startingCooldown;
        this.attackCooldown = startingCooldown;
    }

    @Override
    public void update(LivingEntity target) {
        this.attackTime--;
        if (this.attackTime <= 0) {
            double distSq = entity.getDistanceSq(target);
            this.attackTime = this.entity.startAttack(target, (float) distSq, false);
        }
    }

    @Override
    public void resetTask() {
        this.attackTime = Math.max(attackTime, attackCooldown);
    }
}
