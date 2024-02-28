package com.barribob.mm.entity.ai;

import java.util.EnumSet;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class AILookAround extends Goal {
    private final Mob parentEntity;

    public AILookAround(Mob e) {
        this.parentEntity = e;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        if (this.parentEntity.getTarget() == null) {
            this.parentEntity.setYRot(-((float) Mth.atan2(this.parentEntity.getDeltaMovement().x, this.parentEntity.getDeltaMovement().z)) * (180F / (float) Math.PI));
            this.parentEntity.yBodyRot = this.parentEntity.getYRot();
        } else {
            LivingEntity entitylivingbase = this.parentEntity.getTarget();
            if (entitylivingbase.distanceToSqr(this.parentEntity) < 4096.0D) {
                double d1 = entitylivingbase.getX() - this.parentEntity.getX();
                double d2 = entitylivingbase.getZ() - this.parentEntity.getZ();
                this.parentEntity.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
                this.parentEntity.yBodyRot = this.parentEntity.getYRot();
            }
        }
    }
}