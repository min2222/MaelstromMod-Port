package com.barribob.mm.entity.ai;

import net.minecraft.world.entity.PathfinderMob;

/**
 * Makes the task unresettable
 */
public class EntityAIRangedAttackNoReset<T extends PathfinderMob> extends EntityAIRangedAttack {
    public EntityAIRangedAttackNoReset(T entity, double moveSpeedAmp, int attackCooldown, float armsRaisedTime, float maxAttackDistance, float strafeAmount) {
        super(entity, moveSpeedAmp, attackCooldown, armsRaisedTime, maxAttackDistance, strafeAmount);
    }

    @Override
    public void stop() {
    }
}
