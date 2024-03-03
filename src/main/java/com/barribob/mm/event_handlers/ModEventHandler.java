package com.barribob.mm.event_handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.barribob.mm.Main;
import com.barribob.mm.config.ModConfig;
import com.barribob.mm.gui.InGameGui;
import com.barribob.mm.init.ModDimensions;
import com.barribob.mm.invasion.InvasionUtils;
import com.barribob.mm.invasion.InvasionWorldSaveData;
import com.barribob.mm.items.IExtendedReach;
import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.items.tools.ToolSword;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.ManaProvider;
import com.barribob.mm.packets.MessageEmptySwing;
import com.barribob.mm.packets.MessageExtendedReachAttack;
import com.barribob.mm.packets.MessageSyncConfig;
import com.barribob.mm.player.PlayerMeleeAttack;
import com.barribob.mm.renderer.InputOverrides;
import com.barribob.mm.util.EntityElementalDamageSourceIndirect;
import com.barribob.mm.util.GenUtils;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.Reference;
import com.barribob.mm.util.handlers.ArmorHandler;
import com.barribob.mm.util.teleporter.NexusToOverworldTeleporter;
import com.barribob.mm.world.dimension.crimson_kingdom.WorldGenCrimsonKingdomChunk;
import com.barribob.mm.world.dimension.nexus.DimensionNexus;
import com.barribob.mm.world.gen.WorldGenCustomStructures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.util.LevelType;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/**
 * Holds various important functionalities only accessible through the forge event system
 */
@Mod.EventBusSubscriber()
public class ModEventHandler {
    public static final ResourceLocation MANA = new ResourceLocation(Reference.MOD_ID, "mana");
    private static long timeSinceServerTick = System.nanoTime();
    public static boolean isInvasionEnabledViaGamestage = true;

    @SubscribeEvent
    public static void afterShieldAndBeforeArmor(LivingHurtEvent event) {
        if(event.getSource() instanceof EntityElementalDamageSourceIndirect) {
            EntityElementalDamageSourceIndirect damageSource = ((EntityElementalDamageSourceIndirect)event.getSource());
            if(damageSource.getStoppedByArmor()) {
                damageSource.bypassArmor = false;
            }

            if(damageSource.getDisablesShields() && event.getEntity() != null && ModUtils.canBlockDamageSource(damageSource, event.getEntity()) && event.getEntity() instanceof Player) {
                ((Player)event.getEntity()).disableShield(true);
            }
        }

        float damage = event.getAmount();
        // Factor in elemental armor first
        if (event.getSource() instanceof IElement) {
            damage *= 1 - ArmorHandler.getElementalArmor(event.getEntity(), ((IElement) event.getSource()).getElement());
        }

        // Factor in maelstrom armor second
        if (!event.getSource().isBypassArmor()) {
            damage *= 1 - ArmorHandler.getMaelstromArmor(event.getEntity());
        }

        event.setAmount(damage);
    }

