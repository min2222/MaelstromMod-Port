package com.barribob.MaelstromMod.entity.ai;

import com.barribob.MaelstromMod.entity.entities.EntityMaelstromMob;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityAIAvoidCrowding extends EntityAIBase {
    private final EntityCreature creature;
    private Vec3 pos;
    private final double movementSpeed;

    public EntityAIAvoidCrowding(EntityCreature theCreatureIn, double movementSpeedIn) {
        this.creature = theCreatureIn;
        this.movementSpeed = movementSpeedIn;
        this.setMutexBits(1);
    }

    /**
     * Executes if there are nearby entities to avoid overcrowding
     */
    @Override
    public boolean shouldExecute() {
        Vec3 groupCenter = Vec3.ZERO;
        float numMobs = 0;
        for (LivingEntity entity : ModUtils.getEntitiesInBox(creature, new AABB(creature.getPosition()).grow(1.5f))) {
            if (entity instanceof EntityMaelstromMob) {
                groupCenter = groupCenter.add(entity.getPositionVector());
                numMobs += 1;
            }
        }

        if (numMobs > 0) {
            groupCenter = groupCenter.scale(1 / numMobs);
            pos = RandomPositionGenerator.findRandomTargetBlockAwayFrom(creature, 5, 3, groupCenter);

            if (pos == null) {
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.creature.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.creature.getNavigator().tryMoveToXYZ(pos.x, pos.y, pos.z, this.movementSpeed);
    }
}