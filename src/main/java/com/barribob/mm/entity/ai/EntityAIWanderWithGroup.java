package com.barribob.mm.entity.ai;

import javax.annotation.Nullable;

import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class EntityAIWanderWithGroup extends RandomStrollGoal {
    public EntityAIWanderWithGroup(PathfinderMob entity, double speed) {
        super(entity, speed, 20);
    }

    @Override
    @Nullable
    protected Vec3 getPosition() {
        if (this.mob.isInWater()) {
            Vec3 vec3d = DefaultRandomPos.getPos(this.mob, 15, 7);
            return vec3d == null ? super.getPosition() : vec3d;
        } else {
            Vec3 groupCenter = ModUtils.findEntityGroupCenter(this.mob, 20);

            for (int i = 0; i < 10; i++) {
                Vec3 pos = DefaultRandomPos.getPosAway(this.mob, 10, 7, groupCenter);
                if (pos != null) {
                    return pos;
                }
            }

            return DefaultRandomPos.getPos(this.mob, 10, 7);
        }
    }
}