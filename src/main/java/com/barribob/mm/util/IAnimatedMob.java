package com.barribob.mm.util;

import com.barribob.mm.entity.animation.Animation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAnimatedMob {
    public static final byte animationByte = 13;

    @OnlyIn(Dist.CLIENT)
    public Animation getCurrentAnimation();
}
