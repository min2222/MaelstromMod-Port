package com.barribob.mm.entity.ai;

import java.util.EnumSet;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * AI made specifically for the gauntlet to wander using its punch attack
 *
 * @author Barribob
 */
public class AiFistWander extends Goal {
    protected final Mob entity;
    protected int cooldown;
    protected float heightAboveGround;
    Consumer<Vec3> movement;

    public AiFistWander(Mob entity, Consumer<Vec3> movement, int cooldown, float heightAboveGround) {
        this.entity = entity;
        this.cooldown = cooldown;
        this.heightAboveGround = heightAboveGround;
        this.movement = movement;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Nullable
    protected Vec3 getPosition() {
        Vec3 groupCenter = ModUtils.findEntityGroupCenter(this.entity, 20);

        for (int i = 0; i < 10; i++) {
            int minRange = 5;
            int maxRange = 15;
            Vec3 pos = groupCenter.add(new Vec3(ModRandom.range(minRange, maxRange) * ModRandom.randSign(), 0, ModRandom.range(minRange, maxRange) * ModRandom.randSign()));
            pos = Vec3.atCenterOf(ModUtils.findGroundBelow(entity.level, new BlockPos(pos)));
            pos = pos.add(ModUtils.yVec(heightAboveGround));

            HitResult result = entity.level.clip(new ClipContext(entity.getEyePosition(1), pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            if (result == null || result.getType() != HitResult.Type.BLOCK) {
                return pos;
            }
        }

        return null;

    }

    @Override
    public boolean canUse() {
        return entity.getTarget() == null;
    }

    @Override
    public void tick() {
        int ticks = this.entity.tickCount % this.cooldown;
        if (ticks != 0) {
            return;
        }

        Vec3 vec3d = this.getPosition();

        if (vec3d != null) {
            movement.accept(vec3d);
        }
    }
}