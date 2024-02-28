package com.barribob.mm.init;

import java.util.HashMap;
import java.util.Map;

import com.barribob.mm.Main;
import com.barribob.mm.entity.EntityCrimsonPortalSpawn;
import com.barribob.mm.entity.entities.EntityAzureGolem;
import com.barribob.mm.entity.entities.EntityAzureVillager;
import com.barribob.mm.entity.entities.EntityBeast;
import com.barribob.mm.entity.entities.EntityChaosKnight;
import com.barribob.mm.entity.entities.EntityCliffFly;
import com.barribob.mm.entity.entities.EntityCliffGolem;
import com.barribob.mm.entity.entities.EntityDreamElk;
import com.barribob.mm.entity.entities.EntityFloatingSkull;
import com.barribob.mm.entity.entities.EntityGoldenBoss;
import com.barribob.mm.entity.entities.EntityGoldenPillar;
import com.barribob.mm.entity.entities.EntityHerobrineOne;
import com.barribob.mm.entity.entities.EntityHorror;
import com.barribob.mm.entity.entities.EntityIronShade;
import com.barribob.mm.entity.entities.EntityMaelstromBeast;
import com.barribob.mm.entity.entities.EntityMaelstromFury;
import com.barribob.mm.entity.entities.EntityMaelstromHealer;
import com.barribob.mm.entity.entities.EntityMaelstromIllager;
import com.barribob.mm.entity.entities.EntityMaelstromLancer;
import com.barribob.mm.entity.entities.EntityMaelstromMage;
import com.barribob.mm.entity.entities.EntityMaelstromStatueOfNirvana;
import com.barribob.mm.entity.entities.EntityMaelstromWitch;
import com.barribob.mm.entity.entities.EntityMonolith;
import com.barribob.mm.entity.entities.EntityShade;
import com.barribob.mm.entity.entities.EntitySwampCrawler;
import com.barribob.mm.entity.entities.EntityWhiteMonolith;
import com.barribob.mm.entity.entities.Herobrine;
import com.barribob.mm.entity.entities.gauntlet.EntityAlternativeMaelstromGauntletStage1;
import com.barribob.mm.entity.entities.gauntlet.EntityAlternativeMaelstromGauntletStage2;
import com.barribob.mm.entity.entities.gauntlet.EntityCrimsonCrystal;
import com.barribob.mm.entity.entities.gauntlet.EntityMaelstromGauntlet;
import com.barribob.mm.entity.entities.npc.NexusArmorer;
import com.barribob.mm.entity.entities.npc.NexusBladesmith;
import com.barribob.mm.entity.entities.npc.NexusGunTrader;
import com.barribob.mm.entity.entities.npc.NexusMageTrader;
import com.barribob.mm.entity.entities.npc.NexusSpecialTrader;
import com.barribob.mm.entity.particleSpawners.ParticleSpawnerExplosion;
import com.barribob.mm.entity.particleSpawners.ParticleSpawnerRainbow;
import com.barribob.mm.entity.particleSpawners.ParticleSpawnerSwordSwing;
import com.barribob.mm.entity.projectile.EntityGeyser;
import com.barribob.mm.entity.projectile.EntityGoldenRune;
import com.barribob.mm.entity.projectile.EntityHealerOrb;
import com.barribob.mm.entity.projectile.EntityLargeGoldenRune;
import com.barribob.mm.entity.projectile.ProjectileBeastAttack;
import com.barribob.mm.entity.projectile.ProjectileBeastFireball;
import com.barribob.mm.entity.projectile.ProjectileBeastQuake;
import com.barribob.mm.entity.projectile.ProjectileBlackFireball;
import com.barribob.mm.entity.projectile.ProjectileBone;
import com.barribob.mm.entity.projectile.ProjectileBoneQuake;
import com.barribob.mm.entity.projectile.ProjectileBrownstoneCannon;
import com.barribob.mm.entity.projectile.ProjectileBullet;
import com.barribob.mm.entity.projectile.ProjectileChaosFireball;
import com.barribob.mm.entity.projectile.ProjectileCrimsonWanderer;
import com.barribob.mm.entity.projectile.ProjectileExplosiveDrill;
import com.barribob.mm.entity.projectile.ProjectileFireball;
import com.barribob.mm.entity.projectile.ProjectileGoldenFireball;
import com.barribob.mm.entity.projectile.ProjectileGoldenMissile;
import com.barribob.mm.entity.projectile.ProjectileHerobrineQuake;
import com.barribob.mm.entity.projectile.ProjectileHomingFlame;
import com.barribob.mm.entity.projectile.ProjectileHorrorAttack;
import com.barribob.mm.entity.projectile.ProjectileMaelstromCannon;
import com.barribob.mm.entity.projectile.ProjectileMaelstromMeteor;
import com.barribob.mm.entity.projectile.ProjectileMaelstromMissile;
import com.barribob.mm.entity.projectile.ProjectileMaelstromQuake;
import com.barribob.mm.entity.projectile.ProjectileMaelstromRune;
import com.barribob.mm.entity.projectile.ProjectileMaelstromWisp;
import com.barribob.mm.entity.projectile.ProjectileMegaFireball;
import com.barribob.mm.entity.projectile.ProjectileMeteor;
import com.barribob.mm.entity.projectile.ProjectileMeteorSpawner;
import com.barribob.mm.entity.projectile.ProjectileMonolithFireball;
import com.barribob.mm.entity.projectile.ProjectilePiercingBullet;
import com.barribob.mm.entity.projectile.ProjectilePillarFlames;
import com.barribob.mm.entity.projectile.ProjectilePumpkin;
import com.barribob.mm.entity.projectile.ProjectileQuake;
import com.barribob.mm.entity.projectile.ProjectileRepeater;
import com.barribob.mm.entity.projectile.ProjectileRuneWisp;
import com.barribob.mm.entity.projectile.ProjectileSkullAttack;
import com.barribob.mm.entity.projectile.ProjectileStatueMaelstromMissile;
import com.barribob.mm.entity.projectile.ProjectileSwampSpittle;
import com.barribob.mm.entity.projectile.ProjectileSwordSlash;
import com.barribob.mm.entity.projectile.ProjectileWillOTheWisp;
import com.barribob.mm.entity.tileentity.TileEntityBossSpawner;
import com.barribob.mm.entity.tileentity.TileEntityDisappearingSpawner;
import com.barribob.mm.entity.tileentity.TileEntityFan;
import com.barribob.mm.entity.tileentity.TileEntityMalestromSpawner;
import com.barribob.mm.entity.tileentity.TileEntityMegaStructure;
import com.barribob.mm.entity.tileentity.TileEntityTeleporter;
import com.barribob.mm.entity.tileentity.TileEntityUpdater;
import com.barribob.mm.entity.util.EntityAzurePortalSpawn;
import com.barribob.mm.entity.util.EntityCliffPortalSpawn;
import com.barribob.mm.entity.util.EntityCrimsonTowerSpawner;
import com.barribob.mm.entity.util.EntityMaelstromTowerDestroyer;
import com.barribob.mm.entity.util.EntityNexusParticleSpawner;
import com.barribob.mm.entity.util.EntityTuningForkLazer;
import com.barribob.mm.util.Reference;

