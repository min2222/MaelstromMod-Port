package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class AIJumpAtTarget extends Goal {
    private final Mob entity;
    private final float horzVel;
    private final float yVel;
    private int ticksAirborne = 0;

    public AIJumpAtTarget(Mob entity, float horzVel, float yVel) {
        this.entity = entity;
        this.horzVel = horzVel;
        this.yVel = yVel;
        this.setFlags(EnumSet.of(Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.entity.isOnGround()) {
            this.ticksAirborne = 0;
        } else {
            this.ticksAirborne++;
        }

        if (entity.getTarget() == null) {
            return false;
        }

        // Our goal is to capture the time right when the entity slips of the edge
        if (this.ticksAirborne == 1 && ModUtils.isAirBelow(entity.level, entity.blockPosition(), 7)) {
            ModUtils.leapTowards(entity, entity.getTarget().position(), horzVel, yVel);
            return true;
        } else {
            if (this.entity.getNavigation() != null && this.entity.getNavigation().isDone() && this.entity.isOnGround()) {
                Vec3 jumpDirection = entity.getTarget().position().subtract(entity.position()).normalize();
                Vec3 jumpPos = entity.position().add(jumpDirection);

                if (!ModUtils.isAirBelow(entity.level, new BlockPos(jumpPos), 3)) {
                    return false;
                }

                for (int i = 0; i < 2; i++) {
                    jumpPos = jumpPos.add(jumpDirection);
                    if (jumpPos.subtract(entity.position()).y < 1.2) {
                        BlockPos pos = new BlockPos(jumpPos);
                        NodeEvaluator processor = this.entity.getNavigation().getNodeEvaluator();

                        for (int y = -1; y <= 1; y++) {
                            if (processor.getBlockPathType(this.entity.level, pos.getX(), pos.getY() + y, pos.getZ()) == BlockPathTypes.WALKABLE) {
                                ModUtils.leapTowards(entity, entity.getTarget().position(), horzVel * (i * 0.3f + ModRandom.getFloat(0.3f) + 1), yVel);
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void start() {
    }
}
