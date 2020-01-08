package com.barribob.MaelstromMod.util.handlers;

import com.barribob.MaelstromMod.config.ModConfig;

/**
 * 
 * The class organizes all of the level dependent stats including, armor,
 * weapons, guns, and certain entity stats
 *
 */
public class LevelHandler
{   
    public static final float INVASION = 0.0f;
    public static final float AZURE_OVERWORLD = 1.0f;
    public static final float AZURE_ENDGAME = 2.0f;

    /**
     * Calculates the armor by using the progression level as a base and the level
     * as the negative exponent.
     * 
     * For example, the default progression is 2, which means a level 1 armor would
     * provide 50% damage reduction
     * 
     */
    public static float getArmorFromLevel(float level)
    {
	return (float) Math.pow(ModConfig.balance.progression_scale, -level);
    }

    /**
     * Get the maximum damage reduction a completely enchanted armor set can have.
     * 
     * This is based on the progression scale. For for a scale of 1.5, the reduction
     * is 1 / (1.5^2)
     */
    public static float getMaxMaelstromProtection()
    {
	return 1 - (float) Math.pow(ModConfig.balance.progression_scale, -2);
    }
    
    /**
     * returns a simple multiplicative modifier based on the level Starts at level 0
     * (returns a multiplier of 1)
     */
    public static float getMultiplierFromLevel(float level)
    {
	return (float) Math.pow(ModConfig.balance.progression_scale, level);
    }
}
