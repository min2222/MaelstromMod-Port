package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityAIAvoidCrowding extends Goal {
    private final PathfinderMob creature;
    private Vec3 pos;
    private final double movementSpeed;

    public EntityAIAvoidCrowding(PathfinderMob theCreatureIn, double movementSpeedIn) {
        this.creature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * Executes if there are nearby entities to avoid overcrowding
     */
    @Override
    public boolean canUse() {
        Vec3 groupCenter = Vec3.ZERO;
        float numMobs = 0;
        for (LivingEntity entity : ModUtils.getEntitiesInBox(creature, new AABB(creature.blockPosition()).inflate(1.5f))) {
            if (entity instanceof EntityMaelstromMob) {
                groupCenter = groupCenter.add(entity.position());
                numMobs += 1;
            }
        }

        if (numMobs > 0) {
            groupCenter = groupCenter.scale(1 / numMobs);
            pos = DefaultRandomPos.getPosAway(creature, 5, 3, groupCenter);

            if (pos == null) {
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.creature.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.creature.getNavigation().moveTo(pos.x, pos.y, pos.z, this.movementSpeed);
    }
}