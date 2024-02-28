package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import javax.annotation.Nullable;

import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class AIPassiveCircle<T extends Mob> extends Goal {

    private final T entity;
    private @Nullable Vec3 planeVectorPath = getNewPlaneVector();
    private final float circleRadius;

    public AIPassiveCircle(T entity, float circleRadius) {
        this.entity = entity;
        this.circleRadius = circleRadius;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return entity.getTarget() != null;
    }

    @Override
    public void tick() {
        if(entity.getTarget() != null) {
            Vec3 target = entity.getTarget().position();
            Vec3 nextPointToFollow = getNextPoint(target);
            Vec3 direction = nextPointToFollow.subtract(entity.position()).normalize();
            double speed = entity.getAttribute(Attributes.FLYING_SPEED).getBaseValue();
            ModUtils.addEntityVelocity(entity, direction.scale(0.05f * speed));

            if (!hasClearPath(nextPointToFollow) || lineBlocked(nextPointToFollow, target)) {
                planeVectorPath = getNewPlaneVector();
            }

            ModUtils.facePosition(nextPointToFollow, entity, 10, 10);
            entity.getLookControl().setLookAt(nextPointToFollow.x, nextPointToFollow.y, nextPointToFollow.z, 3, 3);
        }

        super.tick();
    }

    private Vec3 getNewPlaneVector() {
        return ModUtils.Y_AXIS.add(ModRandom.randVec().scale(2)).normalize();
    }

    private boolean hasClearPath(Vec3 nextPointToFollow) {
        return ModUtils.getBoundingBoxCorners(entity.getBoundingBox()).stream().noneMatch(vec3d -> lineBlocked(vec3d, nextPointToFollow));
    }

    private boolean lineBlocked(Vec3 start, Vec3 nextPointToFollow) {
        HitResult rayTraceResult = entity.level.clip(new ClipContext(start, nextPointToFollow, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        return rayTraceResult != null && rayTraceResult.getType() == HitResult.Type.BLOCK;
    }

    private Vec3 getNextPoint(Vec3 center) {
        Vec3 circlePointInWorld = entity.position().add(ModRandom.randVec());
        for(int i = 0; i < circleRadius; i++) {
            for(int sign : new int[]{1, -1}) {
                Vec3 entityVelocity = ModUtils.getEntityVelocity(entity);
                Vec3 entityDirection = entity.position().subtract(center);
                Vec3 projectedEntityDirection = ModUtils.planeProject(entityDirection, planeVectorPath).normalize().scale(circleRadius + (i * sign));
                Vec3 nextPointOnCircle = ModUtils.rotateVector2(projectedEntityDirection, planeVectorPath, 15 * entityVelocity.length());
                circlePointInWorld = nextPointOnCircle.add(center);
                if (hasClearPath(circlePointInWorld)) {
                    return circlePointInWorld;
                } else {
                    planeVectorPath = getNewPlaneVector();
                }
            }
        }
        return circlePointInWorld;
    }
}
