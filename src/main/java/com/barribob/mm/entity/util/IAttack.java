package com.barribob.mm.entity.util;

import net.minecraft.world.entity.LivingEntity;

/**
 * Used by {@link #EntityAITimedAttack}
 */
public interface IAttack {
    /**
     * Called when the entity is ready to begin an attack sequence
     *
     * @param target
     * @param distanceSq
     * @param strafingBackwards
     * @return The number of seconds before launching another attack (calling this
     * method again)
     */
    int startAttack(LivingEntity target, float distanceSq, boolean strafingBackwards);
}
