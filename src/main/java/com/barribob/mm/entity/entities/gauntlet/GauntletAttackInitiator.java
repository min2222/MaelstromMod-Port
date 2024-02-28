package com.barribob.mm.entity.entities.gauntlet;

import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

import com.barribob.mm.entity.util.IAttackInitiator;

public class GauntletAttackInitiator implements IAttackInitiator {
    private int attackTime;
    private final int startingCooldown;
    private int attackCooldown;
    private final Function<LivingEntity, IGauntletAction> attack;
    private final Function<LivingEntity, IGauntletAction> overrideAction;

    public GauntletAttackInitiator(int startingCooldown, Function<LivingEntity, IGauntletAction> attack, Function<LivingEntity, IGauntletAction> overrideAction) {
        this.attack = attack;
        this.overrideAction = overrideAction;
        this.startingCooldown = startingCooldown;
        this.attackTime = startingCooldown;
    }

    @Override
    public void update(LivingEntity target) {
        attackTime--;
        if (attackTime <= 0) {
            IGauntletAction action = attack.apply(target);
            attackTime = action.attackLength() + action.attackCooldown();
            attackCooldown = action.attackCooldown();
        }
        else if (attackTime <= attackCooldown) {
            IGauntletAction action = overrideAction.apply(target);
            if (action != null) {
                attackTime = action.attackLength() + action.attackCooldown();
                attackCooldown = action.attackCooldown();
            }
        }
    }

    @Override
    public void stop() {
        this.attackTime = Math.max(attackTime, startingCooldown);
        attackCooldown = 0;
    }
}
