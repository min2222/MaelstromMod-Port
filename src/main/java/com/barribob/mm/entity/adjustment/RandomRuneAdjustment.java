package com.barribob.mm.entity.adjustment;

import com.barribob.mm.entity.util.IEntityAdjustment;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.Entity;

public class RandomRuneAdjustment implements IEntityAdjustment {
    private final Entity target;

    public RandomRuneAdjustment(Entity target) {
        this.target = target;
    }

    @Override
    public void adjust(Entity entity) {
        ModUtils.setEntityPosition(entity,
                target.position()
                        .add(ModRandom.randFlatVec(ModUtils.Y_AXIS).scale(ModRandom.getFloat(2)))
                        .add(ModUtils.yVec(0.1)));
    }
}
