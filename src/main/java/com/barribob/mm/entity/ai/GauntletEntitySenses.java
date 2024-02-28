package com.barribob.mm.entity.ai;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.sensing.Sensing;

public class GauntletEntitySenses extends Sensing {

    private final Mob entity;

    public GauntletEntitySenses(Mob entityIn) {
        super(entityIn);
        entity = entityIn;
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean hasLineOfSight(@Nonnull Entity entityIn) {
        return entity.getTarget() != null || super.hasLineOfSight(entityIn);
    }
}
