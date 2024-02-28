package com.barribob.mm.entity.adjustment;

import com.barribob.mm.entity.util.IEntityAdjustment;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MovingRuneAdjustment implements IEntityAdjustment {
    private final Entity target;

    public MovingRuneAdjustment(Entity target) {
        this.target = target;
    }

    @Override
    public void adjust(Entity entity) {
        Vec3 randomDirection = ModRandom.randFlatVec(ModUtils.Y_AXIS);
        ModUtils.setEntityPosition(entity,
                target.position()
                        .add(ModUtils.yVec(0.1))
                        .add(randomDirection.scale(2)));
        Vec3 velocity = randomDirection.scale(0.13 + ModRandom.getFloat(0.05f)).scale(-1);
        ModUtils.setEntityVelocity(entity, velocity);
    }
}
