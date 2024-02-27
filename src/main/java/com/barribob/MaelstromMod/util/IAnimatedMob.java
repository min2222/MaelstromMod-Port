package com.barribob.MaelstromMod.util;

import com.barribob.MaelstromMod.entity.animation.Animation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAnimatedMob {
    public static final byte animationByte = 13;

    @OnlyIn(Dist.CLIENT)
    public Animation getCurrentAnimation();
}
