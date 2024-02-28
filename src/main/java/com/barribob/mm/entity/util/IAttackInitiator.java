package com.barribob.mm.entity.util;

import net.minecraft.world.entity.LivingEntity;

public interface IAttackInitiator {
    void update(LivingEntity target);
    void stop();
}
