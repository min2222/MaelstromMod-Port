package com.barribob.MaelstromMod.entity.ai;

import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;

/**
 * Has a cooldown that prevents it from running immediately
 * Requires the player not be looking at the entity
 */
public class AIFuryDive extends EntityAIBase {

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
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        cooldown++;
        return shouldContinueExecuting() && entity.getAttackTarget() != null && entity.getAttackTarget().getLookVec().dotProduct(entity.getPositionVector().subtract(entity.getAttackTarget().getPositionEyes(1))) < 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return cooldown >= maxCooldown && entity.getAttackTarget() != null && hasClearPath(entity.getAttackTarget().getPositionEyes(1));
    }

    @Override
    public void startExecuting() {
        onDiveStart.run();
        cooldown = maxCooldown;
    }

    private boolean hasClearPath(Vec3 nextPointToFollow) {
        return ModUtils.getBoundingBoxCorners(entity.getEntityBoundingBox()).stream().noneMatch(vec3d -> {
            RayTraceResult rayTraceResult = entity.world.rayTraceBlocks(vec3d, nextPointToFollow, true, true, false);
            return rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK;
        });
    }

    @Override
    public void resetTask() {
        cooldown = 0;
        onDiveEnd.run();
    }

    @Override
    public void updateTask() {
        dive();
    }

    public void dive() {
        if (entity.getAttackTarget() != null) {
            cooldown++;
            Vec3 target = entity.getAttackTarget().getPositionEyes(1);
            Vec3 pos = entity.getPositionVector();
            Vec3 toTarget = target.subtract(pos);
            Vec3 directionToTarget = toTarget.normalize();
            double speed = entity.getEntityAttribute(Attributes.FLYING_SPEED).getAttributeValue();
            double speedForDistance = ModUtils.clamp((toTarget.lengthVector() / 20.0), 1, 1.5f);
            Vec3 velocityCorrection = ModUtils.planeProject(ModUtils.getEntityVelocity(entity), directionToTarget);
            ModUtils.addEntityVelocity(entity, directionToTarget.subtract(velocityCorrection).scale(0.055 * speed * speedForDistance));
            Vec3 lookTarget = pos.add(directionToTarget);

            ModUtils.facePosition(lookTarget, entity, 10, 10);
            entity.getLookHelper().setLookPosition(lookTarget.x, lookTarget.y, lookTarget.z, 3, 3);
            whileDiving.run();

            double hitboxCompensation = entity.getEntityBoundingBox().getAverageEdgeLength() * 0.5 +
                    entity.getAttackTarget().getEntityBoundingBox().getAverageEdgeLength() * 0.5;

            boolean diveInCriteria = target.squareDistanceTo(pos) < Math.pow(hitboxCompensation + 1.5, 2) || entity.collided;

            if (diveInCriteria || !hasClearPath(target) || cooldown - maxCooldown > maxDiveTime) {
                resetTask();
            }
        }
    }
}
