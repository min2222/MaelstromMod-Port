package com.barribob.mm.event_handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.barribob.mm.Main;
import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.entity.util.LeapingEntity;
import com.barribob.mm.gui.InGameGui;
import com.barribob.mm.init.ModDimensions;
import com.barribob.mm.init.ModPotions;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.ManaProvider;
import com.barribob.mm.packets.MessageMana;
import com.barribob.mm.packets.MessagePlayDarkNexusWindSound;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

@Mod.EventBusSubscriber()
public class EntityEventHandler {
    // Queues players to receive the wind sound packet
    private static final Set<ServerPlayer> DARK_NEXUS_PLAYERS = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Map<LivingEntity, Integer> FALLING_ENTITIES = new WeakHashMap<>();

    @SubscribeEvent
    public static void onLivingFallEvent(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof LeapingEntity && ((LeapingEntity) event.getEntityLiving()).isLeaping()) {
            event.setDistance(event.getDistance() - 3);
            ((LeapingEntity) event.getEntityLiving()).onStopLeaping();
            ((LeapingEntity) event.getEntityLiving()).setLeaping(false);
        }
    }

    // Play wind sound when traveling to the dark nexus dimension
    @SubscribeEvent
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer && event.getDimension() == ModDimensions.DARK_NEXUS.getId()) {
            DARK_NEXUS_PLAYERS.add((ServerPlayer) event.getEntity());
        }
    }

    // Play wind sound when logging in and in dark nexus dimension
    @SubscribeEvent
    public static void onWorldLoad(PlayerLoggedInEvent event) {
        if (event.player instanceof ServerPlayer) {
            if (event.player.dimension == ModDimensions.DARK_NEXUS.getId()) {
                DARK_NEXUS_PLAYERS.add((ServerPlayer) event.player);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityUpdateEvent(LivingUpdateEvent event) {

        if (event.getEntityLiving() instanceof LeapingEntity &&
                !event.getEntityLiving().level.isClientSide &&
                ((LeapingEntity) event.getEntityLiving()).isLeaping() &&
                event.getEntityLiving().onGround) {

            FALLING_ENTITIES.put(event.getEntityLiving(), FALLING_ENTITIES.getOrDefault(event.getEntityLiving(), 0) + 1);

            if(FALLING_ENTITIES.get(event.getEntityLiving()) >= 10) {
                ((LeapingEntity) event.getEntityLiving()).setLeaping(false);
                FALLING_ENTITIES.remove(event.getEntityLiving());
            }
        }

            // Play wind sound for players in dark nexus
        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = ((ServerPlayer) event.getEntity());
            if (DARK_NEXUS_PLAYERS.contains(player) && event.getEntity().dimension == ModDimensions.DARK_NEXUS.getId()) {
                Main.network.sendTo(new MessagePlayDarkNexusWindSound(), player);
                DARK_NEXUS_PLAYERS.remove(player);
            }
        }

        if (event.getEntityLiving() != null && event.getEntityLiving().isPotionActive(ModPotions.water_strider)) {
            ModUtils.walkOnWater(event.getEntityLiving(), event.getEntityLiving().world);
        }

        if (event.getEntity().dimension == ModConfig.world.dark_nexus_dimension_id && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.level.isClientSide) {
                ModUtils.performNTimes(15, (i) -> {
                    Vec3 pos = player.position().add(new Vec3(ModRandom.getFloat(8), ModRandom.getFloat(4), ModRandom.getFloat(4)));
                    ParticleManager.spawnColoredSmoke(player.world, pos, ModColors.DARK_GREY, new Vec3(0.8, 0, 0));
                });
            }

            int[] blockage = {0, 0}; // Represents the two y values the wind could be blowing at the player

            // Find any blocks that block the path of the wind
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 2; y++) {
                    BlockPos pos = new BlockPos(player.position()).add(new BlockPos(x - 4, y, 0));
                    BlockState block = player.world.getBlockState(pos);
                    if (block.isFullBlock() || block.isFullCube() || block.isBlockNormalCube()) {
                        blockage[y] = 1;
                    }
                }
            }

            // With 1 blockage, velocity is 0.01. With no blockage, velocity is 0.02, and
            // with all blockage, velocity is 0
            float windStrength = (2 - (blockage[0] + blockage[1])) * 0.5f * 0.02f;
            player.addVelocity(windStrength, 0, 0);

        }

        if (!event.getEntity().level.isClientSide && event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = ((ServerPlayer) event.getEntity());
            if (event.getEntity().tickCount % 35 == 0) {
                IMana currentMana = player.getCapability(ManaProvider.MANA, null);
                if (!currentMana.isLocked()) {
                    if (currentMana.isRecentlyConsumed()) {
                        currentMana.setRecentlyConsumed(false);
                    } else {
                        currentMana.replenish(1f);
                        Main.network.sendTo(new MessageMana(currentMana.getMana()), player);
                    }
                }
            }
        } else if (event.getEntity().level.isClientSide && event.getEntity() instanceof Player) {
            InGameGui.updateCounter();
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.Clone event) {
        // Persist whether mana is locked across player respawning
        IMana newMana = event.getEntityPlayer().getCapability(ManaProvider.MANA, null);
        IMana oldMana = event.getOriginal().getCapability(ManaProvider.MANA, null);
        newMana.setLocked(oldMana.isLocked());
    }

    @SubscribeEvent
    public static void onEntitySpawnEvent(LivingSpawnEvent event) {
        if (event.getEntityLiving() instanceof EntitySheep) {
            if (event.getEntityLiving().dimension == ModConfig.world.fracture_dimension_id) {
                ((EntitySheep) event.getEntityLiving()).setFleeceColor(DyeColor.CYAN);
            }
            if (event.getEntityLiving().dimension == ModConfig.world.cliff_dimension_id) {
                ((EntitySheep) event.getEntityLiving()).setFleeceColor(DyeColor.GRAY);
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(LivingAttackEvent event) {
        boolean isMaelstromFriend = !EntityMaelstromMob.CAN_TARGET.apply(event.getEntityLiving());
        boolean sourceIsMaelstromFriend = !EntityMaelstromMob.CAN_TARGET.apply(event.getSource().getTrueSource());
        if(isMaelstromFriend && sourceIsMaelstromFriend) {
            event.setCanceled(true);
        }
    }
}
