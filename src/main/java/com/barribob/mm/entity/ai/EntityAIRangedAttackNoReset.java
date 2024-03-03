package com.barribob.mm.entity.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;

/**
 * Makes the task unresettable
 */
public class EntityAIRangedAttackNoReset<T extends Mob & RangedAttackMob> extends EntityAIRangedAttack<T> {
    public EntityAIRangedAttackNoReset(T entity, double moveSpeedAmp, int attackCooldown, float armsRaisedTime, float maxAttackDistance, float strafeAmount) {
        super(entity, moveSpeedAmp, attackCooldown, armsRaisedTime, maxAttackDistance, strafeAmount);
    }

    @Override
    public void stop() {
    }
}
