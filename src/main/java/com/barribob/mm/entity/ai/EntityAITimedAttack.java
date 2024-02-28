package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import com.barribob.mm.entity.util.IAttack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * A simplified version of the skeleton's ranged attack AI
 */
public class EntityAITimedAttack<T extends Mob & IAttack> extends Goal {
    private final T entity;
    private final double moveSpeedAmp;
    private final int attackCooldown;
    private final float maxAttackDistSq;
    private int attackTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private final float strafeAmount;
    private float lookSpeed;

    private static final float STRAFING_STOP_FACTOR = 0.75f;
    private static final float STRAFING_BACKWARDS_FACTOR = 0.25f;
    private static final float STRAFING_DIRECTION_TICK = 20;
    private static final float STRAFING_DIRECTION_CHANGE_CHANCE = 0.3f;

    public EntityAITimedAttack(T entity, double moveSpeedAmp, int attackCooldown, float maxAttackDistance, float strafeAmount) {
        this(entity, moveSpeedAmp, attackCooldown, maxAttackDistance, strafeAmount, 30.0f);
    }

    public EntityAITimedAttack(T entity, double moveSpeedAmp, int attackCooldown, float maxAttackDistance, float strafeAmount, float lookSpeed) {
        this.entity = entity;
        this.moveSpeedAmp = moveSpeedAmp;
        this.attackCooldown = attackCooldown;
        this.maxAttackDistSq = maxAttackDistance * maxAttackDistance;
        this.strafeAmount = strafeAmount;
        this.attackTime = attackCooldown;
        this.lookSpeed = lookSpeed;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.entity.getTarget() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return (this.canUse() || !this.entity.getNavigation().isDone());
    }

    @Override
    public void stop() {
        super.stop();
        this.attackTime = Math.max(attackTime, attackCooldown);
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();

        if (target == null) {
            return;
        }

        double distSq = this.entity.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
        boolean canSee = this.entity.getSensing().hasLineOfSight(target);

        move(target, distSq, canSee);

        if (distSq <= this.maxAttackDistSq && canSee) {
            this.attackTime--;
            if (this.attackTime <= 0) {
                this.attackTime = this.entity.startAttack(target, (float) distSq, this.strafingBackwards);
            }
        }
    }

    public void move(LivingEntity target, double distSq, boolean canSee) {
        if (distSq <= this.maxAttackDistSq && canSee) {
            this.entity.getNavigation().stop();
            ++this.strafingTime;
        } else {
            this.entity.getNavigation().moveTo(target, this.moveSpeedAmp);
            this.strafingTime = -1;
        }

        if (this.strafingTime >= STRAFING_DIRECTION_TICK) {
            if ((double) this.entity.getRandom().nextFloat() < STRAFING_DIRECTION_CHANGE_CHANCE) {
                this.strafingClockwise = !this.strafingClockwise;
            }

            if ((double) this.entity.getRandom().nextFloat() < STRAFING_DIRECTION_CHANGE_CHANCE) {
                this.strafingBackwards = !this.strafingBackwards;
            }

            this.strafingTime = 0;
        }

        if (this.strafingTime > -1) {
            if (distSq > this.maxAttackDistSq * STRAFING_STOP_FACTOR) {
                this.strafingBackwards = false;
            } else if (distSq < this.maxAttackDistSq * STRAFING_BACKWARDS_FACTOR) {
                this.strafingBackwards = true;
            }

            this.entity.getMoveControl().strafe((this.strafingBackwards ? -1 : 1) * this.strafeAmount, (this.strafingClockwise ? 1 : -1) * this.strafeAmount);
            this.entity.lookAt(target, this.lookSpeed, this.lookSpeed);
        } else {
            this.entity.getLookControl().setLookAt(target, this.lookSpeed, this.lookSpeed);
        }
    }
}