package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import com.barribob.mm.entity.entities.EntityMaelstromHealer;
import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AIFlyingSupport extends Goal {
    private final EntityMaelstromHealer supporter;
    private final double movementSpeed;
    private final double heightAboveGround;
    private final double supportDistance;
    private final double supportCooldown;
    private double cooldown;

    public AIFlyingSupport(EntityMaelstromHealer creature, double movementSpeed, double heightAboveGround, double supportDistance, double supportCooldown) {
        this.supportCooldown = supportCooldown;
        this.supportDistance = supportDistance;
        this.supporter = creature;
        this.movementSpeed = movementSpeed;
        this.heightAboveGround = heightAboveGround;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return supporter.isFlying();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void stop() {
        super.stop();
        this.cooldown = this.supportCooldown;
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 groupCenter = ModUtils.findEntityGroupCenter(this.supporter, supporter.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue());
        boolean hasGroup = groupCenter.distanceToSqr(this.supporter.position()) != 0;

        /**
         * Provide support to the nearest mobs
         */
        LivingEntity optimalMob = null;
        double health = 2;
        for (LivingEntity entity : ModUtils.getEntitiesInBox(supporter, new AABB(supporter.blockPosition()).inflate(supporter.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()))) {
            if (!EntityMaelstromMob.CAN_TARGET.apply(entity) && entity.getHealth() / entity.getMaxHealth() < health && this.supporter.distanceToSqr(entity) < Math.pow(supportDistance, 2)) {
                optimalMob = entity;
                health = entity.getHealth() / entity.getMaxHealth();
            }
        }

        if (optimalMob != null && hasGroup) {
            cooldown--;
            /**
             * Face the closest mob
             */
            this.supporter.lookAt(optimalMob, 25, 25);
            this.supporter.getLookControl().setLookAt(optimalMob, 25, 25);

            /**
             * Provide support if close enough
             */
            if (this.cooldown <= 0) {
                this.supporter.performRangedAttack(optimalMob, (float) this.supporter.distanceToSqr(optimalMob));
                this.cooldown = supportCooldown;
            }

            Vec3 pos = groupCenter.add(ModUtils.yVec((float) (this.heightAboveGround + ModRandom.getFloat(0.5f) * this.heightAboveGround)));
            this.supporter.getNavigation().moveTo(pos.x, pos.y, pos.z, this.movementSpeed);
        } else {
            /**
             * Move towards the target, which is the center of the group
             */
            Vec3 pos;

            if (hasGroup) {
                pos = groupCenter.add(ModUtils.yVec((float) (this.heightAboveGround + ModRandom.getFloat(0.5f) * this.heightAboveGround)));
            }
            /**
             * Run away from the attack target if there are no mobs to support nearby
             */
            else if (this.supporter.getTarget() != null) {
                Vec3 away = this.supporter.position().subtract(this.supporter.getTarget().position()).normalize();
                pos = this.supporter.position().add(away.scale(4)).add(ModRandom.randVec().scale(4));
            }
            /**
             * There is no target and no mobs to support, slowly float down
             */
            else {
                pos = this.supporter.position().add(ModUtils.yVec(0.01));
            }

            this.supporter.getNavigation().moveTo(pos.x, pos.y, pos.z, this.movementSpeed);
            this.supporter.getLookControl().setLookAt(pos.x, pos.y, pos.z, 3, 3);
            ModUtils.facePosition(pos, this.supporter, 10, 10);
        }
    }
}
