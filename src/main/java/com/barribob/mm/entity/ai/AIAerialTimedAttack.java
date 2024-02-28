package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import com.barribob.mm.entity.util.IAttackInitiator;
import com.barribob.mm.entity.util.IPitch;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

/**
 * A version of the timed attack that attempts to work for flying mobs a bit better.
 *
 * @author micha
 */
public class AIAerialTimedAttack extends Goal {
    private final Mob entity;
    private final float maxAttackDistSq;
    private final float lookSpeed;
    private final IAttackInitiator attackInitiator;
    private int unseeTime;
    private final AIPassiveCircle<Mob> circleAI;

    private static final int MEMORY = 100;

    public AIAerialTimedAttack(Mob entity, float maxAttackDistance, float idealAttackDistance, float lookSpeed, IAttackInitiator attackInitiator) {
        this.entity = entity;
        this.maxAttackDistSq = maxAttackDistance * maxAttackDistance;
        this.lookSpeed = lookSpeed;
        this.attackInitiator = attackInitiator;
        circleAI = new AIPassiveCircle<>(entity, idealAttackDistance);
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
        attackInitiator.stop();
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();

        if (target == null) {
            return;
        }

        double distSq = this.entity.distanceToSqr(target.getX(), target.getBoundingBox().minY, target.getZ());
        boolean canSee = this.entity.getSensing().hasLineOfSight(target);

        // Implements some sort of memory mechanism (can still attack a short while after the enemy isn't seen)
        if (canSee) {
            unseeTime = 0;
        } else {
            unseeTime += 1;
        }

        canSee = canSee || unseeTime < MEMORY;

        move(target, distSq, canSee);

        if (distSq <= this.maxAttackDistSq && canSee) {
            attackInitiator.update(target);
        }
    }

    public void move(LivingEntity target, double distSq, boolean canSee) {
        circleAI.tick();

        this.entity.getLookControl().setLookAt(target, this.lookSpeed, this.lookSpeed);
        this.entity.lookAt(target, this.lookSpeed, this.lookSpeed);
        if (this.entity instanceof IPitch) {
            Vec3 targetPos = target.getEyePosition(1);
            Vec3 entityPos = this.entity.getEyePosition(1);
            Vec3 forwardVec = ModUtils.direction(entityPos, targetPos);
            ((IPitch) this.entity).setPitch(forwardVec);
        }
    }
}