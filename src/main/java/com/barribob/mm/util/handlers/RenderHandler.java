package com.barribob.mm.util.handlers;

import java.util.function.Function;

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
import com.barribob.mm.entity.model.ModelArmorer;
import com.barribob.mm.entity.model.ModelBeast;
import com.barribob.mm.entity.model.ModelBladesmith;
import com.barribob.mm.entity.model.ModelCliffFly;
import com.barribob.mm.entity.model.ModelDreamElk;
import com.barribob.mm.entity.model.ModelFireball;
import com.barribob.mm.entity.model.ModelFloatingSkull;
import com.barribob.mm.entity.model.ModelGoldenPillar;
import com.barribob.mm.entity.model.ModelGunTrader;
import com.barribob.mm.entity.model.ModelHorror;
import com.barribob.mm.entity.model.ModelIronShade;
import com.barribob.mm.entity.model.ModelMaelstromFury;
import com.barribob.mm.entity.model.ModelMaelstromGauntlet;
import com.barribob.mm.entity.model.ModelMaelstromHealer;
import com.barribob.mm.entity.model.ModelMaelstromLancer;
import com.barribob.mm.entity.model.ModelMaelstromMage;
import com.barribob.mm.entity.model.ModelMaelstromWarrior;
import com.barribob.mm.entity.model.ModelMaelstromWitch;
import com.barribob.mm.entity.model.ModelMageTrader;
import com.barribob.mm.entity.model.ModelNexusSaiyan;
import com.barribob.mm.entity.model.ModelStatueOfNirvana;
import com.barribob.mm.entity.model.ModelSwampCrawler;
import com.barribob.mm.entity.projectile.EntityHealerOrb;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileBeastFireball;
import com.barribob.mm.entity.projectile.ProjectileBlackFireball;
import com.barribob.mm.entity.projectile.ProjectileBone;
import com.barribob.mm.entity.projectile.ProjectileBullet;
import com.barribob.mm.entity.projectile.ProjectileChaosFireball;
import com.barribob.mm.entity.projectile.ProjectileFireball;
import com.barribob.mm.entity.projectile.ProjectileGoldenFireball;
import com.barribob.mm.entity.projectile.ProjectileGoldenMissile;
import com.barribob.mm.entity.projectile.ProjectileHomingFlame;
import com.barribob.mm.entity.projectile.ProjectileHorrorAttack;
import com.barribob.mm.entity.projectile.ProjectileMegaFireball;
import com.barribob.mm.entity.projectile.ProjectileMonolithFireball;
import com.barribob.mm.entity.projectile.ProjectileStatueMaelstromMissile;
import com.barribob.mm.entity.projectile.ProjectileSwampSpittle;
import com.barribob.mm.entity.render.ModelHomingFlame;
import com.barribob.mm.entity.render.RenderAzureGolem;
import com.barribob.mm.entity.render.RenderAzureVillager;
import com.barribob.mm.entity.render.RenderChaosKnight;
import com.barribob.mm.entity.render.RenderCrimsonCrystal;
import com.barribob.mm.entity.render.RenderHerobrine;
import com.barribob.mm.entity.render.RenderMaelstromBeast;
import com.barribob.mm.entity.render.RenderMaelstromGauntlet;
import com.barribob.mm.entity.render.RenderMaelstromIllager;
import com.barribob.mm.entity.render.RenderModEntity;
import com.barribob.mm.entity.render.RenderMonolith;
import com.barribob.mm.entity.render.RenderNonLivingEntity;
import com.barribob.mm.entity.render.RenderStatueOfNirvana;
import com.barribob.mm.entity.render.RenderTuningForkLazer;
import com.barribob.mm.entity.render.RenderWhiteMonolith;
import com.barribob.mm.entity.util.EntityCrimsonTowerSpawner;
import com.barribob.mm.entity.util.EntityMaelstromTowerDestroyer;
import com.barribob.mm.entity.util.EntityNexusParticleSpawner;
import com.barribob.mm.entity.util.EntityParticleSpawner;
import com.barribob.mm.entity.util.EntityPortalSpawn;
import com.barribob.mm.entity.util.EntityTuningForkLazer;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.Reference;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.Items;

