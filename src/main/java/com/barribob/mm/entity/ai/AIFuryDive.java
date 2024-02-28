package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Has a cooldown that prevents it from running immediately
 * Requires the player not be looking at the entity
 */
public class AIFuryDive extends Goal {

    private int cooldown;
    private final int maxCooldown;
    private final Mob entity;
    private final Runnable onDiveStart;
    private final Runnable onDiveEnd;
    private final Runnable whileDiving;
    private final int maxDiveTime;

    public AIFuryDive(int maxCooldown, int maxDiveTime, Mob entity, Runnable onDiveStart, Runnable onDiveEnd, Runnable whileDiving) {
        this.maxCooldown = maxCooldown;
        this.entity = entity;
        this.onDiveEnd = onDiveEnd;
        this.onDiveStart = onDiveStart;
        this.whileDiving = whileDiving;
        this.maxDiveTime = maxDiveTime;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        cooldown++;
        return canContinueToUse() && entity.getTarget() != null && entity.getTarget().getLookAngle().dot(entity.position().subtract(entity.getTarget().getEyePosition(1))) < 0;
    }

    @Override
    public boolean canContinueToUse() {
        return cooldown >= maxCooldown && entity.getTarget() != null && hasClearPath(entity.getTarget().getEyePosition(1));
    }

    @Override
    public void start() {
        onDiveStart.run();
        cooldown = maxCooldown;
    }

    private boolean hasClearPath(Vec3 nextPointToFollow) {
        return ModUtils.getBoundingBoxCorners(entity.getBoundingBox()).stream().noneMatch(vec3d -> {
            HitResult rayTraceResult = entity.level.clip(new ClipContext(vec3d, nextPointToFollow, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, entity));
            return rayTraceResult != null && rayTraceResult.getType() == HitResult.Type.BLOCK;
        });
    }

    @Override
    public void stop() {
        cooldown = 0;
        onDiveEnd.run();
    }

    @Override
    public void tick() {
        dive();
    }

    public void dive() {
        if (entity.getTarget() != null) {
            cooldown++;
            Vec3 target = entity.getTarget().getEyePosition(1);
            Vec3 pos = entity.position();
            Vec3 toTarget = target.subtract(pos);
            Vec3 directionToTarget = toTarget.normalize();
            double speed = entity.getAttribute(Attributes.FLYING_SPEED).getBaseValue();
            double speedForDistance = ModUtils.clamp((toTarget.length() / 20.0), 1, 1.5f);
            Vec3 velocityCorrection = ModUtils.planeProject(ModUtils.getEntityVelocity(entity), directionToTarget);
            ModUtils.addEntityVelocity(entity, directionToTarget.subtract(velocityCorrection).scale(0.055 * speed * speedForDistance));
            Vec3 lookTarget = pos.add(directionToTarget);

            ModUtils.facePosition(lookTarget, entity, 10, 10);
            entity.getLookControl().setLookAt(lookTarget.x, lookTarget.y, lookTarget.z, 3, 3);
            whileDiving.run();

            double hitboxCompensation = entity.getBoundingBox().getSize() * 0.5 +
                    entity.getTarget().getBoundingBox().getSize() * 0.5;

            boolean collided = (entity.horizontalCollision || entity.horizontalCollision);
            boolean diveInCriteria = target.distanceToSqr(pos) < Math.pow(hitboxCompensation + 1.5, 2) || collided;

            if (diveInCriteria || !hasClearPath(target) || cooldown - maxCooldown > maxDiveTime) {
                stop();
            }
        }
    }
}