import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Lists all of the entities in the mod
 */
public class ModEntities {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Reference.MOD_ID);
	
    public static final Vec3i MAELSTROM = new Vec3i(6433126, 3221816, 0);
    public static final Vec3i AZURE = new Vec3i(7248383, 7236306, 0);
    public static final Vec3i NEXUS = new Vec3i(15724287, 12501453, 0);
    public static final Vec3i CLIFF = new Vec3i(0x999966, 0xe6e600, 0);
    public static final Vec3i CLIFF_MAELSTROM = new Vec3i(6433126, 0xe6e600, 0);
    public static final Vec3i CRIMSON_MAELSTROM = new Vec3i(6433126, 0xeb4034, 0);

    private static final Map<Class<? extends Entity>, String> ID_MAP = new HashMap<>();

    public static void registerEntities() {
        registerEntityWithID("shade", EntityShade.class, SHADE_ID, 50, maelstrom);
        registerEntityWithID("horror", EntityHorror.class, HORROR_ID, 50, maelstrom);
        registerEntity("dream_elk", EntityDreamElk.class, DREAM_ELK_ID, 50, azure);
        registerEntityWithID("maelstrom_crawler", EntityBeast.class, BEAST_ID, 100, maelstrom);
        registerEntity("maelstrom_illager", EntityMaelstromIllager.class, MAELSTROM_ILLAGER_ID, 50, maelstrom);
        registerEntity("azure_villager", EntityAzureVillager.class, AZURE_VILLAGER_ID, 100, azure);
        registerEntityWithID("maelstrom_mage", EntityMaelstromMage.class, MAELSTROM_MAGE_ID, 50, maelstrom);
        registerEntity("azure_golem", EntityAzureGolem.class, AZURE_GOLEM_ID, 70, azure);
        registerEntityWithID("floating_skull", EntityFloatingSkull.class, FLOATING_SKULL_ID, 50, maelstrom);
        registerEntity("herobrine_1", EntityHerobrineOne.class, HEROBRINE_1_ID, 50);
        registerEntityWithID("herobrine_controller", Herobrine.class, HEROBRINE_CONTROLLLER, 50, maelstrom);
        registerEntity("nexus_gunsmith", NexusGunTrader.class, NEXUS_GUNSMITH, 50, nexus);
        registerEntity("nexus_mage", NexusMageTrader.class, NEXUS_MAGE, 50, nexus);
        registerEntity("nexus_armorer", NexusArmorer.class, NEXUS_ARMORER, 50, nexus);
        registerEntity("nexus_saiyan", NexusSpecialTrader.class, NEXUS_SAIYAN, 50, nexus);
        registerEntity("nexus_bladesmith", NexusBladesmith.class, NEXUS_BLADESMITH, 50, nexus);
        registerEntityWithID("golden_pillar", EntityGoldenPillar.class, GOLDEN_PILLAR, 50, cliff_maelstrom);
        registerEntityWithID("golden_boss", EntityGoldenBoss.class, GOLDEN_BOSS, 70, cliff_maelstrom);
        registerEntityWithID("maelstrom_witch", EntityMaelstromWitch.class, MAELSTROM_WITCH, 70, cliff_maelstrom);
        registerEntityWithID("cliff_golem", EntityCliffGolem.class, CLIFF_GOLEM, 70, cliff);
        registerEntity("swamp_crawler", EntitySwampCrawler.class, ENTITY_START_ID++, 50, cliff);
        registerEntity("cliff_fly", EntityCliffFly.class, ENTITY_START_ID++, 70, cliff);
        registerEntityWithID("iron_shade", EntityIronShade.class, ENTITY_START_ID++, 70, maelstrom);
        registerEntityWithID("maelstrom_beast", EntityMaelstromBeast.class, ENTITY_START_ID++, 70, maelstrom);
        registerEntity("monolith", EntityMonolith.class, ENTITY_START_ID++, 70, maelstrom);
        registerEntity("white_monolith", EntityWhiteMonolith.class, ENTITY_START_ID++, 70);
        registerEntityWithID("maelstrom_lancer", EntityMaelstromLancer.class, ENTITY_START_ID++, 50, maelstrom);
        registerEntityWithID("chaos_knight", EntityChaosKnight.class, ENTITY_START_ID++, 70, crimson_maelstrom);
        registerEntityWithID("maelstrom_healer", EntityMaelstromHealer.class, ENTITY_START_ID++, 50, maelstrom);
        registerEntityWithID("maelstrom_gauntlet", EntityMaelstromGauntlet.class, ENTITY_START_ID++, 70, crimson_maelstrom);
        registerEntityWithID("maelstrom_statue_of_nirvana", EntityMaelstromStatueOfNirvana.class, ENTITY_START_ID++, 70, cliff_maelstrom);
        registerEntityWithID("maelstrom_fury", EntityMaelstromFury.class, ENTITY_START_ID++, 100, maelstrom);
        registerEntityWithID("alternative_maelstrom_gauntlet_stage_1", EntityAlternativeMaelstromGauntletStage1.class, ENTITY_START_ID++, 100, crimson_maelstrom);
        registerEntityWithID("alternative_maelstrom_gauntlet_stage_2", EntityAlternativeMaelstromGauntletStage2.class, ENTITY_START_ID++, 100, crimson_maelstrom);

        registerEntity("horror_attack", ProjectileHorrorAttack.class, HORROR_ATTACK_ID, 30);
        registerEntity("beast_attack", ProjectileBeastAttack.class, BEAST_ATTACK_ID, 100);
        registerFastProjectile("bullet", ProjectileBullet.class, BULLET_ID, 100);
        registerEntity("maelstrom_cannon", ProjectileMaelstromCannon.class, MAELSTROM_CANNON_ID, 30);
        registerEntity("will-o-the-wisp", ProjectileWillOTheWisp.class, WILL_O_THE_WISP_ID, 30);
        registerEntity("quake", ProjectileQuake.class, QUAKE_ID, 30);
        registerEntity("skull_attack", ProjectileSkullAttack.class, SKULL_ATTACK_ID, 30);
        registerEntity("azure_portal_spawn", EntityAzurePortalSpawn.class, AZURE_PORTAL_SPAWN_ID, 100);
        registerFastProjectile("pumpkin", ProjectilePumpkin.class, PUMPKIN_ID, 1000);
        registerEntity("repeater", ProjectileRepeater.class, REPEATER_ID, 30);
        registerEntity("fireball", ProjectileFireball.class, FIREBALL_ID, 30);
        registerEntity("herobrine_slash", ProjectileHerobrineQuake.class, HEROBRINE_SLASH_ID, 30);
        registerEntity("black_fireball", ProjectileBlackFireball.class, BLACK_FIREBALL_ID, 30);
        registerEntity("pillar_flames", ProjectilePillarFlames.class, PILLAR_FLAMES_ID, 30);
        registerEntity("golden_rune", EntityGoldenRune.class, GOLDEN_RUNE_ID, 30);
        registerEntity("golden_mage_attack", ProjectileGoldenMissile.class, GOLDEN_MAGE_ATTACK_ID, 30);
        registerEntity("golden_fireball", ProjectileGoldenFireball.class, GOLDEN_FIREBALL_ID, 30);
        registerEntity("maelstrom_quake", ProjectileMaelstromQuake.class, MAELSTROM_QUAKE_ID, 30);
        registerEntity("maelstrom_missile", ProjectileMaelstromMissile.class, WOOD_ID, 30);
        registerEntity("geyser", EntityGeyser.class, GEYSER_ID, 30);
        registerEntity("brownstone_cannon", ProjectileBrownstoneCannon.class, BROWNSTONE_CANNON_ID, 30);
        registerEntity("cliff_portal_spawn", EntityCliffPortalSpawn.class, CLIFF_PORTAL_SPAWN, 30);
        registerEntity("explosive_drill", ProjectileExplosiveDrill.class, EXPLOSIVE_DRILL, 30);
        registerFastProjectile("azure_bullet", ProjectilePiercingBullet.class, AZURE_BULLET, 100);
        registerEntity("swamp_spittle", ProjectileSwampSpittle.class, PROJECTILE_START_ID++, 30);
        registerEntity("nexus_particle", EntityNexusParticleSpawner.class, PROJECTILE_START_ID++, 50);
        registerEntity("maelstrom_wisp", ProjectileMaelstromWisp.class, PROJECTILE_START_ID++, 50);
        registerEntity("meteor", ProjectileMeteor.class, PROJECTILE_START_ID++, 100);
        registerEntity("meteor_spawner", ProjectileMeteorSpawner.class, PROJECTILE_START_ID++, 50);
        registerEntity("sword_slash", ProjectileSwordSlash.class, PROJECTILE_START_ID++, 50);
        registerEntity("beast_quake", ProjectileBeastQuake.class, PROJECTILE_START_ID++, 50);
        registerEntity("bone", ProjectileBone.class, PROJECTILE_START_ID++, 30);
        registerEntity("bone_quake", ProjectileBoneQuake.class, PROJECTILE_START_ID++, 50);
        registerEntity("monolith_fireball", ProjectileMonolithFireball.class, PROJECTILE_START_ID++, 50);
        registerEntity("maelstrom_meteor", ProjectileMaelstromMeteor.class, PROJECTILE_START_ID++, 50);
        registerEntity("large_golden_rune", EntityLargeGoldenRune.class, PROJECTILE_START_ID++, 40);
        registerEntity("crimson_tower_spawner", EntityCrimsonTowerSpawner.class, PROJECTILE_START_ID++, 40);
        registerEntity("healer_orb", EntityHealerOrb.class, PROJECTILE_START_ID++, 40);
        registerEntity("chaos_fireball", ProjectileChaosFireball.class, PROJECTILE_START_ID++, 40);
        registerEntity("rune_wisp", ProjectileRuneWisp.class, PROJECTILE_START_ID++, 40);
        registerEntity("crimson_portal_spawn", EntityCrimsonPortalSpawn.class, PROJECTILE_START_ID++, 40);
        registerEntity("tuning_fork_lazer_renderer", EntityTuningForkLazer.class, PROJECTILE_START_ID++, 60);
        registerEntity("mega_fireball", ProjectileMegaFireball.class, PROJECTILE_START_ID++, 40);
        registerEntity("statue_maelstrom_missile", ProjectileStatueMaelstromMissile.class, PROJECTILE_START_ID++, 30);
        registerEntity("maelstrom_rune", ProjectileMaelstromRune.class, PROJECTILE_START_ID++, 40);
        registerEntity("beast_fireball", ProjectileBeastFireball.class, PROJECTILE_START_ID++, 40);
        registerEntity("homing_flame", ProjectileHomingFlame.class, PROJECTILE_START_ID++, 50);
        registerEntity("crimson_wanderer", ProjectileCrimsonWanderer.class, PROJECTILE_START_ID++, 60);
        registerEntity("crimson_crystal", EntityCrimsonCrystal.class, PROJECTILE_START_ID++, 60);

        registerEntity("explosion_particle", ParticleSpawnerExplosion.class, PARTICLE_START_ID++, 20);
        registerEntity("black_gold_sword_particle", ParticleSpawnerSwordSwing.class, PARTICLE_START_ID++, 20);
        registerEntity("rainbow_particle", ParticleSpawnerRainbow.class, PARTICLE_START_ID++, 20);
        registerEntity("maelstrom_tower_destroyer", EntityMaelstromTowerDestroyer.class, PARTICLE_START_ID++, 20);

        registerTileEntity(TileEntityMalestromSpawner.class, "spawner");
        registerTileEntity(TileEntityDisappearingSpawner.class, "maelstrom_spawner");
        registerTileEntity(TileEntityMegaStructure.class, "mega_structure");
        registerTileEntity(TileEntityTeleporter.class, "nexus_teleporter");
        registerTileEntity(TileEntityBossSpawner.class, "nexus_spawner");
        registerTileEntity(TileEntityUpdater.class, "updater");
        registerTileEntity(TileEntityFan.class, "fan");
    }

    public static String getID(Class<? extends Entity> entity) {
        if (ID_MAP.containsKey(entity)) {
            return Reference.MOD_ID + ":" + ID_MAP.get(entity);
        }
        throw new IllegalArgumentException("Mapping of an entity has not be registered for the maelstrom mod spawner system.");
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityWithID(String name, Class<? extends Entity> entity, int id, int range, Vec3i eggColor) {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":" + name), entity, name, id, Main.instance, range, 1, true, eggColor.getX(), eggColor.getY());
        ID_MAP.put(entity, name);
        return ENTITY_TYPES.register(name, null);
    }

    private static void registerEntity(String name, Class<? extends Entity> entity, int id, int range, Vec3i eggColor) {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":" + name), entity, name, id, Main.instance, range, 1, true, eggColor.getX(), eggColor.getY());
    }

    private static void registerEntity(String name, Class<? extends Entity> entity, int id, int range) {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":" + name), entity, name, id, Main.instance, range, 1, true);
    }

    private static void registerFastProjectile(String name, Class<? extends Entity> entity, int id, int range) {
        EntityRegistry.registerModEntity(new ResourceLocation(Reference.MOD_ID + ":" + name), entity, name, id, Main.instance, range, 1, false);
    }

    private static void registerTileEntity(Class<? extends BlockEntity> entity, String name) {
        GameRegistry.registerTileEntity(entity, new ResourceLocation(Reference.MOD_ID + ":" + name));
    }
}
