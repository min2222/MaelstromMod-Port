package com.barribob.MaelstromMod.util;

import com.google.common.collect.Maps;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

/**
 * The data for the elemental system
 */
public enum Element {
    AZURE(new Vec3(0.5, 0.5, 1.0), ModColors.AZURE, "\u03A6", ChatFormatting.AQUA, 0),
    GOLDEN(new Vec3(1.0, 1.0, 0.5), ModColors.YELLOW, "\u03A9", ChatFormatting.YELLOW, 1),
    CRIMSON(new Vec3(1.0, 0.5, 0.5), ModColors.RED, "\u03A3", ChatFormatting.RED, 2),
    NONE(ModColors.WHITE, ModColors.MAELSTROM, " ", ChatFormatting.WHITE, 3);

    public Vec3 sweepColor;
    public Vec3 particleColor;
    public String symbol;
    public ChatFormatting textColor;
    public int id;
    private static final Map<Integer, Element> FROM_ID = Maps.<Integer, Element>newHashMap();

    private Element(Vec3 sweepColor, Vec3 particleColor, String symbol, ChatFormatting textColor, int id) {
        this.sweepColor = sweepColor;
        this.particleColor = particleColor;
        this.symbol = symbol;
        this.textColor = textColor;
        this.id = id;
    }

    /*
     * A function to determine where elemental effects should be applied given a
     * certain element
     */
    public boolean matchesElement(Element e) {
        return this == e && e != NONE && this != NONE;
    }

    public static Element getElementFromId(int id) {
        return FROM_ID.get(Integer.valueOf(id));
    }

    static {
        for (Element element : values()) {
            FROM_ID.put(Integer.valueOf(element.id), element);
        }
    }
}