public class RenderHandler {
    public static void registerEntityRenderers() {
        registerModEntityRenderer(EntityShade.class, new ModelMaelstromWarrior(), "shade_base.png", "shade_azure.png", "shade_golden.png", "shade_crimson.png");
        registerModEntityRenderer(EntityHorror.class, new ModelHorror(), "horror.png");
        registerModEntityRenderer(EntityDreamElk.class, new ModelDreamElk(), "dream_elk.png");
        registerModEntityRenderer(EntityBeast.class, new ModelBeast(), "beast.png", "beast.png", "beast.png", "beast_crimson.png");
        registerModEntityRenderer(EntityMaelstromMage.class, new ModelMaelstromMage(), "maelstrom_mage.png", "maelstrom_mage_azure.png", "maelstrom_mage_golden.png", "maelstrom_mage_crimson.png");
        registerModEntityRenderer(EntityFloatingSkull.class, new ModelFloatingSkull(), "floating_skull.png");
        registerModEntityRenderer(Herobrine.class, (manager) -> new RenderHerobrine(manager, new ResourceLocation(Reference.MOD_ID + ":textures/entity/herobrine_1.png")));
        registerModEntityRenderer(EntityHerobrineOne.class, (manager) -> new RenderHerobrine(manager, new ResourceLocation(Reference.MOD_ID + ":textures/entity/shadow_clone.png")));
        registerModEntityRenderer(NexusGunTrader.class, new ModelGunTrader(), "gun_trader.png");
        registerModEntityRenderer(NexusMageTrader.class, new ModelMageTrader(), "mage_trader.png");
        registerModEntityRenderer(NexusArmorer.class, new ModelArmorer(), "armorer.png");
        registerModEntityRenderer(NexusBladesmith.class, new ModelBladesmith(), "bladesmith.png");
        registerModEntityRenderer(NexusSpecialTrader.class, new ModelNexusSaiyan(), "nexus_saiyan.png");
        registerModEntityRenderer(EntityGoldenPillar.class, new ModelGoldenPillar(), "golden_pillar.png");
        registerModEntityRenderer(EntityGoldenBoss.class, RenderStatueOfNirvana::new);
        registerModEntityRenderer(EntityMaelstromWitch.class, new ModelMaelstromWitch(), "maelstrom_witch.png");
        registerModEntityRenderer(EntitySwampCrawler.class, new ModelSwampCrawler(), "swamp_crawler.png");
        registerModEntityRenderer(EntityIronShade.class, new ModelIronShade(), "iron_shade.png", null, null, "iron_shade_crimson.png");
        registerModEntityRenderer(EntityCliffFly.class, new ModelCliffFly(), "cliff_fly.png");
        registerModEntityRenderer(EntityAzureVillager.class, RenderAzureVillager::new);
        registerModEntityRenderer(EntityMaelstromIllager.class, RenderMaelstromIllager::new);
        registerModEntityRenderer(EntityAzureGolem.class, (manager) -> new RenderAzureGolem(manager, "azure_golem.png"));
        registerModEntityRenderer(EntityCliffGolem.class, (manager) -> new RenderAzureGolem(manager, "cliff_golem.png"));
        registerModEntityRenderer(EntityMaelstromBeast.class, RenderMaelstromBeast::new);
        registerModEntityRenderer(EntityMonolith.class, RenderMonolith::new);
        registerModEntityRenderer(EntityWhiteMonolith.class, RenderWhiteMonolith::new);
        registerModEntityRenderer(EntityMaelstromLancer.class, new ModelMaelstromLancer(), "maelstrom_lancer.png", "maelstrom_lancer_azure.png", "maelstrom_lancer_golden.png", "maelstrom_lancer_crimson.png");
        registerModEntityRenderer(EntityChaosKnight.class, (manager) -> new RenderChaosKnight(manager, "chaos_knight.png"));
        registerModEntityRenderer(EntityMaelstromHealer.class, new ModelMaelstromHealer(), "maelstrom_healer.png");
        registerModEntityRenderer(EntityMaelstromGauntlet.class, (manager) -> new RenderMaelstromGauntlet(manager, "maelstrom_gauntlet.png"));
        registerModEntityRenderer(EntityTuningForkLazer.class, RenderTuningForkLazer::new);
        registerModEntityRenderer(ProjectileMegaFireball.class, (manager) -> new RenderNonLivingEntity<>(manager, "fireball.png", new ModelFireball(), -1.501F));
        registerModEntityRenderer(EntityMaelstromStatueOfNirvana.class, new ModelStatueOfNirvana(), "maelstrom_statue.png");
        registerModEntityRenderer(ProjectileHomingFlame.class, (manager) -> new RenderNonLivingEntity<>(manager, "homing_fireball.png", new ModelHomingFlame(), -0.2F));
        registerModEntityRenderer(EntityAlternativeMaelstromGauntletStage2.class, new ModelMaelstromGauntlet(), "maelstrom_gauntlet_stage_2.png");
        registerModEntityRenderer(EntityMaelstromFury.class, new ModelMaelstromFury(), "maelstrom_fury.png");
        registerModEntityRenderer(EntityAlternativeMaelstromGauntletStage1.class, new ModelMaelstromGauntlet(), "maelstrom_gauntlet.png");
        registerModEntityRenderer(EntityCrimsonCrystal.class, (manager) -> new RenderCrimsonCrystal(manager, "crystal.png", -0.5f));

        registerProjectileRenderer(ModProjectile.class);
        registerProjectileRenderer(ProjectileBullet.class);
        registerProjectileRenderer(EntityPortalSpawn.class);
        registerProjectileRenderer(EntityNexusParticleSpawner.class);
        registerProjectileRenderer(ProjectileSwampSpittle.class, ModItems.SWAMP_SLIME);
        registerProjectileRenderer(EntityParticleSpawner.class);
        registerProjectileRenderer(ProjectileBone.class, Items.BONE);
        registerProjectileRenderer(ProjectileHorrorAttack.class, ModItems.MAELSTROM_PELLET);
        registerProjectileRenderer(ProjectileFireball.class, Items.FIRE_CHARGE);
        registerProjectileRenderer(ProjectileBlackFireball.class, Items.FIRE_CHARGE);
        registerProjectileRenderer(ProjectileGoldenFireball.class, Items.FIRE_CHARGE);
        registerProjectileRenderer(ProjectileMonolithFireball.class, Items.FIRE_CHARGE);
        registerProjectileRenderer(ProjectileGoldenMissile.class, ModItems.GOLD_PELLET);
        registerProjectileRenderer(EntityCrimsonTowerSpawner.class);
        registerProjectileRenderer(EntityHealerOrb.class);
        registerProjectileRenderer(ProjectileChaosFireball.class, ModItems.CRIMSON_PELLET);
        registerProjectileRenderer(ProjectileStatueMaelstromMissile.class, ModItems.MAELSTROM_PELLET);
        registerProjectileRenderer(ProjectileBeastFireball.class, Items.FIRE_CHARGE);
        registerProjectileRenderer(EntityMaelstromTowerDestroyer.class);
    }

    /**
     * Registers an entity with a model and sets it up for rendering
     */
    private static <T extends Mob, M extends EntityModel<T>, V extends RenderModEntity<T, M>> void registerModEntityRenderer(EntityType<T> type, Function<EntityRendererProvider.Context, V> function) {
    	EntityRenderers.register(type, pContext -> function.apply(pContext));
    }
    
    private static <T extends Entity> void registerNonLivingEntityRenderer(EntityType<T> type, Function<EntityRendererProvider.Context, EntityRenderer<? super T>> function) {
    	EntityRenderers.register(type, pContext -> function.apply(pContext));
    }

    /**
     * Makes a projectile render with the given item
     *
     * @param projectileClass
     */
    private static <T extends Entity & ItemSupplier> void registerProjectileRenderer(EntityType<T> type) {
    	EntityRenderers.register(type, pContext -> new ThrownItemRenderer<>(pContext));
    }
}
