package com.barribob.mm.entity.action;

import com.barribob.mm.entity.entities.EntityLeveledMob;

import net.minecraft.world.entity.LivingEntity;

/*
 * Base interface for entity actions for example, Shooting, melee attack, and other actions
 */
public interface IAction {
    void performAction(EntityLeveledMob actor, LivingEntity target);

    IAction NONE = (actor, target) -> {
    };
}