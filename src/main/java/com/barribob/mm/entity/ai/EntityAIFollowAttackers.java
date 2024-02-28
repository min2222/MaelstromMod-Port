package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class EntityAIFollowAttackers extends Goal {
    private final PathfinderMob creature;
    private LivingEntity targetEntity;
    private final double movementSpeed;

    public EntityAIFollowAttackers(PathfinderMob theCreatureIn, double movementSpeedIn) {
        this.creature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * Executes if there are nearby entities to avoid overcrowding
     */
    @Override
    public boolean canUse() {
        LivingEntity closestMob = null;
        double distanceSq = Math.pow(creature.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue(), 2);
        for (LivingEntity entity : ModUtils.getEntitiesInBox(creature, new AABB(creature.blockPosition()).inflate(creature.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()))) {
            if (!EntityMaelstromMob.CAN_TARGET.apply(entity) &&
                    creature.getTarget() == null &&
                    entity instanceof Mob &&
                    ((Mob)entity).getTarget() != null) {
                if (entity.distanceToSqr(creature) < distanceSq) {
                    closestMob = entity;
                    distanceSq = entity.distanceToSqr(creature);
                }
            }
        }

        if (closestMob != null) {
            this.targetEntity = closestMob;
            return true;
        }

        this.targetEntity = null;
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.creature.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.creature.getNavigation().moveTo(this.targetEntity, this.movementSpeed);
    }
}