package com.barribob.mm.entity.action;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;
import java.util.function.Supplier;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

public class ActionVolley implements IAction {
    Supplier<ModProjectile> projectileSupplier;
    float velocity;

    public ActionVolley(Supplier<ModProjectile> projectileSupplier, float velocity) {
        this.projectileSupplier = projectileSupplier;
        this.velocity = velocity;
    }

    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        Function<Vec3, Runnable> missile = (offset) -> () -> {
            ModProjectile projectile = projectileSupplier.get();
            projectile.setTravelRange(40);

            ModUtils.throwProjectile(actor, target.getEyePosition(1),
                    projectile,
                    6.0f,
                    velocity,
                    offset);

            actor.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, ModRandom.getFloat(0.2f) + 1.3f);
        };

        actor.addEvent(missile.apply(ModUtils.getRelativeOffset(actor, new Vec3(0, 1, 1))), 20);
        actor.addEvent(missile.apply(ModUtils.getRelativeOffset(actor, new Vec3(0, 1, -1))), 20);
        actor.addEvent(missile.apply(ModUtils.getRelativeOffset(actor, new Vec3(0, 0.5, 1.7))), 25);
        actor.addEvent(missile.apply(ModUtils.getRelativeOffset(actor, new Vec3(0, 0.5, -1.7))), 25);
        actor.addEvent(missile.apply(ModUtils.getRelativeOffset(actor, new Vec3(0, 0, 2))), 30);
        actor.addEvent(missile.apply(ModUtils.getRelativeOffset(actor, new Vec3(0, 0, -2))), 30);
        actor.addEvent(missile.apply(ModUtils.getRelativeOffset(actor, new Vec3(0, -0.5, 2.5))), 35);
        actor.addEvent(missile.apply(ModUtils.getRelativeOffset(actor, new Vec3(0, -0.5, -2.5))), 35);
    }
}