    /**
     * The purpose of this hook is to detect chunk errors in the crimson kingdom and fill in those chunks automatically, or at the very least on world reload. It is very hard to figure out why these chunk
     * error happen (although my guess is that the structure generates when its +x +z or +xz chunks are somehow not generated.) In any case, this is sort of a patch after the fact hack to fix chunk
     * errors.
     */
    @SubscribeEvent
    public static void chunkWatched(ChunkWatchEvent.Watch event) {
        if (event.getPlayer().level.dimension() == ModDimensions.CRIMSON_KINGDOM_KEY) {
            Chunk chunk = event.getChunkInstance();
            if (chunk.isPopulated() && chunk.isLoaded() && event.getPlayer().world == chunk.getWorld()) {
                BlockPos chunkPos = new BlockPos(chunk.x * 16, 0, chunk.z * 16);

                // Detect if this chunk is empty
                // Try to generate the four generated structures that comprise the chunk
                for (int x = -1; x <= 0; x++) {
                    for (int z = -1; z <= 0; z++) {
                        int chunkX = chunk.x + x;
                        int chunkZ = chunk.z + z;

                        // Position to detect if a chunk is there or not (if the block is an air block)
                        // Take a look at the blocks place in the crimson empty spaces with chunk borders on to see how it works exactly
                        BlockPos detectionPos = new BlockPos(x * 8 + 12, 0, z * 8 + 12).offset(chunkPos);

                        // Position to generate the structure at if the chunk is indeed missing
                        BlockPos generationPos = new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);

                        if (chunk.getWorld().isAirBlock(detectionPos)) {
                            int chunkModX = Math.floorMod(chunkX, DimensionNexus.NexusStructureSpacing);
                            int chunkModZ = Math.floorMod(chunkZ, DimensionNexus.NexusStructureSpacing);
                            new WorldGenCrimsonKingdomChunk(chunkModX, chunkModZ).generate(chunk.getWorld(), chunk.getWorld().rand, generationPos);

                            // A hacky way of writing down that we've generated this chunk and it doesn't need to be looked at again
                            chunk.getWorld().setBlockState(detectionPos, Blocks.BEDROCK.defaultBlockState());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerLoggedInEvent event) {
        // Sync some of the config parameters
        if (ModConfig.server.sync_on_login) {
            Main.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new MessageSyncConfig(ModConfig.balance.progression_scale, ModConfig.balance.weapon_damage, ModConfig.balance.armor_toughness, ModConfig.balance.elemental_factor));
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.LevelTickEvent event) {
        boolean correctTickPhase = event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END;
        boolean isSuperflat = event.level.getWorldType().equals(LevelType.FLAT);
        boolean isInOverworld = event.level.dimension() == Level.OVERWORLD;
        if (!correctTickPhase || isSuperflat || !isInOverworld || !isInvasionEnabledViaGamestage) {
            return;
        }

        if(InvasionUtils.hasMultipleInvasionsConfigured()) {
            InvasionUtils.getInvasionData(event.level).tick(event.level);
            return;
        }

        InvasionWorldSaveData invasionCounter = ModUtils.getInvasionData(event.level);

        int previousTime = invasionCounter.getInvasionTime();
        long timeElapsed = System.nanoTime() - timeSinceServerTick;
        timeSinceServerTick = System.nanoTime();
        invasionCounter.update((int) (timeElapsed * 1e-6)); // Convert from nanoseconds to milleseconds

        // Issue a warning one tenth of the time left
        float warningMessageTime = ModConfig.world.warningInvasionTime * 60 * 1000;
        if (invasionCounter.getInvasionTime() > 0 && previousTime >= warningMessageTime && invasionCounter.getInvasionTime() < warningMessageTime && !invasionCounter.isInvaded()) {
            event.level.players().forEach((p) -> {
                p.sendSystemMessage(
                		Component.literal("" + ChatFormatting.DARK_PURPLE + Component.translatable(Reference.MOD_ID + ".invasion_1").getString()));
            });
        }

        if (invasionCounter.shouldDoInvasion()) {
            if (event.level.players().size() > 0) {
                // Get the player closest to the origin
                Player player = event.level.players().stream().reduce(event.level.players().get(0),
                        (p1, p2) -> p1.distanceToSqr(0, 0, 0) < p2.distanceToSqr(0, 0, 0) ? p1 : p2);

                List<BlockPos> positions = new ArrayList<BlockPos>();
                List<Integer> variations = new ArrayList<Integer>();

                // Find the flattest area
                ModUtils.circleCallback(50, 16, (pos) -> {
                    BlockPos structureSize = WorldGenCustomStructures.invasionTower.getSize(event.level);
                    BlockPos structurePos = new BlockPos(player.position().x, 0, player.position().z); // Start with player xz position
                    BlockPos mainTowerSize = new BlockPos(structureSize.getX() * 0.5f, 0, structureSize.getZ() * 0.5f);

                    structurePos = structurePos.offset(new BlockPos(pos.x, 0, pos.y)); // Add the circle position
                    structurePos = structurePos.subtract(new BlockPos(mainTowerSize)); // Center the structure

                    // The tower template edges are not very good indicators for what the height
                    // should be.
                    // This adjusts so that the height is based more on the center of the tower
                    int y = ModUtils.getAverageGroundHeight(event.level, structurePos.getX() + (int) (mainTowerSize.getX() * 0.5f),
                            structurePos.getZ() + (int) (mainTowerSize.getZ() * 0.5f), mainTowerSize.getX(), mainTowerSize.getZ(), 8);

                    // There is too much terrain variation for the tower to be here
                    if (y == -1 || y > NexusToOverworldTeleporter.yPortalOffset - structureSize.getY()) {
                        return;
                    }

                    // Add the y height
                    final BlockPos finalPos = structurePos.offset(new BlockPos(0, y, 0));

                    // Avoid spawning in water (mostly for oceans because they can be very deep)
                    if (event.level.containsAnyLiquid(new AABB(finalPos, structureSize.offset(finalPos)))) {
                        return;
                    }

                    // Try to avoid bases with beds (spawnpoints) in them
                    boolean baseNearby = event.level.players().stream().anyMatch((p) -> {
                        if (event.level.getSpawnPoint().equals(p.getBedLocation()) || p.getBedLocation() == null) {
                            return false;
                        }
                        return finalPos.distanceSq(p.getBedLocation()) < Math.pow(75, 2);
                    });

                    if (!baseNearby) {
                        int terrainVariation = GenUtils.getTerrainVariation(event.level, finalPos.getX(), finalPos.getZ(), finalPos.getX(),
                                structureSize.getZ());
                        positions.add(finalPos);
                        variations.add(terrainVariation);
                    }
                });

                if (positions.size() > 0) {
                    event.level.players().forEach((p) -> {
                        p.sendSystemMessage(Component.literal(
                                "" + ChatFormatting.DARK_PURPLE + Component.translatable(Reference.MOD_ID + ".invasion_2").getString()));
                    });
                    invasionCounter.setInvaded(true);
                    BlockPos structurePos = positions.get(variations.indexOf(Collections.min(variations)));
                    WorldGenCustomStructures.invasionTower.generateStructure(event.level, structurePos, Rotation.NONE);
                } else {
                    // If we don't find any good place to put the tower, put a cooldown because
                    // chances are there may be a lot of bad areas, so don't spend too much
                    // computing power
                    // every tick. Instead wait 10 seconds and try again
                    invasionCounter.setInvasionTime(10 * 1000);
                }
            } else {
                invasionCounter.setDimensionCooldownTime();
            }
        }
    }

    @SubscribeEvent
    public static void attachCabability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(MANA, new ManaProvider());
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public static void onAttackEntityEvent(AttackEntityEvent event) {
        // Overrides the melee attack of the player if the item used is the sweep attack
        // override interface
        if (event.getEntity().getMainHandItem().getItem() instanceof ISweepAttackOverride) {
            PlayerMeleeAttack.attackTargetEntityWithCurrentItem(event.getEntity(), event.getTarget());
            event.setCanceled(true);
        } else {
            event.setCanceled(false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(receiveCanceled = true)
    public static void onMouseEvent(MouseEvent event) {
        // If left clicking
        if (event.getButton() == 0 && event.isButtonstate()) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            if (player != null) {
                ItemStack itemStack = player.getMainHandItem();

                // If the item has extended reach, apply that, and send the attack
                // to the server to verify
                if (itemStack != null && itemStack.getItem() instanceof IExtendedReach) {
                    float reach = ((IExtendedReach) itemStack.getItem()).getReach();
                    HitResult result = InputOverrides.getMouseOver(1.0f, mc, reach);

                    if (result != null) {
                        if (result.getType() == HitResult.Type.ENTITY) {
                            Main.NETWORK.sendToServer(new MessageExtendedReachAttack(((EntityHitResult) result).getEntity().getId()));
                            mc.player.resetAttackStrengthTicker();
                        } else if (result.getType() == HitResult.Type.MISS) {
                            mc.player.resetAttackStrengthTicker();
                            net.minecraftforge.common.ForgeHooks.onEmptyLeftClick(mc.player);
                            event.setCanceled(true); // Prevents shorter reach swords from hitting with the event going through
                        }
                        // We let the block ray trace result be handled by the default event
                        mc.player.swing(InteractionHand.MAIN_HAND);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent()
    public static void onEmptyLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        handleEmptyLeftClick(event);

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent()
    public static void onEmptyLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        handleEmptyLeftClick(event);
    }

    /**
     * If the weapon is charged and the player empty left clicks, sends a message to the server to do a sweep attack
     *
     * @param event
     */
    private static void handleEmptyLeftClick(PlayerInteractEvent event) {
        if (event.getItemStack().getItem() instanceof ToolSword) {
            Main.NETWORK.sendToServer(new MessageEmptySwing());
        }
    }

    /**
     * Renders the maelstrom armor bar and the gun reload bar
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onGuiPostRender(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getCameraEntity() instanceof Player) {
            RenderSystem.enableBlend();
            Player player = (Player) mc.getCameraEntity();

            // If in creative mode or something, don't draw
            if (ModConfig.gui.showArmorBar) {
                InGameGui.renderArmorBar(mc, event, player);
            }

            if (ModConfig.gui.showGunCooldownBar) {
                InGameGui.renderGunReload(event, player);
            }

            IMana mana = player.getCapability(ManaProvider.MANA).orElse(null);

            if (!mana.isLocked() && ModConfig.gui.showManaBar) {
                InGameGui.renderManaBar(mc, event, player);
            }
        }
    }
}
