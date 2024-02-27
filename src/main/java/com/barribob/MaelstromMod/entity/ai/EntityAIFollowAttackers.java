package com.barribob.MaelstromMod.entity.ai;

import com.barribob.MaelstromMod.entity.entities.EntityMaelstromMob;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.phys.AABB;

public class EntityAIFollowAttackers extends EntityAIBase {
    private final EntityCreature creature;
    private LivingEntity targetEntity;
    private final double movementSpeed;

    public EntityAIFollowAttackers(EntityCreature theCreatureIn, double movementSpeedIn) {
        this.creature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexBits(1);
    }

    /**
     * Executes if there are nearby entities to avoid overcrowding
     */
    @Override
    public boolean shouldExecute() {
        LivingEntity closestMob = null;
        double distanceSq = Math.pow(creature.getEntityAttribute(Attributes.FOLLOW_RANGE).getAttributeValue(), 2);
        for (LivingEntity entity : ModUtils.getEntitiesInBox(creature, new AABB(creature.getPosition()).grow(creature.getEntityAttribute(Attributes.FOLLOW_RANGE).getAttributeValue()))) {
            if (!EntityMaelstromMob.CAN_TARGET.apply(entity) &&
                    creature.getAttackTarget() == null &&
                    entity instanceof Mob &&
                    ((Mob)entity).getAttackTarget() != null) {
                if (entity.getDistanceSq(creature) < distanceSq) {
                    closestMob = entity;
                    distanceSq = entity.getDistanceSq(creature);
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
    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.creature.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.movementSpeed);
    }
}