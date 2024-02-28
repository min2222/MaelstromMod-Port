package com.barribob.mm.renderer;

import java.util.Optional;

import net.minecraft.world.phys.Vec3;

public interface ITarget {
    Optional<Vec3> getLazerTarget();
}
