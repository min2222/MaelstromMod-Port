package com.barribob.mm.entity.ai;

import com.barribob.mm.util.ModRandom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.phys.Vec3;

public class AIFlyingRangedAttack<T extends Mob & RangedAttackMob> extends Goal {
    private final T parentEntity;
    public int attackTimer;
    private final int attackCooldown;
    private final float maxAttackDistance;
    private final float idealAttackDistance;
    protected float armsRaisedTime;
    private float moveSpeed;

    public AIFlyingRangedAttack(T e, int attackCooldown, float maxAttackDistance, float raiseArmsTime, float moveSpeed) {
        this.parentEntity = e;
        this.attackCooldown = attackCooldown;
        this.maxAttackDistance = maxAttackDistance * maxAttackDistance;
        this.idealAttackDistance = this.maxAttackDistance * 0.25f;
        this.armsRaisedTime = raiseArmsTime;
        this.moveSpeed = moveSpeed;
    }

    @Override
    public boolean canUse() {
        return this.parentEntity.getTarget() != null;
    }

    @Override
    public void start() {
        this.attackTimer = 0;
    }

    @Override
    public void stop() {
        this.parentEntity.setAggressive(false);
    }

    @Override
    public void tick() {
        LivingEntity entitylivingbase = this.parentEntity.getTarget();
        double distance = entitylivingbase.distanceToSqr(this.parentEntity);

        if (distance < maxAttackDistance && this.parentEntity.hasLineOfSight(entitylivingbase)) {
            ++this.attackTimer;

            if (this.attackTimer == armsRaisedTime) {
                this.parentEntity.setAggressive(true);
            }

            if (this.attackTimer == attackCooldown) {
                if (!this.parentEntity.level.isClientSide) {
                    this.parentEntity.performRangedAttack(this.parentEntity.getTarget(), (float) distance);
                }
                this.attackTimer = -attackCooldown;
            }
        } else if (this.attackTimer > 0) {
            --this.attackTimer;
        }

        if (distance > idealAttackDistance) {
            Vec3 moveVec = this.parentEntity.getTarget().position().subtract(this.parentEntity.position()).normalize().scale(16);
            Vec3 pos = this.parentEntity.position().add(moveVec);
            pos = pos.add(ModRandom.randVec().scale(16));
            this.parentEntity.getMoveControl().setWantedPosition(pos.x, pos.y, pos.z, moveSpeed);
        }

    }
}