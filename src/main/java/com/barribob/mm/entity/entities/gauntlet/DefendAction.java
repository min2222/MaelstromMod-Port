package com.barribob.mm.entity.entities.gauntlet;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;

import java.util.function.Function;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;

public class DefendAction implements IGauntletAction {
    private final EntityLeveledMob entity;
    private boolean isDefending;

    public DefendAction(EntityLeveledMob entity) {
        this.entity = entity;
    }

    @Override
    public void doAction() {
        ModBBAnimations.animation(entity, "gauntlet.defend", false);
        entity.addEvent(() -> {
            isDefending = true;
            entity.height = 2;
        }, 5);
        entity.addEvent(() -> {
            isDefending = false;
            entity.height = 4;
        }, 22);
    }

    @Override
    public void update() {
        if(isDefending) {
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.MOB)
                    .directEntity(entity)
                    .stoppedByArmorNotShields()
                    .element(entity.getElement()).build();

            Function<Entity, Float> kineticDamage = (e) -> entity.getAttack() * (float) ModUtils.getEntityVelocity(e).length();
            ModUtils.handleAreaImpact(1.0f, kineticDamage, entity,
                    entity.getEyePosition(1), source, 1, 0, false);
        }
    }

    @Override
    public boolean isImmuneToDamage() {
        return isDefending;
    }

    @Override
    public int attackLength() {
        return 23;
    }

    @Override
    public int attackCooldown() {
        return 20;
    }
}
