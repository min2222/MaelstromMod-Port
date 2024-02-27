package com.barribob.MaelstromMod.renderer;

import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public interface ITarget {
    Optional<Vec3> getTarget();
}
