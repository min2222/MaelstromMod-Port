package com.barribob.MaelstromMod.entity.util;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Allows mobs to communicate directions so that it can be indicated on the client (in the form of lazers for example)
 *
 * @author Barribob
 */
public interface DirectionalRender {
    @OnlyIn(Dist.CLIENT)
    public void setRenderDirection(Vec3 dir);
}
