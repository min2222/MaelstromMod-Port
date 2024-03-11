package com.barribob.mm.init;

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
import net.minecraft.world.entity.MobCategory;
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
    public static final RegistryObject<EntityType<EntityShade>> SHADE = registerEntityWithID("shade", createBuilder(EntityShade::new, MobCategory.MONSTER).sized(0.9f, 1.8f), 50, MAELSTROM);
    public static final RegistryObject<EntityType<EntityHorror>> HORROR = registerEntityWithID("horror", createBuilder(EntityHorror::new, MobCategory.MONSTER).sized(1.3F, 1.3F), 50, MAELSTROM);
    public static final RegistryObject<EntityType<EntityDreamElk>> DREAM_ELK = registerEntity("dream_elk", createBuilder(EntityDreamElk::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F), 50, AZURE);
    public static final RegistryObject<EntityType<EntityBeast>> MAELSTROM_CRAWLER = registerEntityWithID("maelstrom_crawler", createBuilder(EntityBeast::new, MobCategory.MONSTER).sized(2.8f, 2.2f), 100, MAELSTROM);
    public static final RegistryObject<EntityType<EntityMaelstromIllager>> MAELSTROM_ILLAGER = registerEntity("maelstrom_illager", EntityMaelstromIllager.class, MAELSTROM_ILLAGER_ID, 50, MAELSTROM);
    public static final RegistryObject<EntityType<EntityAzureVillager>> AZURE_VILLAGER = registerEntity("azure_villager", EntityAzureVillager.class, AZURE_VILLAGER_ID, 100, azure);
    public static final RegistryObject<EntityType<EntityMaelstromMage>> MAESLTROM_MAGE = registerEntityWithID("maelstrom_mage", EntityMaelstromMage.class, MAELSTROM_MAGE_ID, 50, maelstrom);
    public static final RegistryObject<EntityType<EntityAzureGolem>> AZURE_GOLEM = registerEntity("azure_golem", EntityAzureGolem.class, AZURE_GOLEM_ID, 70, azure);
    public static final RegistryObject<EntityType<EntityFloatingSkull>> FLOATING_SKULL = registerEntityWithID("floating_skull", EntityFloatingSkull.class, FLOATING_SKULL_ID, 50, maelstrom);
    public static final RegistryObject<EntityType<EntityHerobrineOne>> HEROBRINE_1 = registerEntity("herobrine_1", EntityHerobrineOne.class, HEROBRINE_1_ID, 50);
    public static final RegistryObject<EntityType<Herobrine>> HEROBRINE_CONTROLLER = registerEntityWithID("herobrine_controller", Herobrine.class, HEROBRINE_CONTROLLLER, 50, maelstrom);
    public static final RegistryObject<EntityType<NexusGunTrader>> NEXUS_GUNSMITH = registerEntity("nexus_gunsmith", NexusGunTrader.class, NEXUS_GUNSMITH, 50, nexus);
    public static final RegistryObject<EntityType<NexusMageTrader>> NEXUS_MAGE = registerEntity("nexus_mage", NexusMageTrader.class, NEXUS_MAGE, 50, nexus);
    public static final RegistryObject<EntityType<NexusArmorer>> NEXUS_ARMORER = registerEntity("nexus_armorer", NexusArmorer.class, NEXUS_ARMORER, 50, nexus);
    public static final RegistryObject<EntityType<NexusSpecialTrader>> NEXUS_SAIYAN = registerEntity("nexus_saiyan", NexusSpecialTrader.class, NEXUS_SAIYAN, 50, nexus);
    public static final RegistryObject<EntityType<NexusBladesmith>> NEXUS_BLADESMITH = registerEntity("nexus_bladesmith", NexusBladesmith.class, NEXUS_BLADESMITH, 50, nexus);
    public static final RegistryObject<EntityType<EntityGoldenPillar>> GOLDEN_PILLAR = registerEntityWithID("golden_pillar", EntityGoldenPillar.class, GOLDEN_PILLAR, 50, cliff_maelstrom);
    public static final RegistryObject<EntityType<EntityGoldenBoss>> GOLDEN_BOSS = registerEntityWithID("golden_boss", EntityGoldenBoss.class, GOLDEN_BOSS, 70, cliff_maelstrom);
    public static final RegistryObject<EntityType<EntityMaelstromWitch>> MAELSTROM_WITCH = registerEntityWithID("maelstrom_witch", EntityMaelstromWitch.class, MAELSTROM_WITCH, 70, cliff_maelstrom);
    public static final RegistryObject<EntityType<EntityCliffGolem>> CLIFF_GOLEM = registerEntityWithID("cliff_golem", EntityCliffGolem.class, CLIFF_GOLEM, 70, cliff);
    public static final RegistryObject<EntityType<EntitySwampCrawler>> SWAMP_CRAWLER = registerEntity("swamp_crawler", EntitySwampCrawler.class, ENTITY_START_ID++, 50, cliff);
    public static final RegistryObject<EntityType<EntityCliffFly>> CLIFF_FLY = registerEntity("cliff_fly", EntityCliffFly.class, ENTITY_START_ID++, 70, cliff);
    public static final RegistryObject<EntityType<EntityIronShade>> IRON_SHADE = registerEntityWithID("iron_shade", EntityIronShade.class, ENTITY_START_ID++, 70, maelstrom);
    public static final RegistryObject<EntityType<EntityMaelstromBeast>> MAELSTROM_BEAST = registerEntityWithID("maelstrom_beast", EntityMaelstromBeast.class, ENTITY_START_ID++, 70, maelstrom);
    public static final RegistryObject<EntityType<EntityMonolith>> MONOLITH = registerEntity("monolith", EntityMonolith.class, ENTITY_START_ID++, 70, maelstrom);
    public static final RegistryObject<EntityType<EntityWhiteMonolith>> WHITHE_MONOLITH = registerEntity("white_monolith", EntityWhiteMonolith.class, ENTITY_START_ID++, 70);
    public static final RegistryObject<EntityType<EntityMaelstromLancer>> MAELSTROM_LANCER = registerEntityWithID("maelstrom_lancer", EntityMaelstromLancer.class, ENTITY_START_ID++, 50, maelstrom);
    public static final RegistryObject<EntityType<EntityChaosKnight>> CHAOS_KNIGHT = registerEntityWithID("chaos_knight", EntityChaosKnight.class, ENTITY_START_ID++, 70, crimson_maelstrom);
    public static final RegistryObject<EntityType<EntityMaelstromHealer>> MAELSTROM_HEALER = registerEntityWithID("maelstrom_healer", EntityMaelstromHealer.class, ENTITY_START_ID++, 50, maelstrom);
    public static final RegistryObject<EntityType<EntityMaelstromGauntlet>> MAELSTROM_GUANTLET = registerEntityWithID("maelstrom_gauntlet", EntityMaelstromGauntlet.class, ENTITY_START_ID++, 70, crimson_maelstrom);
    public static final RegistryObject<EntityType<EntityMaelstromStatueOfNirvana>> MAELSTROM_STATUE_OF_NIRVANA = registerEntityWithID("maelstrom_statue_of_nirvana", EntityMaelstromStatueOfNirvana.class, ENTITY_START_ID++, 70, cliff_maelstrom);
    public static final RegistryObject<EntityType<EntityMaelstromFury>> MAELSTROM_FURY = registerEntityWithID("maelstrom_fury", EntityMaelstromFury.class, ENTITY_START_ID++, 100, maelstrom);
    public static final RegistryObject<EntityType<EntityAlternativeMaelstromGauntletStage1>> ALTERNATIVE_MAELSTROM_GAUNTLET_STAGE_1 = registerEntityWithID("alternative_maelstrom_gauntlet_stage_1", EntityAlternativeMaelstromGauntletStage1.class, ENTITY_START_ID++, 100, crimson_maelstrom);
    public static final RegistryObject<EntityType<EntityAlternativeMaelstromGauntletStage2>> ALTERNATIVE_MAELSTROM_GAUNTLET_STAGE_2 = registerEntityWithID("alternative_maelstrom_gauntlet_stage_2", EntityAlternativeMaelstromGauntletStage2.class, ENTITY_START_ID++, 100, crimson_maelstrom);

    public static final RegistryObject<EntityType<ProjectileHorrorAttack>> HORROR_ATTACK = registerEntity("horror_attack", ProjectileHorrorAttack.class, HORROR_ATTACK_ID, 30);
    public static final RegistryObject<EntityType<ProjectileBeastAttack>> BEAST_ATTACK = registerEntity("beast_attack", ProjectileBeastAttack.class, BEAST_ATTACK_ID, 100);
    public static final RegistryObject<EntityType<ProjectileBullet>> BULLET = registerFastProjectile("bullet", ProjectileBullet.class, BULLET_ID, 100);
    public static final RegistryObject<EntityType<ProjectileMaelstromCannon>> MAELSTROM_CANNON = registerEntity("maelstrom_cannon", ProjectileMaelstromCannon.class, MAELSTROM_CANNON_ID, 30);
    public static final RegistryObject<EntityType<ProjectileWillOTheWisp>> WILL_O_THE_WISP = registerEntity("will_o_the_wisp", ProjectileWillOTheWisp.class, WILL_O_THE_WISP_ID, 30);
    public static final RegistryObject<EntityType<ProjectileQuake>> QUAKE = registerEntity("quake", ProjectileQuake.class, QUAKE_ID, 30);
    public static final RegistryObject<EntityType<ProjectileSkullAttack>> SKULL_ATTACK = registerEntity("skull_attack", ProjectileSkullAttack.class, SKULL_ATTACK_ID, 30);
    public static final RegistryObject<EntityType<EntityAzurePortalSpawn>> AZURE_PORTAL_SPAWN = registerEntity("azure_portal_spawn", EntityAzurePortalSpawn.class, AZURE_PORTAL_SPAWN_ID, 100);
    public static final RegistryObject<EntityType<ProjectilePumpkin>> PUMPKIN = registerFastProjectile("pumpkin", ProjectilePumpkin.class, PUMPKIN_ID, 1000);
    public static final RegistryObject<EntityType<ProjectileRepeater>> REPEATER = registerEntity("repeater", ProjectileRepeater.class, REPEATER_ID, 30);
    public static final RegistryObject<EntityType<ProjectileFireball>> FIREBALL = registerEntity("fireball", ProjectileFireball.class, FIREBALL_ID, 30);
    public static final RegistryObject<EntityType<ProjectileHerobrineQuake>> HEROBRINE_SLASH = registerEntity("herobrine_slash", ProjectileHerobrineQuake.class, HEROBRINE_SLASH_ID, 30);
    public static final RegistryObject<EntityType<ProjectileBlackFireball>> BLACK_FIREBALL = registerEntity("black_fireball", ProjectileBlackFireball.class, BLACK_FIREBALL_ID, 30);
    public static final RegistryObject<EntityType<ProjectilePillarFlames>> PILLAR_FLAMES = registerEntity("pillar_flames", ProjectilePillarFlames.class, PILLAR_FLAMES_ID, 30);
    public static final RegistryObject<EntityType<EntityGoldenRune>> GOLDEN_RUNE = registerEntity("golden_rune", EntityGoldenRune.class, GOLDEN_RUNE_ID, 30);
    public static final RegistryObject<EntityType<ProjectileGoldenMissile>> GOLDEN_MAGE_ATTACK = registerEntity("golden_mage_attack", ProjectileGoldenMissile.class, GOLDEN_MAGE_ATTACK_ID, 30);
    public static final RegistryObject<EntityType<ProjectileGoldenFireball>> GOLDEN_FIREBALL = registerEntity("golden_fireball", ProjectileGoldenFireball.class, GOLDEN_FIREBALL_ID, 30);
    public static final RegistryObject<EntityType<ProjectileMaelstromQuake>> MAELSTROM_QUAKE = registerEntity("maelstrom_quake", ProjectileMaelstromQuake.class, MAELSTROM_QUAKE_ID, 30);
    public static final RegistryObject<EntityType<ProjectileMaelstromMissile>> MAELSTROM_MISSILE = registerEntity("maelstrom_missile", ProjectileMaelstromMissile.class, WOOD_ID, 30);
    public static final RegistryObject<EntityType<EntityGeyser>> GEYSER = registerEntity("geyser", EntityGeyser.class, GEYSER_ID, 30);
    public static final RegistryObject<EntityType<ProjectileBrownstoneCannon>> BROWNSTONE_CANNON = registerEntity("brownstone_cannon", ProjectileBrownstoneCannon.class, BROWNSTONE_CANNON_ID, 30);
    public static final RegistryObject<EntityType<EntityCliffPortalSpawn>> CLIFF_PORTAL_SPAWN = registerEntity("cliff_portal_spawn", EntityCliffPortalSpawn.class, CLIFF_PORTAL_SPAWN, 30);
    public static final RegistryObject<EntityType<ProjectileExplosiveDrill>> EXPLOSIVE_DRILL = registerEntity("explosive_drill", ProjectileExplosiveDrill.class, EXPLOSIVE_DRILL, 30);
    public static final RegistryObject<EntityType<ProjectilePiercingBullet>> AZURE_BULLET = registerFastProjectile("azure_bullet", ProjectilePiercingBullet.class, AZURE_BULLET, 100);
    public static final RegistryObject<EntityType<ProjectileSwampSpittle>> SWAMP_SPITTLE = registerEntity("swamp_spittle", ProjectileSwampSpittle.class, PROJECTILE_START_ID++, 30);
    public static final RegistryObject<EntityType<EntityNexusParticleSpawner>> NEXUS_PARTICLE = registerEntity("nexus_particle", EntityNexusParticleSpawner.class, PROJECTILE_START_ID++, 50);
    public static final RegistryObject<EntityType<ProjectileMaelstromWisp>> MAELSTROM_WISP = registerEntity("maelstrom_wisp", ProjectileMaelstromWisp.class, PROJECTILE_START_ID++, 50);
    public static final RegistryObject<EntityType<ProjectileMeteor>> METEOR = registerEntity("meteor", ProjectileMeteor.class, PROJECTILE_START_ID++, 100);
    public static final RegistryObject<EntityType<ProjectileMeteorSpawner>> METEOR_SPAWNER = registerEntity("meteor_spawner", ProjectileMeteorSpawner.class, PROJECTILE_START_ID++, 50);
    public static final RegistryObject<EntityType<ProjectileSwordSlash>> SWORD_SLASH = registerEntity("sword_slash", ProjectileSwordSlash.class, PROJECTILE_START_ID++, 50);
    public static final RegistryObject<EntityType<ProjectileBeastQuake>> BEAST_QUAKE = registerEntity("beast_quake", ProjectileBeastQuake.class, PROJECTILE_START_ID++, 50);
    public static final RegistryObject<EntityType<ProjectileBone>> BONE = registerEntity("bone", ProjectileBone.class, PROJECTILE_START_ID++, 30);
    public static final RegistryObject<EntityType<ProjectileBoneQuake>> BONE_QUAKE = registerEntity("bone_quake", ProjectileBoneQuake.class, PROJECTILE_START_ID++, 50);
    public static final RegistryObject<EntityType<ProjectileMonolithFireball>> MONOLITH_FIREBALL = registerEntity("monolith_fireball", ProjectileMonolithFireball.class, PROJECTILE_START_ID++, 50);
    public static final RegistryObject<EntityType<ProjectileMaelstromMeteor>> MAELSTROM_METEOR = registerEntity("maelstrom_meteor", ProjectileMaelstromMeteor.class, PROJECTILE_START_ID++, 50);
    public static final RegistryObject<EntityType<EntityLargeGoldenRune>> LARGE_GOLDEN_RUNE = registerEntity("large_golden_rune", EntityLargeGoldenRune.class, PROJECTILE_START_ID++, 40);
    public static final RegistryObject<EntityType<EntityCrimsonTowerSpawner>> CRIMSON_TOWER_SPAWNER = registerEntity("crimson_tower_spawner", EntityCrimsonTowerSpawner.class, PROJECTILE_START_ID++, 40);
    public static final RegistryObject<EntityType<EntityHealerOrb>> HEALER_ORB = registerEntity("healer_orb", EntityHealerOrb.class, PROJECTILE_START_ID++, 40);
    public static final RegistryObject<EntityType<ProjectileChaosFireball>> CHAOS_FIREBALL = registerEntity("chaos_fireball", ProjectileChaosFireball.class, PROJECTILE_START_ID++, 40);
    public static final RegistryObject<EntityType<ProjectileRuneWisp>> RUNE_WISP = registerEntity("rune_wisp", ProjectileRuneWisp.class, PROJECTILE_START_ID++, 40);
    public static final RegistryObject<EntityType<EntityCrimsonPortalSpawn>> CRIMSON_PORTAL_SPAWN = registerEntity("crimson_portal_spawn", EntityCrimsonPortalSpawn.class, PROJECTILE_START_ID++, 40);
    public static final RegistryObject<EntityType<EntityTuningForkLazer>> TUNING_FORK_LAZER_RENDERER = registerEntity("tuning_fork_lazer_renderer", EntityTuningForkLazer.class, PROJECTILE_START_ID++, 60);
    public static final RegistryObject<EntityType<ProjectileMegaFireball>> MEGA_FIREBALL = registerEntity("mega_fireball", ProjectileMegaFireball.class, PROJECTILE_START_ID++, 40);
    public static final RegistryObject<EntityType<ProjectileStatueMaelstromMissile>> STATUE_MAELSTROM_MISSILE = registerEntity("statue_maelstrom_missile", ProjectileStatueMaelstromMissile.class, PROJECTILE_START_ID++, 30);
    public static final RegistryObject<EntityType<ProjectileMaelstromRune>> MAELSTROM_RUNE = registerEntity("maelstrom_rune", ProjectileMaelstromRune.class, PROJECTILE_START_ID++, 40);
    public static final RegistryObject<EntityType<ProjectileBeastFireball>> BEAST_FIREBALL = registerEntity("beast_fireball", ProjectileBeastFireball.class, PROJECTILE_START_ID++, 40);
    public static final RegistryObject<EntityType<ProjectileHomingFlame>> HOMING_FLAME = registerEntity("homing_flame", ProjectileHomingFlame.class, PROJECTILE_START_ID++, 50);
    public static final RegistryObject<EntityType<ProjectileCrimsonWanderer>> CRIMSON_WANDERER = registerEntity("crimson_wanderer", ProjectileCrimsonWanderer.class, PROJECTILE_START_ID++, 60);
    public static final RegistryObject<EntityType<EntityCrimsonCrystal>> CRIMSON_CRYSTAL = registerEntity("crimson_crystal", EntityCrimsonCrystal.class, PROJECTILE_START_ID++, 60);

    public static final RegistryObject<EntityType<ParticleSpawnerExplosion>> EXPLOSIVE_PARTICLE = registerEntity("explosion_particle", ParticleSpawnerExplosion.class, PARTICLE_START_ID++, 20);
    public static final RegistryObject<EntityType<ParticleSpawnerSwordSwing>> BLACK_GOLD_SWORD_PARTICLE = registerEntity("black_gold_sword_particle", ParticleSpawnerSwordSwing.class, PARTICLE_START_ID++, 20);
    public static final RegistryObject<EntityType<ParticleSpawnerRainbow>> RAINBOW_PARTICLE = registerEntity("rainbow_particle", ParticleSpawnerRainbow.class, PARTICLE_START_ID++, 20);
    public static final RegistryObject<EntityType<EntityMaelstromTowerDestroyer>> MAELSTROM_TOWER_DESTROYER = registerEntity("maelstrom_tower_destroyer", EntityMaelstromTowerDestroyer.class, PARTICLE_START_ID++, 20);

    /*registerTileEntity(TileEntityMalestromSpawner.class, "spawner");
    registerTileEntity(TileEntityDisappearingSpawner.class, "maelstrom_spawner");
    registerTileEntity(TileEntityMegaStructure.class, "mega_structure");
    registerTileEntity(TileEntityTeleporter.class, "nexus_teleporter");
    registerTileEntity(TileEntityBossSpawner.class, "nexus_spawner");
    registerTileEntity(TileEntityUpdater.class, "updater");
    registerTileEntity(TileEntityFan.class, "fan");*/
    
	public static <T extends Entity> EntityType.Builder<T> createBuilder(EntityType.EntityFactory<T> factory, MobCategory category) {
		return EntityType.Builder.<T>of(factory, category);
	}

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityWithID(String name, EntityType.Builder<T> builder, int range, Vec3i eggColor) {
        //ModItems.ITEMS.register(name + "spawn_egg", () -> new ForgeSpawnEggItem(null, eggColor.getX(), eggColor.getY(), new Item.Properties().tab(ModCreativeTabs.ITEMS)));
        return ENTITY_TYPES.register(name, () -> builder.setTrackingRange(range).updateInterval(1).setShouldReceiveVelocityUpdates(true).build(new ResourceLocation(Reference.MOD_ID, name).toString()));
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder, int range, Vec3i eggColor) {
        return ENTITY_TYPES.register(name, () -> builder.setTrackingRange(range).updateInterval(1).setShouldReceiveVelocityUpdates(true).build(new ResourceLocation(Reference.MOD_ID, name).toString()));
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder, int range) {
        return ENTITY_TYPES.register(name, () -> builder.setTrackingRange(range).updateInterval(1).setShouldReceiveVelocityUpdates(true).build(new ResourceLocation(Reference.MOD_ID, name).toString()));
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerFastProjectile(String name, EntityType.Builder<T> builder, int range) {
        return ENTITY_TYPES.register(name, () -> builder.setTrackingRange(range).updateInterval(1).setShouldReceiveVelocityUpdates(false).build(new ResourceLocation(Reference.MOD_ID, name).toString()));
    }

    private static void registerTileEntity(Class<? extends BlockEntity> entity, String name) {
        GameRegistry.registerTileEntity(entity, new ResourceLocation(Reference.MOD_ID + ":" + name));
    }
}
