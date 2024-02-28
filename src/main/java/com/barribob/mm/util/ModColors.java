package com.barribob.mm.util;

import net.minecraft.world.phys.Vec3;

public class ModColors {
    public static final Vec3 YELLOW = new Vec3(0.8, 0.8, 0.4);
    public static final Vec3 PURPLE = new Vec3(0.8, 0.4, 0.8);
    public static final Vec3 CLIFF_STONE = new Vec3(0.7, 0.7, 0.5);
    public static final Vec3 WHITE = new Vec3(1, 1, 1);
    public static final Vec3 GREY = new Vec3(0.5, 0.5, 0.5);
    public static final Vec3 BROWNSTONE = new Vec3(0.8, 0.5, 0.0);
    public static final Vec3 AZURE = new Vec3(0.2, 0.8, 0.8);
    public static final Vec3 ORANGE = new Vec3(0.9, 0.7, 0.4);
    public static final Vec3 RED = new Vec3(0.9, 0.1, 0.1);
    public static final Vec3 GREEN = new Vec3(0.1, 0.9, 0.1);
    public static final Vec3 BLUE = new Vec3(0.1, 0.1, 0.8);
    public static final Vec3 MAELSTROM = new Vec3(0.3, 0.2, 0.4);
    public static final Vec3 DARK_GREY = new Vec3(0.2, 0.2, 0.2);
    public static final Vec3 FADED_RED = new Vec3(0.7, 0.3, 0.3);
    public static final Vec3 FIREBALL_ORANGE = new Vec3(1.0, 0.6, 0.5);
    public static final Vec3 SWAMP_FOG = new Vec3(0.4, 0.35, 0.2);

    public static Vec3 variateColor(Vec3 baseColor, float variance) {
        float f = ModRandom.getFloat(variance);

        return new Vec3((float) Math.min(Math.max(0, baseColor.x + f), 1),
                (float) Math.min(Math.max(0, baseColor.y + f), 1),
                (float) Math.min(Math.max(0, baseColor.z + f), 1));
    }

    public static int toIntegerColor(int r, int g, int b, int a) {
        int i = r << 16;
        i += g << 8;
        i += b;
        i += a << 24;
        return i;
    }
}
