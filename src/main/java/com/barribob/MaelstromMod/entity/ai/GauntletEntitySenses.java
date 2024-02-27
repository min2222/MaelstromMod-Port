package com.barribob.MaelstromMod.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.entity.ai.EntitySenses;

import javax.annotation.Nonnull;

public class GauntletEntitySenses extends EntitySenses {

    private final Mob entity;

    public GauntletEntitySenses(Mob entityIn) {
        super(entityIn);
        entity = entityIn;
    }

    @Override
    public void clearSensingCache() {
    }

    @Override
    public boolean canSee(@Nonnull Entity entityIn) {
        return entity.getAttackTarget() != null || super.canSee(entityIn);
    }
}
