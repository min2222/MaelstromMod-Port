package com.barribob.mm.entity.action;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.util.ModRandom;

import net.minecraft.world.entity.LivingEntity;

/*
 * Attempt to teleport around the target
 */
public class ActionTeleport implements IAction {
    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        int attempts = 100;
        int minRange = 5;
        int maxRange = 11;
        int yOffset = 2;

        while (attempts > 0) {
            double x = ModRandom.range(minRange, maxRange) * ModRandom.randSign() + target.getX();
            double z = ModRandom.range(minRange, maxRange) * ModRandom.randSign() + target.getZ();
            double y = target.getY() - yOffset;

            for (; y <= yOffset + target.getY(); y++) {
                if (actor.attemptTeleport(x, y, z)) {
                    return;
                }
            }

            attempts++;
        }
    }
}
