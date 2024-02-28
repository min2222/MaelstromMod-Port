package com.barribob.mm.util.handlers;

import com.barribob.mm.util.Reference;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Keeps track of all the sound resources and registers them
 */
public class SoundsHandler {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Reference.MOD_ID);
    public static RegistryObject<SoundEvent> ENTITY_SHADE_AMBIENT;
    public static RegistryObject<SoundEvent> ENTITY_SHADE_HURT;
    public static RegistryObject<SoundEvent> ENTITY_HORROR_AMBIENT;
    public static RegistryObject<SoundEvent> ENTITY_HORROR_HURT;
    public static RegistryObject<SoundEvent> ENTITY_BEAST_AMBIENT;
    public static RegistryObject<SoundEvent> ENTITY_BEAST_HURT;
    public static RegistryObject<SoundEvent> ENTTIY_CRAWLER_AMBIENT;
    public static RegistryObject<SoundEvent> ENTTIY_CRAWLER_HURT;
    public static RegistryObject<SoundEvent> ENTITY_MONOLITH_AMBIENT;
    public static RegistryObject<SoundEvent> ENTITY_CHAOS_KNIGHT_BLOCK;
    public static RegistryObject<SoundEvent> ENTITY_CHAOS_KNIGHT_AMBIENT;
    public static RegistryObject<SoundEvent> ENTITY_CHAOS_KNIGHT_HURT;
    public static RegistryObject<SoundEvent> ENTITY_CHAOS_KNIGHT_DEATH;
    public static RegistryObject<SoundEvent> ENTITY_GAUNTLET_AMBIENT;
    public static RegistryObject<SoundEvent> ENTITY_GAUNTLET_HURT;
    public static RegistryObject<SoundEvent> ENTITY_GAUNTLET_LAZER_CHARGE;
    public static RegistryObject<SoundEvent> NONE;

    // Sound hooks
    public static class Hooks{
        public static RegistryObject<SoundEvent> ENTITY_ILLAGER_SPELL_CHARGE;
        public static RegistryObject<SoundEvent> ENTITY_ILLAGER_DOME_CHARGE;
        public static RegistryObject<SoundEvent> ENTITY_ILLAGER_VORTEX;
        public static RegistryObject<SoundEvent> ENTITY_ILLAGER_DOME;
    }

    static {
        ENTITY_HORROR_AMBIENT = registerSound("horror.ambient");
        ENTITY_HORROR_HURT = registerSound("horror.hurt");

        ENTITY_SHADE_AMBIENT = registerSound("shade.ambient");
        ENTITY_SHADE_HURT = registerSound("shade.hurt");

        ENTITY_BEAST_AMBIENT = registerSound("beast.ambient");
        ENTITY_BEAST_HURT = registerSound("beast.hurt");

        ENTTIY_CRAWLER_AMBIENT = registerSound("swamp_crawler.ambient");
        ENTTIY_CRAWLER_HURT = registerSound("swamp_crawler.hurt");
        ENTITY_MONOLITH_AMBIENT = registerSound("monolith.ambient");
        ENTITY_CHAOS_KNIGHT_BLOCK = registerSound("chaos_knight.block");
        ENTITY_CHAOS_KNIGHT_AMBIENT = registerSound("chaos_knight.ambient");
        ENTITY_CHAOS_KNIGHT_HURT = registerSound("chaos_knight.hurt");
        ENTITY_CHAOS_KNIGHT_DEATH = registerSound("chaos_knight.death");
        ENTITY_GAUNTLET_AMBIENT = registerSound("gauntlet.ambient");
        ENTITY_GAUNTLET_HURT = registerSound("gauntlet.hurt");
        ENTITY_GAUNTLET_LAZER_CHARGE = registerSound("gauntlet.lazer_charge");
        NONE = registerSound("none");

        Hooks.ENTITY_ILLAGER_SPELL_CHARGE = registerSound("illager.spell_charge");
        Hooks.ENTITY_ILLAGER_DOME_CHARGE = registerSound("illager.dome_charge");
        Hooks.ENTITY_ILLAGER_VORTEX = registerSound("illager.vortex");
        Hooks.ENTITY_ILLAGER_DOME = registerSound("illager.dome");
    }

    private static RegistryObject<SoundEvent> registerSound(String name) {
        ResourceLocation location = new ResourceLocation(Reference.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(location));
    }
}
