package com.barribob.mm.entity.action;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.util.IEntityAdjustment;

public class ActionRuneAttack implements IAction {
    Supplier<ModProjectile> projectileSupplier;
    IEntityAdjustment adjustment;
    private static final float zeroish = 0.001f;

    public ActionRuneAttack(Supplier<ModProjectile> projectileSupplier, IEntityAdjustment adjustment) {
        this.projectileSupplier = projectileSupplier;
        this.adjustment = adjustment;
    }

    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        ModProjectile projectile = projectileSupplier.get();
        projectile.shoot(zeroish, zeroish, zeroish, zeroish, zeroish);
        adjustment.adjust(projectile);
        projectile.setTravelRange(50);
        actor.level.addFreshEntity(projectile);
    }
}
