package com.barribob.mm.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Allows tools that override this interface to replace the standard sword sweep attack with a custom one
 */
public interface ISweepAttackOverride {
    public void doSweepAttack(Player player, @Nullable LivingEntity entity);
}
