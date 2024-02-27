package com.barribob.MaelstromMod.entity.ai;

import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.entity.Mob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * AI made specifically for the gauntlet to wander using its punch attack
 *
 * @author Barribob
 */
public class AiFistWander extends EntityAIBase {
    protected final Mob entity;
    protected int cooldown;
    protected float heightAboveGround;
    Consumer<Vec3> movement;

    public AiFistWander(Mob entity, Consumer<Vec3> movement, int cooldown, float heightAboveGround) {
        this.entity = entity;
        this.cooldown = cooldown;
        this.heightAboveGround = heightAboveGround;
        this.movement = movement;
        this.setMutexBits(3);
    }

    @Nullable
    protected Vec3 getPosition() {
        Vec3 groupCenter = ModUtils.findEntityGroupCenter(this.entity, 20);

        for (int i = 0; i < 10; i++) {
            int minRange = 5;
            int maxRange = 15;
            Vec3 pos = groupCenter.add(new Vec3(ModRandom.range(minRange, maxRange) * ModRandom.randSign(), 0, ModRandom.range(minRange, maxRange) * ModRandom.randSign()));
            pos = new Vec3(ModUtils.findGroundBelow(entity.world, new BlockPos(pos)));
            pos = pos.add(ModUtils.yVec(heightAboveGround));

            RayTraceResult result = entity.world.rayTraceBlocks(entity.getPositionEyes(1), pos, false, true, false);
            if (result == null || result.typeOfHit != RayTraceResult.Type.BLOCK) {
                return pos;
            }
        }

        return null;

    }

    @Override
    public boolean shouldExecute() {
        return entity.getAttackTarget() == null;
    }

    @Override
    public void updateTask() {
        int ticks = this.entity.ticksExisted % this.cooldown;
        if (ticks != 0) {
            return;
        }

        Vec3 vec3d = this.getPosition();

        if (vec3d != null) {
            movement.accept(vec3d);
        }
    }
}