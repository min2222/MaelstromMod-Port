package com.barribob.mm.entity.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;

/**
 * Combines melee and ranged ai to create an ai that does both intermittently
 *
 * @param <T> The entity getting the ai
 */
public class AIMeleeAndRange<T extends PathfinderMob & RangedAttackMob> extends Goal {
    private int switchUpdateTime;
    private float switchChance;

    private int switchTimer;

    private EntityAIRangedAttack rangedAttackAI;
    private MeleeAttackGoal meleeAttackAI;

    private Goal attackAI;

    private T entity;

    public AIMeleeAndRange(T mob, double speedIn, boolean useLongMemory, double moveSpeedAmp, int attackCooldown, float maxAttackDistance, int switchUpdateTime,
                           float switchChance, float strafeAmount) {
        rangedAttackAI = new EntityAIRangedAttack<T>(mob, moveSpeedAmp, attackCooldown, maxAttackDistance, strafeAmount);
        meleeAttackAI = new MeleeAttackGoal(mob, speedIn, useLongMemory);
        attackAI = meleeAttackAI;
        this.switchUpdateTime = switchUpdateTime;
        this.switchChance = switchChance;
        this.entity = mob;
    }

    public boolean isRanged() {
        return attackAI.equals(rangedAttackAI);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean canUse() {
        return attackAI.canUse() && meleeAttackAI.canUse();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return attackAI.canContinueToUse() && meleeAttackAI.canContinueToUse();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        attackAI.start();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by
     * another one
     */
    public void stop() {
        this.rangedAttackAI.stop();
        this.meleeAttackAI.stop();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        this.switchTimer--;
        if (this.switchTimer <= 0) {
            this.switchTimer = this.switchUpdateTime;

            if (this.entity.level.random.nextFloat() < this.switchChance) {
                // Switch ai's
                if (attackAI == this.meleeAttackAI) {
                    attackAI = this.rangedAttackAI;
                } else {
                    attackAI = this.meleeAttackAI;
                }
            }
        }

        this.attackAI.tick();
    }
}
