package com.barribob.mm.entity.entities.gauntlet;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class MovementTracker {
    public final Entity entity;
    private final int maxTicksTracking;
    private final List<Vec3> positions = new ArrayList<>();

    public MovementTracker(Entity entity, int maxTicksTracking) {
        this.entity = entity;
        this.maxTicksTracking = maxTicksTracking;
    }

    public Vec3 getMovementOverTicks(int ticks) {
        if(ticks > maxTicksTracking) throw new IllegalArgumentException("Ticks was larger than tick tracking");
        if(positions.size() == 0) return Vec3.ZERO;
        ticks = Math.min(ticks, positions.size() - 1);
        Vec3 firstPos = positions.get(0);
        Vec3 secondPos = positions.get(ticks);
        return secondPos.subtract(firstPos);
    }

    public void tick() {
        positions.add(entity.position());
        if(positions.size() > maxTicksTracking) positions.remove(0);
    }
}
