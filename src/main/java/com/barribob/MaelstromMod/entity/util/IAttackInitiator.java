package com.barribob.MaelstromMod.entity.util;

import net.minecraft.world.entity.LivingEntity;

public interface IAttackInitiator {
    void update(LivingEntity target);
    void resetTask();
}
