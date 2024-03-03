package com.barribob.mm.event_handlers;

import com.barribob.mm.Main;
import com.barribob.mm.entity.particleSpawners.ParticleSpawnerRainbow;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.init.ModPotions;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.ManaProvider;
import com.barribob.mm.packets.MessageMana;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.typesafe.config.Config;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber()
public class ItemEventHandler {

    // Test function to make sure the lookVec and pitch utilities are correct
//    public static void drawLookVec(EntityPlayer player) {
//        Vec3d vec = ModUtils.getLookVec((float) ModUtils.toPitch(player.getLookVec()), player.rotationYaw).scale(6);
//        ModUtils.lineCallback(player.position(), vec.add(player.position()), 20, (v, i) -> ParticleManager.spawnEffect(player.world, v, ModColors.BLUE));
//    }

    @SubscribeEvent
    public static void aiStepEvent(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Item heldItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
            Item offhandItem = player.getItemInHand(InteractionHand.OFF_HAND).getItem();
	
            Item helmet = player.getInventory().armor.get(3).getItem();
            Item chestplate = player.getInventory().armor.get(2).getItem();
            Item leggings = player.getInventory().armor.get(1).getItem();
            Item boots = player.getInventory().armor.get(0).getItem();
            
            Config bonusConfig = Main.itemsConfig.getConfig("full_set_bonuses");

            if (((heldItem.equals(ModItems.BAKUYA) && offhandItem.equals(ModItems.KANSHOU)) || (heldItem.equals(ModItems.KANSHOU) && offhandItem.equals(ModItems.BAKUYA))) && bonusConfig.getBoolean("kanshou_bakuya")) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 1));
            }

            if (!player.level.isClientSide && player instanceof ServerPlayer && (heldItem.equals(ModItems.CROSS_OF_AQUA) || offhandItem.equals(ModItems.CROSS_OF_AQUA))) {
                IMana mana = player.getCapability(ManaProvider.MANA).orElse(null);
                if (!mana.isLocked() && mana.getMana() > 0) {
                    if (!player.getAbilities().instabuild && player.tickCount % 40 == 0) {
                        mana.consume(1);
                        Main.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new MessageMana(mana.getMana()));
                    }
                    player.addEffect(new MobEffectInstance(ModPotions.WATER_STRIDER.get(), 40));
                }
            }

            if (bonusConfig.getBoolean("nyan") &&
                    helmet.equals(ModItems.NYAN_HELMET) &&
                    chestplate.equals(ModItems.NYAN_CHESTPLATE) &&
                    leggings.equals(ModItems.NYAN_LEGGINGS) &&
                    boots.equals(ModItems.NYAN_BOOTS) && player.isSprinting()) {
                Entity particle = new ParticleSpawnerRainbow(player.level);
                Vec3 pos = player.position().subtract(new Vec3(player.getLookAngle().x, 0, player.getLookAngle().z));
                particle.setPos(pos.x, pos.y, pos.z);
                player.level.addFreshEntity(particle);
            }

            if (bonusConfig.getBoolean("goltox") &&
                    helmet.equals(ModItems.GOLTOX_HELMET) &&
                    chestplate.equals(ModItems.GOLTOX_CHESTPLATE) &&
                    leggings.equals(ModItems.GOLTOX_LEGGINGS) &&
                    boots.equals(ModItems.GOLTOX_BOOTS) &&
                    player.tickCount % 20 == 0) {
                if (player.hasEffect(MobEffects.POISON)) {
                	MobEffectInstance effect = player.getEffect(MobEffects.POISON);
                    player.removeEffect(MobEffects.POISON);
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, effect.getDuration() - 20, effect.getAmplifier()));
                }
            }

            if (!player.level.isClientSide && player.tickCount % 40 == 0) {
                if (bonusConfig.getBoolean("black_gold") &&
                        helmet.equals(ModItems.BLACK_GOLD_HELMET) &&
                        chestplate.equals(ModItems.BLACK_GOLD_CHESTPLATE) &&
                        leggings.equals(ModItems.BLACK_GOLD_LEGGINGS) &&
                        boots.equals(ModItems.BLACK_GOLD_BOOTS)) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0));
                }

                if (bonusConfig.getBoolean("maelstrom") &&
                        helmet.equals(ModItems.MAELSTROM_HELMET) &&
                        chestplate.equals(ModItems.MAELSTROM_CHESTPLATE) &&
                        leggings.equals(ModItems.MAELSTROM_LEGGINGS) &&
                        boots.equals(ModItems.MAELSTROM_BOOTS)) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 0));
                }

                if (bonusConfig.getBoolean("swamp") &&
                        helmet.equals(ModItems.SWAMP_HELMET) &&
                        chestplate.equals(ModItems.SWAMP_CHESTPLATE) &&
                        leggings.equals(ModItems.SWAMP_LEGGINGS) &&
                        boots.equals(ModItems.SWAMP_BOOTS)) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0));
                }

                if (bonusConfig.getBoolean("energetic_steel") &&
                        helmet.equals(ModItems.ENERGETIC_STEEL_HELMET) &&
                        chestplate.equals(ModItems.ENERGETIC_STEEL_CHESTPLATE) &&
                        leggings.equals(ModItems.ENERGETIC_STEEL_LEGGINGS) &&
                        boots.equals(ModItems.ENERGETIC_STEEL_BOOTS)) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 1));
                }

                if (bonusConfig.getBoolean("fadesteel") &&
                        helmet.equals(ModItems.FADESTEEL_HELMET) &&
                        chestplate.equals(ModItems.FADESTEEL_CHESTPLATE) &&
                        leggings.equals(ModItems.FADESTEEL_LEGGINGS) &&
                        boots.equals(ModItems.FADESTEEL_BOOTS)) {
                    player.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.357);
                }
                // Using 0.357 as a value unlikely to be chosen by modders, so I can expect to enable and disable this without conflicting
                else if (player.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue() == 0.357) {
                    player.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0);
                }

                if (bonusConfig.getBoolean("elysium") &&
                        helmet.equals(ModItems.ELYSIUM_HELMET) &&
                        chestplate.equals(ModItems.ELYSIUM_CHESTPLATE) &&
                        leggings.equals(ModItems.ELYSIUM_LEGGINGS) &&
                        boots.equals(ModItems.ELYSIUM_BOOTS)) {
                    // Every 2 seconds * 60 = approximately every 2 minutes
                    if (player.getRandom().nextInt(60) == 0) {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 140, 3));
                    }
                }
            }

            if (player.level.isClientSide && chestplate.equals(ModItems.ELYSIUM_WINGS) && player.isFallFlying()) {
                double speed = Math.max(0.1, Math.min(1, new Vec3(player.getDeltaMovement().x, 0, player.getDeltaMovement().z).length() + player.getDeltaMovement().y));
                ParticleManager.spawnFirework(player.level, player.getEyePosition(1).add(ModUtils.getRelativeOffset(player, new Vec3(0, 0, -speed))), ModColors.RED);
                ParticleManager.spawnFirework(player.level, player.getEyePosition(1).add(ModUtils.getRelativeOffset(player, new Vec3(0, 0, speed))), ModColors.RED);
            }
        }
    }
}
