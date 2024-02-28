package com.barribob.mm.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.barribob.mm.Main;
import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.entities.EntityMaelstromHealer;
import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.entity.particleSpawners.ParticleSpawnerSwordSwing;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.tileentity.MobSpawnerLogic.MobSpawnData;
import com.barribob.mm.entity.util.IPitch;
import com.barribob.mm.init.ModEnchantments;
import com.barribob.mm.invasion.InvasionWorldSaveData;
import com.barribob.mm.packets.MessageModParticles;
import com.barribob.mm.particle.EnumModParticles;
import com.barribob.mm.util.handlers.LevelHandler;
import com.google.common.collect.Sets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public final class ModUtils {
    public static char AZURE_SYMBOL = '\u03A6';
    public static char GOLDEN_SYMBOL = '\u03A9';
    public static char CRIMSON_SYMBOL = '\u03A3';

    public static byte PARTICLE_BYTE = 12;
    public static byte SECOND_PARTICLE_BYTE = 14;
    public static byte THIRD_PARTICLE_BYTE = 15;
    public static byte FOURTH_PARTICLE_BYTE = 16;

    public static Vec3 X_AXIS = new Vec3(1, 0, 0);
    public static Vec3 Y_AXIS = new Vec3(0, 1, 0);
    public static Vec3 Z_AXIS = new Vec3(0, 0, 1);

    /**
     * This is only for the maelstrom mob death particles so it doesn't intersect with the other particle bytes.
     */
    public static byte MAELSTROM_PARTICLE_BYTE = 17;
    public static final String LANG_DESC = Reference.MOD_ID + ".desc.";
    public static final String LANG_CHAT = Reference.MOD_ID + ".dialog.";
    public static final DecimalFormat DF_0 = new DecimalFormat("0.0");
    public static final DecimalFormat ROUND = new DecimalFormat("0");
    public static final ResourceLocation PARTICLE = new ResourceLocation(Reference.MOD_ID + ":textures/particle/particles.png");

    static {
        DF_0.setRoundingMode(RoundingMode.HALF_EVEN);
        ROUND.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    public static Consumer<String> getPlayerAreaMessager(Entity entity) {
        return (message) -> {
            if (message != "") {
                for (Player player : entity.level.getNearbyPlayer(Player.class, (p) -> p.distanceToSqr(entity) < 100)) {
                    player.sendSystemMessage(Component.literal(ChatFormatting.DARK_PURPLE + entity.getDisplayName().getString() + ": " + ChatFormatting.WHITE)
                            .append(Component.translatable(ModUtils.LANG_CHAT + message)));
                }
            }
        };
    }

    public static String translateDesc(String key, Object... params) {
        return Component.translatable(ModUtils.LANG_DESC + key, params).getString();
    }

    public static String translateDialog(String key) {
        return Component.translatable(ModUtils.LANG_CHAT + key).getString();
    }

    public static String getDisplayLevel(float level) {
        return ModUtils.translateDesc("level", "" + ChatFormatting.DARK_PURPLE + Math.round(level));
    }

    public static String getElementalTooltip(Element element) {
        return ModUtils.translateDesc("elemental_damage_desc",
                "x" + ModUtils.DF_0.format(ModConfig.balance.elemental_factor),
                element.textColor + element.symbol + ChatFormatting.GRAY);
    }

    /**
     * Determines if the chunk is already generated, in which case new structures cannot be placed
     *
     * @param box
     * @param world
     * @return
     */
    public static boolean chunksGenerated(BoundingBox box, Level world) {
        return world.isChunkGeneratedAt(box.minX >> 4, box.minZ >> 4) || world.isChunkGeneratedAt(box.minX >> 4, box.maxZ >> 4)
                || world.isChunkGeneratedAt(box.maxX >> 4, box.minZ >> 4) || world.isChunkGeneratedAt(box.maxX >> 4, box.maxZ >> 4);
    }

    /**
     * Calls the function n times, passing in the ith iteration
     *
     * @param n
     * @param func
     */
    public static void performNTimes(int n, Consumer<Integer> func) {
        for (int i = 0; i < n; i++) {
            func.accept(i);
        }
    }

    /**
     * Returns all EntityLivingBase entities in a certain bounding box
     */
    public static List<LivingEntity> getEntitiesInBox(Entity entity, AABB bb) {
        List<Entity> list = entity.level.getEntities(entity, bb);

        Predicate<Entity> isInstance = i -> i instanceof LivingEntity;
        Function<Entity, LivingEntity> cast = i -> (LivingEntity) i;

        return list.stream().filter(isInstance).map(cast).collect(Collectors.toList());
    }

    /**
     * Returns the entity's position in vector form
     */
    public static Vec3 entityPos(Entity entity) {
        return entity.position();
    }

    /*
     * Generates a generator n times in a chunk
     */
    public static void generateN(Level worldIn, Random rand, BlockPos pos, int n, int baseY, int randY, WorldGenerator gen) {
        randY = randY > 0 ? randY : 1;
        for (int i = 0; i < n; ++i) {
            int x = rand.nextInt(16) + 8;
            int y = rand.nextInt(randY) + baseY;
            int z = rand.nextInt(16) + 8;
            gen.generate(worldIn, rand, pos.offset(x, y, z));
        }
    }

    public static BlockPos posToChunk(BlockPos pos) {
        return new BlockPos(pos.getX() / 16f, pos.getY(), pos.getZ() / 16f);
    }

    /**
     * Creates a Vec3 using the pitch and yaw of the entities rotation. Taken from entity, so it can be used anywhere
     */
    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -Mth.cos(-pitch * 0.017453292F);
        float f3 = Mth.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static Vec3 yVec(double heightAboveGround) {
        return new Vec3(0, heightAboveGround, 0);
    }

    public static void handleAreaImpact(float radius, Function<Entity, Float> maxDamage, Entity source, Vec3 pos, DamageSource damageSource) {
        handleAreaImpact(radius, maxDamage, source, pos, damageSource, 1, 0);
    }

    public static void handleAreaImpact(float radius, Function<Entity, Float> maxDamage, Entity source, Vec3 pos, DamageSource damageSource,
                                        float knockbackFactor, int fireFactor) {
        handleAreaImpact(radius, maxDamage, source, pos, damageSource, knockbackFactor, fireFactor, true);
    }

    private static Vec3 getCenter(AABB box) {
        return new Vec3(box.minX + (box.maxX - box.minX) * 0.5D, box.minY + (box.maxY - box.minY) * 0.5D, box.minZ + (box.maxZ - box.minZ) * 0.5D);
    }

    public static void handleAreaImpact(float radius, Function<Entity, Float> maxDamage, Entity source, Vec3 pos, DamageSource damageSource,
                                        float knockbackFactor, int fireFactor, boolean damageDecay) {
        if (source == null) {
            return;
        }

        List<Entity> list = source.level.getEntities(source, new AABB(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z).inflate(radius));

        Predicate<Entity> isInstance = i -> i instanceof LivingEntity || i instanceof PartEntity || i.canBeCollidedWith();
        double radiusSq = Math.pow(radius, 2);

        list.stream().filter(isInstance).forEach((entity) -> {

            // Get the hitbox size of the entity because otherwise explosions are less
            // effective against larger mobs
            double avgEntitySize = entity.getBoundingBox().getSize() * 0.75;

            // Choose the closest distance from the center or the head to encourage
            // headshots
            double distance = Math.min(Math.min(getCenter(entity.getBoundingBox()).distanceTo(pos),
                    entity.position().add(ModUtils.yVec(entity.getEyeHeight())).distanceTo(pos)),
                    entity.position().distanceTo(pos));

            // Subtracting the average size makes it so that the full damage can be dealt
            // with a direct hit
            double adjustedDistance = Math.max(distance - avgEntitySize, 0);
            double adjustedDistanceSq = Math.pow(adjustedDistance, 2);
            double damageFactor = damageDecay ? Math.max(0, Math.min(1, (radiusSq - adjustedDistanceSq) / radiusSq)) : 1;

            // Damage decays by the square to make missed impacts less powerful
            double damageFactorSq = Math.pow(damageFactor, 2);
            double damage = maxDamage.apply(entity) * damageFactorSq;
            if (damage > 0 && adjustedDistanceSq < radiusSq) {
                entity.setSecondsOnFire((int) (fireFactor * damageFactorSq));
                if(entity.hurt(damageSource, (float) damage)) {
                    double entitySizeFactor = avgEntitySize == 0 ? 1 : Math.max(0.5, Math.min(1, 1 / avgEntitySize));
                    double entitySizeFactorSq = Math.pow(entitySizeFactor, 2);

                    // Velocity depends on the entity's size and the damage dealt squared
                    Vec3 velocity = getCenter(entity.getBoundingBox()).subtract(pos).normalize().scale(damageFactorSq).scale(knockbackFactor).scale(entitySizeFactorSq);
                    entity.push(velocity.x, velocity.y, velocity.z);
                }
            }
        });
    }

    public static void handleBulletImpact(Entity hitEntity, ModProjectile projectile, float damage, DamageSource damageSource) {
        handleBulletImpact(hitEntity, projectile, damage, damageSource, 0);
    }

    public static void handleBulletImpact(Entity hitEntity, ModProjectile projectile, float damage, DamageSource damageSource, int knockback) {
        handleBulletImpact(hitEntity, projectile, damage, damageSource, knockback, (p, e) -> {
        }, (p, e) -> {
        });
    }

    public static void handleBulletImpact(Entity hitEntity, ModProjectile projectile, float damage, DamageSource damageSource, int knockback,
                                          BiConsumer<ModProjectile, Entity> beforeHit, BiConsumer<ModProjectile, Entity> afterHit) {
        handleBulletImpact(hitEntity, projectile, damage, damageSource, knockback, beforeHit, afterHit, true);
    }

    public static void handleBulletImpact(Entity hitEntity, ModProjectile projectile, float damage, DamageSource damageSource, int knockback,
                                          BiConsumer<ModProjectile, Entity> beforeHit, BiConsumer<ModProjectile, Entity> afterHit, Boolean resetHurtTime) {
        if (hitEntity != null && projectile != null && projectile.shootingEntity != null && hitEntity != projectile.shootingEntity) {
            beforeHit.accept(projectile, hitEntity);
            if (projectile.isOnFire() && !(hitEntity instanceof EnderMan)) {
                hitEntity.setSecondsOnFire(5);
            }
            if (resetHurtTime) {
                hitEntity.invulnerableTime = 0;
            }
            hitEntity.hurt(damageSource, damage);
            if (knockback > 0) {
                float f1 = (float) Math.sqrt(projectile.getDeltaMovement().x * projectile.getDeltaMovement().x + projectile.getDeltaMovement().z * projectile.getDeltaMovement().z);

                if (f1 > 0.0F) {
                    hitEntity.push(projectile.getDeltaMovement().x * knockback * 0.6000000238418579D / f1, 0.1D, projectile.getDeltaMovement().z * knockback * 0.6000000238418579D / f1);
                }
            }
            afterHit.accept(projectile, hitEntity);
        }
    }

    public static Vec3 getRelativeOffset(LivingEntity actor, Vec3 offset) {
        Vec3 look = ModUtils.getVectorForRotation(0, actor.yBodyRot);
        Vec3 side = look.yRot((float) Math.PI * 0.5f);
        return look.scale(offset.x).add(yVec((float) offset.y)).add(side.scale(offset.z));
    }

    /**
     * Returns the xyz offset using the axis as the relative base
     *
     * @param axis
     * @param offset
     * @return
     */
    public static Vec3 getAxisOffset(Vec3 axis, Vec3 offset) {
        Vec3 forward = axis.normalize();
        Vec3 side = axis.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 up = axis.cross(side).normalize();
        return forward.scale(offset.x).add(side.scale(offset.z)).add(up.scale(offset.y));
    }

    public static void throwProjectile(LivingEntity actor, LivingEntity target, ModProjectile projectile) {
        throwProjectile(actor, target, projectile, 12.0f, 1.6f);
    }

    public static void throwProjectile(LivingEntity actor, Vec3 target, ModProjectile projectile, float inaccuracy, float velocity, Vec3 offset) {
        Vec3 pos = projectile.position().add(offset);
        projectile.setPos(pos.x, pos.y, pos.z);
        throwProjectile(actor, target, projectile, inaccuracy, velocity);
    }

    public static void throwProjectile(LivingEntity actor, LivingEntity target, ModProjectile projectile, float inaccuracy, float velocity, Vec3 offset) {
        Vec3 pos = projectile.position().add(offset);
        projectile.setPos(pos.x, pos.y, pos.z);
        throwProjectile(actor, target, projectile, inaccuracy, velocity);
    }

    public static void throwProjectile(LivingEntity actor, LivingEntity target, ModProjectile projectile, float inaccuracy, float velocity) {
        double d0 = target.getY() + target.getEyeHeight() - 0.9;
        throwProjectile(actor, new Vec3(target.getX(), d0, target.getZ()), projectile, inaccuracy, velocity);
    }

    public static void throwProjectile(LivingEntity actor, Vec3 target, ModProjectile projectile, float inaccuracy, float velocity) {
        throwProjectileNoSpawn(target, projectile, inaccuracy, velocity);
        actor.level.addFreshEntity(projectile);
    }

    public static void throwProjectileNoSpawn(Vec3 target, ModProjectile projectile, float inaccuracy, float velocity) {
        double d0 = target.y;
        double d1 = target.x - projectile.getX();
        double d2 = d0 - projectile.getY();
        double d3 = target.z - projectile.getZ();
        float f = (float) (projectile.isNoGravity() ? 0 : Math.sqrt(d1 * d1 + d3 * d3) * 0.2F);
        projectile.shoot(d1, d2 + f, d3, velocity, inaccuracy);
    }

    /**
     * Credit to coolAlias https://www.minecraftforge.net/forum/topic/22166-walking-on-water/
     *
     * @param entity
     * @param world
     */
    public static void walkOnWater(LivingEntity entity, Level world) {
        BlockPos pos = new BlockPos(entity.getX(), Math.floor(entity.getBoundingBox().minY), entity.getZ());
        if (world.getBlockState(pos).getBlock() == Blocks.WATER) {
            if (entity.getDeltaMovement().y < 0) {
                entity.setPos(entity.position().add(0, -entity.getDeltaMovement().y, 0)); // player is falling, so motionY is negative, but we want to reverse that
                entity.setDeltaMovement(entity.getDeltaMovement().x, 0, entity.getDeltaMovement().z); // no longer falling
            }
            entity.fallDistance = 0.0F; // otherwise I believe it adds up, which may surprise you when you come down
            entity.setOnGround(true);
        }
    }

    /**
     * The function that calculates the mob damage for any leveled mob
     *
     * @param baseAttackDamage
     * @param level
     * @return
     */
    public static float getMobDamage(double baseAttackDamage, double healthScaledAttackFactor, float maxHealth, float health, float level, Element element) {
        double leveledAttack = baseAttackDamage * LevelHandler.getMultiplierFromLevel(level) * ModConfig.balance.mob_damage;
        double healthScaledAttack = leveledAttack * healthScaledAttackFactor * (((maxHealth * 0.5) - health) / maxHealth);
        double elementalScale = element != Element.NONE ? ModConfig.balance.elemental_factor : 1;
        return (float) ((healthScaledAttack + leveledAttack) * elementalScale);
    }

    /**
     * Determine if a >= v < b
     *
     * @param a
     * @param b
     * @param v
     * @return
     */
    public static boolean isBetween(int a, int b, int v) {
        if (a > b) {
            int t = a;
            a = b;
            b = t;
        }
        return v >= a && v < b;
    }

    public static int calculateGenerationHeight(Level world, int x, int z) {
        return world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY();
    }

    /**
     * Returns -1 if the variation is too much
     */
    public static int getAverageGroundHeight(Level world, int x, int z, int sizeX, int sizeZ, int maxVariation) {
        sizeX = x + sizeX;
        sizeZ = z + sizeZ;
        int corner1 = calculateGenerationHeight(world, x, z);
        int corner2 = calculateGenerationHeight(world, sizeX, z);
        int corner3 = calculateGenerationHeight(world, x, sizeZ);
        int corner4 = calculateGenerationHeight(world, sizeX, sizeZ);

        int max = Math.max(Math.max(corner3, corner4), Math.max(corner1, corner2));
        int min = Math.min(Math.min(corner3, corner4), Math.min(corner1, corner2));
        if (max - min > maxVariation) {
            return -1;
        }
        return min;
    }

    public static String getDamageTooltip(float damage) {
        return ModUtils.translateDesc("damage_tooltip", "" + ChatFormatting.BLUE + DF_0.format(damage) + ChatFormatting.GRAY);
    }

    public static String getCooldownTooltip(float cooldown) {
        return ModUtils.translateDesc("gun_reload_time", ChatFormatting.BLUE + "" + DF_0.format(cooldown * 0.05) + ChatFormatting.GRAY);
    }

    public static float getEnchantedDamage(ItemStack stack, float level, float damage) {
        float maxPower = ModEnchantments.gun_power.getMaxLevel();
        float power = stack.getEnchantmentLevel(ModEnchantments.gun_power);
        float maxDamageBonus = (float) Math.pow(ModConfig.balance.progression_scale, 2); // Maximum damage is two levels above
        float enchantmentBonus = 1 + ((power / maxPower) * (maxDamageBonus - 1));
        return damage * enchantmentBonus * LevelHandler.getMultiplierFromLevel(level);
    }

    public static int getGunAmmoUse(float level) {
        return Math.round(LevelHandler.getMultiplierFromLevel(level));
    }

    public static InvasionWorldSaveData getInvasionData(Level world) {
    	LevelData storage = world.getLevelData();
        InvasionWorldSaveData instance = (InvasionWorldSaveData) storage.getOrLoadData(InvasionWorldSaveData.class, InvasionWorldSaveData.DATA_NAME);

        if (instance == null) {
            instance = new InvasionWorldSaveData();
            storage.setData(InvasionWorldSaveData.DATA_NAME, instance);
        }
        return instance;
    }

    public static float calculateElementalDamage(Element mobElement, Element damageElement, float amount) {
        if (mobElement.matchesElement(damageElement)) {
            return amount * 1.5f;
        }
        return amount;
    }

    public static void doSweepAttack(Player player, @Nullable LivingEntity target, Element element, Consumer<LivingEntity> perEntity) {
        doSweepAttack(player, target, element, perEntity, 9, 1);
    }

    public static void doSweepAttack(Player player, @Nullable LivingEntity target, Element element, Consumer<LivingEntity> perEntity, float maxDistanceSq, float areaSize) {
        float attackDamage = (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
        float sweepDamage = Math.min(0.15F + EnchantmentHelper.getSweepingDamageRatio(player), 1) * attackDamage;

        AABB box;

        if (target != null) {
            box = target.getBoundingBox();
        } else {
            Vec3 center = ModUtils.getAxisOffset(player.getLookAngle(), new Vec3(areaSize * 1.5, 0, 0)).add(player.getEyePosition(1));
            box = makeBox(center, center).inflate(areaSize * 0.5, areaSize, areaSize * 0.5);
        }

        for (LivingEntity entitylivingbase : player.level.getEntitiesOfClass(LivingEntity.class, box.inflate(areaSize, 0.25D, areaSize))) {
            if (entitylivingbase != player && entitylivingbase != target && !player.isAlliedTo(entitylivingbase) && player.distanceToSqr(entitylivingbase) < maxDistanceSq) {
                entitylivingbase.knockback(0.4F, Mth.sin(player.getYRot() * 0.017453292F), (-Mth.cos(player.getYRot() * 0.017453292F)));
                entitylivingbase.hurt(ModDamageSource.causeElementalPlayerDamage(player, element), sweepDamage);
                perEntity.accept(entitylivingbase);
            }
        }

        player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 0.9F);

        // Spawn colored sweep particles
        if (!player.level.isClientSide && player instanceof ServerPlayer) {
            Main.NETWORK.sendTo(new MessageModParticles(EnumModParticles.SWEEP_ATTACK, getCenter(box), Vec3.ZERO, element.sweepColor), (ServerPlayer) player);
        }

        Entity particle = new ParticleSpawnerSwordSwing(player.level);
        ModUtils.setEntityPosition(particle, getCenter(box));
        player.level.addFreshEntity(particle);
    }

    /**
     * Provides multiple points in a circle via a callback
     *
     * @param radius          The radius of the circle
     * @param points          The number of points around the circle
     * @param particleSpawner
     */
    public static void circleCallback(float radius, int points, Consumer<Vec3> particleSpawner) {
        float degrees = 360f / points;
        for (int i = 0; i < points; i++) {
            double radians = Math.toRadians(i * degrees);
            Vec3 offset = new Vec3(Math.sin(radians), Math.cos(radians), 0).scale(radius);
            particleSpawner.accept(offset);
        }
    }

    public static List<Vec3> circlePoints(float radius, int numPoints) {
        List<Vec3> points = new ArrayList<>();
        circleCallback(radius, numPoints, points::add);
        return points;
    }

    /*
     * Does the elemental and leveled calculations for damage
     */
    public static float getArmoredDamage(DamageSource source, float amount, float level, Element element) {
        amount *= ModConfig.balance.mob_armor;

        if (!source.isBypassArmor()) {
            if (element != element.NONE) {
                amount /= ModConfig.balance.elemental_factor;
            }
            amount = amount * LevelHandler.getArmorFromLevel(level);
        }

        if (source instanceof IElement) {
            amount = ModUtils.calculateElementalDamage(element, ((IElement) source).getElement(), amount);
        }

        return amount;
    }

    public static void leapTowards(LivingEntity entity, Vec3 target, float horzVel, float yVel) {
        Vec3 dir = target.subtract(entity.position()).normalize();
        Vec3 leap = new Vec3(dir.x, 0, dir.z).normalize().scale(horzVel).add(ModUtils.yVec(yVel));
        entity.setDeltaMovement(entity.getDeltaMovement().add(leap.x, 0, 0));
        if (entity.getDeltaMovement().y < 0.1) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, leap.y, 0));
        }
        entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0, leap.z));

        // Normalize to make sure the velocity doesn't go beyond what we expect
        double horzMag = Math.sqrt(Math.pow(entity.getDeltaMovement().x, 2) + Math.pow(entity.getDeltaMovement().z, 2));
        double scale = horzVel / horzMag;
        if (scale < 1) {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(scale, 1, scale));
        }
    }

    /**
     * Calls a function that linearly interpolates between two points. Includes both ends of the line
     *
     * @param start
     * @param end
     * @param points
     * @param callback
     */
    public static void lineCallback(Vec3 start, Vec3 end, int points, BiConsumer<Vec3, Integer> callback) {
        Vec3 dir = end.subtract(start).scale(1 / (float) (points - 1));
        Vec3 pos = start;
        for (int i = 0; i < points; i++) {
            callback.accept(pos, i);
            pos = pos.add(dir);
        }
    }

    public static int tryParseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static float clamp(double value, double min, double max) {
        return (float) Math.max(min, Math.min(max, value));
    }

    public static Vec3 findEntityGroupCenter(Entity mob, double d) {
        Vec3 groupCenter = mob.position();
        float numMobs = 1;
        for (LivingEntity entity : ModUtils.getEntitiesInBox(mob, new AABB(mob.blockPosition()).inflate(d))) {
            if (entity instanceof EntityMaelstromMob && !(entity instanceof EntityMaelstromHealer)) {
                groupCenter = groupCenter.add(entity.position());
                numMobs += 1;
            }
        }

        return groupCenter.scale(1 / numMobs);
    }

    public static boolean isAirBelow(Level world, BlockPos pos, int blocksBelow) {
        boolean hasGround = false;
        for (int i = 0; i >= -blocksBelow; i--) {
            if (!world.isEmptyBlock(pos.offset(new BlockPos(0, i, 0)))) {
                hasGround = true;
            }
        }
        return !hasGround;
    }

    public static void facePosition(Vec3 pos, Entity entity, float maxYawIncrease, float maxPitchIncrease) {
        double d0 = pos.x - entity.getX();
        double d2 = pos.z - entity.getZ();
        double d1 = pos.y - entity.getY();

        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
        float f1 = (float) (-(Mth.atan2(d1, d3) * (180D / Math.PI)));
        entity.setXRot(updateRotation(entity.getXRot(), f1, maxPitchIncrease));
        entity.setYRot(updateRotation(entity.getYRot(), f, maxYawIncrease));
    }

    private static float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = Mth.wrapDegrees(targetAngle - angle);

        if (f > maxIncrease) {
            f = maxIncrease;
        }

        if (f < -maxIncrease) {
            f = -maxIncrease;
        }

        return angle + f;
    }

    /**
     * Rotate a normalized vector around an axis by given degrees https://stackoverflow.com/questions/31225062/rotating-a-vector-by-angle-and-axis-in-java
     *
     * @param vec
     * @param axis
     * @param degrees
     * @return
     */
    @Deprecated
    public static Vec3 rotateVector(Vec3 vec, Vec3 axis, double degrees) {
        double theta = Math.toRadians(degrees);
        double x, y, z;
        double u, v, w;
        x = vec.x;
        y = vec.y;
        z = vec.z;
        u = axis.x;
        v = axis.y;
        w = axis.z;
        double xPrime = u * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                + x * Math.cos(theta)
                + (-w * y + v * z) * Math.sin(theta);
        double yPrime = v * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                + y * Math.cos(theta)
                + (w * x - u * z) * Math.sin(theta);
        double zPrime = w * (u * x + v * y + w * z) * (1d - Math.cos(theta))
                + z * Math.cos(theta)
                + (-v * x + u * y) * Math.sin(theta);
        return new Vec3(xPrime, yPrime, zPrime).normalize();
    }

    public static Vec3 rotateVector2(Vec3 v, Vec3 k, double degrees) {
        double theta = Math.toRadians(degrees);
        k = k.normalize();
        return v
                .scale(Math.cos(theta))
                .add(k.cross(v)
                        .scale(Math.sin(theta)))
                .add(k.scale(k.dot(v))
                        .scale(1 - Math.cos(theta)));
    }

    // http://www.java-gaming.org/index.php/topic,28253
    public static double unsignedAngle(Vec3 a, Vec3 b) {
        double dot = a.dot(b);
        double cos = dot / (a.length() * b.length());
        return Math.acos(cos);
    }

    /**
     * Pitch of a vector in degrees 90 is up, -90 is down.
     */
    public static double toPitch(Vec3 vec) {
        double angleBetweenYAxis = Math.toDegrees(unsignedAngle(vec, ModUtils.Y_AXIS.scale(-1)));
        return angleBetweenYAxis - 90;
    }

    /**
     * Taken from EntityDragon. Destroys blocks in a bounding box. Returns whether it failed to destroy some blocks.
     *
     * @param box
     * @param world
     * @param entity
     * @return
     */
    public static void destroyBlocksInAABB(AABB box, Level world, Entity entity) {
        int i = Mth.floor(box.minX);
        int j = Mth.floor(box.minY);
        int k = Mth.floor(box.minZ);
        int l = Mth.floor(box.maxX);
        int i1 = Mth.floor(box.maxY);
        int j1 = Mth.floor(box.maxZ);

        for (int x = i; x <= l; ++x) {
            for (int y = j; y <= i1; ++y) {
                for (int z = k; z <= j1; ++z) {
                    BlockPos blockpos = new BlockPos(x, y, z);
                    BlockState iblockstate = world.getBlockState(blockpos);
                    Block block = iblockstate.getBlock();

                    if (!iblockstate.isAir() && iblockstate.getMaterial() != Material.FIRE) {
                        if (ForgeEventFactory.getMobGriefingEvent(world, entity)) {
                            if (block != Blocks.COMMAND_BLOCK &&
                                    block != Blocks.REPEATING_COMMAND_BLOCK &&
                                    block != Blocks.CHAIN_COMMAND_BLOCK &&
                                    block != Blocks.BEDROCK &&
                                    !(block instanceof LiquidBlock)) {
                                if (world.getNearestPlayer(blockpos.getX(), blockpos.getY(), blockpos.getZ(), 20, false) != null) {
                                    world.destroyBlock(blockpos, false);
                                } else {
                                    world.setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the look vector from pitch and yaw, where 0 pitch is forward, negative 90 pitch is down
     * Yaw is the negative rotation of the z vector.
     *
     * @param pitch
     * @param yaw
     * @return
     */
    public static Vec3 getLookVec(float pitch, float yaw) {
        Vec3 yawVec = ModUtils.rotateVector2(ModUtils.Z_AXIS, ModUtils.Y_AXIS, -yaw);
        return ModUtils.rotateVector2(yawVec, yawVec.cross(ModUtils.Y_AXIS), pitch);
    }

    /**
     * Finds all entities that collide with the line specified by two vectors, excluding a certain entity
     *
     * @param start
     * @param end
     * @param world
     * @param toExclude
     * @return
     */
    public static List<Entity> findEntitiesInLine(Vec3 start, Vec3 end, Level world, @Nullable Entity toExclude) {
        return world.getEntities(toExclude, new AABB(start.x, start.y, start.z, end.x, end.y, end.z), (e) -> {
            HitResult raytraceresult = e.getBoundingBox().calculateIntercept(start, end);
            return raytraceresult != null;
        });
    }

    /**
     * Taken from {@code EntityLivingBase#travel(float, float, float)} The purpose is to let my custom elytras still have the fly into wall damage
     */
    public static void handleElytraTravel(LivingEntity entity) {
        if (entity.isServerWorld() || entity.canPassengerSteer()) {
            if (!entity.isInWater() || entity instanceof Player && ((Player) entity).capabilities.isFlying) {
                if (!entity.isInLava() || entity instanceof Player && ((Player) entity).capabilities.isFlying) {
                    if (entity.motionY > -0.5D) {
                        entity.fallDistance = 1.0F;
                    }

                    Vec3 vec3d = entity.getLookVec();
                    float f = entity.rotationPitch * 0.017453292F;
                    double d6 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
                    double d8 = Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
                    double d1 = vec3d.lengthVector();
                    float f4 = Mth.cos(f);
                    f4 = (float) ((double) f4 * (double) f4 * Math.min(1.0D, d1 / 0.4D));
                    entity.motionY += -0.08D + f4 * 0.06D;

                    if (entity.motionY < 0.0D && d6 > 0.0D) {
                        double d2 = entity.motionY * -0.1D * f4;
                        entity.motionY += d2;
                        entity.motionX += vec3d.x * d2 / d6;
                        entity.motionZ += vec3d.z * d2 / d6;
                    }

                    if (f < 0.0F) {
                        double d10 = d8 * (-Mth.sin(f)) * 0.04D;
                        entity.motionY += d10 * 3.2D;
                        entity.motionX -= vec3d.x * d10 / d6;
                        entity.motionZ -= vec3d.z * d10 / d6;
                    }

                    if (d6 > 0.0D) {
                        entity.motionX += (vec3d.x / d6 * d8 - entity.motionX) * 0.1D;
                        entity.motionZ += (vec3d.z / d6 * d8 - entity.motionZ) * 0.1D;
                    }

                    entity.motionX *= 0.9900000095367432D;
                    entity.motionY *= 0.9800000190734863D;
                    entity.motionZ *= 0.9900000095367432D;
                    entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);

                    if (entity.collidedHorizontally && !entity.level.isClientSide) {
                        double d11 = Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
                        double d3 = d8 - d11;
                        float f5 = (float) (d3 * 10.0D - 3.0D);

                        if (f5 > 0.0F) {
                            entity.playSound(SoundEvents.PLAYER_SMALL_FALL, 1.0F, 1.0F);
                            entity.attackEntityFrom(DamageSource.FLY_INTO_WALL, f5);
                        }
                    }
                }
            }
        }
    }

    public static @Nullable
    EntityLeveledMob spawnMob(Level world, BlockPos pos, float level, Config algorithmConfig) {
        return spawnMob(world, pos, level, algorithmConfig, true);
    }

    public static @Nullable
    EntityLeveledMob spawnMob(Level world, BlockPos pos, float level, Config algorithmConfig, boolean findGround) {
        List<? extends Config> configs = algorithmConfig.getConfigList("mobs");

        BlockPos spawnRange = new BlockPos(algorithmConfig.getInt("spawning_area.width"),
                algorithmConfig.getInt("spawning_area.height"),
                algorithmConfig.getInt("spawning_area.width"));
        int[] mobWeights = getMobsThatCanSpawn(world, pos, algorithmConfig);

        Function<Config, int[]> getElementalWeights = config -> config.getConfigList("elements").stream()
                .mapToInt(c -> c.getInt("weight")).toArray();

        Function<Config, Element[]> getElementalIds = config -> config.getConfigList("elements").stream()
                .mapToInt(c -> c.getInt("id"))
                .mapToObj(Element::getElementFromId)
                .toArray(Element[]::new);

        MobSpawnData[] data = configs.stream().map(config -> {
            MobSpawnData newSpawnData;

            if (config.hasPath("elements")) {
                int[] elementWeights = getElementalWeights.apply(config);
                Element[] elementIds = getElementalIds.apply(config);
                newSpawnData = new MobSpawnData(config.getString("entity_id"), elementIds, elementWeights, 1);
            } else {
                newSpawnData = new MobSpawnData(config.getString("entity_id"), Element.NONE);
            }

            if (config.hasPath("nbt_spawn_data")) {
                CompoundTag spawnData = parseNBTFromConfig(config.getConfig("nbt_spawn_data"));
                spawnData.setString("id", config.getString("entity_id"));
                newSpawnData.addMobNBT(spawnData);
            }

            return newSpawnData;
        }).toArray(MobSpawnData[]::new);

        return ModUtils.spawnMob(world, pos, level, data, mobWeights, spawnRange, findGround);
    }

    private static int[] getMobsThatCanSpawn(Level world, BlockPos pos, Config algorithmConfig) {
        List<? extends Config> configs = algorithmConfig.getConfigList("mobs");
        BlockPos mobDetectionRange = new BlockPos(algorithmConfig.getInt("mob_cap_detection_area.width"),
                algorithmConfig.getInt("mob_cap_detection_area.height"),
                algorithmConfig.getInt("mob_cap_detection_area.width"));

        AABB detectionArea = new AABB(pos).grow(mobDetectionRange.getX() * 0.5, mobDetectionRange.getY() * 0.5, mobDetectionRange.getZ() * 0.5);

        Function<String, Integer> getCountOfMobsById = mobId -> (int) world.getEntitiesOfClass(LivingEntity.class, detectionArea).stream()
                .filter((e) -> {
                    EntityEntry registry = EntityRegistry.getEntry(e.getClass());
                    if (registry != null) {
                        return registry.getRegistryName() != null && registry.getRegistryName().toString().equals(mobId);
                    }
                    return false;
                }).count();

        Function<Config, Integer> filterOutMobsOverCap = config -> {
            if (config.hasPath("max_nearby") && getCountOfMobsById.apply(config.getString("entity_id")) > config.getInt("max_nearby")) {
                return 0;
            }
            return config.hasPath("spawn_weight") ? config.getInt("spawn_weight") : 1;
        };

        return configs.stream().map(filterOutMobsOverCap).mapToInt(x -> x).toArray();
    }

    /**
     * Attempts to spawn a mob around the actor within a certain range. Returns null if the spawning failed. Otherwise returns the spawned mob
     */
    private static @Nullable
    EntityLeveledMob spawnMob(Level world, BlockPos pos, float level, MobSpawnData[] mobs, int[] weights, BlockPos range, boolean findGround) {
        Random random = new Random();

        if(weights.length == 0 || Arrays.stream(weights).reduce(Integer::sum).getAsInt() == 0) return null;

        MobSpawnData data = ModRandom.choice(mobs, random, weights).next();
        int tries = 100;
        for (int i = 0; i < tries; i++) {
            // Find a random position to spawn the enemy
            int x = pos.getX() + ModRandom.range(-range.getX(), range.getX());
            int y = pos.getY() + ModRandom.range(-range.getY(), range.getY());
            int z = pos.getZ() + ModRandom.range(-range.getZ(), range.getZ());

            BlockPos spawnPos = new BlockPos(x, y, z);

            if(findGround) {
                spawnPos = ModUtils.findGroundBelow(world, spawnPos).above();
                int lowestY = pos.getY() - range.getY();
                if(spawnPos.getY() < lowestY) continue;
            }

            if (!findGround || world.getBlockState(spawnPos.below()).isFaceSturdy(world, spawnPos.below(), Direction.UP)) {
                Entity mob = createMobFromSpawnData(data, world, x + 0.5, spawnPos.getY(), z + 0.5);

                if (!(mob instanceof EntityLeveledMob)) continue;

                boolean notNearPlayer = !world.isAnyPlayerWithinRangeAt(x, spawnPos.getY(), z, 3.0D);
                boolean clearAroundHitbox = world.getCollisionBoxes(mob, mob.getBoundingBox()).isEmpty();
                boolean noLiquid = !world.containsAnyLiquid(mob.getBoundingBox());

                if (notNearPlayer && clearAroundHitbox && noLiquid) {
                    EntityLeveledMob leveledMob = (EntityLeveledMob) mob;

                    level.addFreshEntity(leveledMob);
                    leveledMob.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(mob)), null);
                    leveledMob.spawnExplosionParticle();

                    leveledMob.setElement(ModRandom.choice(data.possibleElements, random, data.elementalWeights).next());
                    leveledMob.setLevel(level);

                    return leveledMob;
                }
            }
        }
        return null;
    }

    /**
     * Implements the way to create a mob from the spawn data object that most spawners in the mod uses
     */
    public static @Nullable
    Entity createMobFromSpawnData(MobSpawnData data, Level world, double x, double y, double z) {
        Entity entity;
        if (data.mobData != null) {
            // Read entity with custom NBT
            entity = AnvilChunkLoader.readWorldEntityPos(data.mobData, world, x, y, z, true);
        } else {
            // Read just the default entity
            entity = EntityList.createEntityByIDFromName(new ResourceLocation(data.mobId), world);
        }

        if (entity == null) {
            System.out.println("Failed to spawn entity with id " + data.mobId);
            return null;
        }

        entity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);

        return entity;
    }

    public static void setEntityPosition(Entity entity, Vec3 vec) {
        entity.setPos(vec.x, vec.y, vec.z);
    }

    public static void setEntityVelocity(Entity entity, Vec3 vec) {
    	entity.setDeltaMovement(vec);
    }

    public static void addEntityVelocity(Entity entity, Vec3 vec) {
        entity.push(vec.x, vec.y, vec.z);
    }

    public static Vec3 getEntityVelocity(Entity entity) {
    	return entity.getDeltaMovement();
    }

    /**
     * Removes all entity ai of a certain class type.
     */
    public static <T extends Goal> void removeTaskOfType(GoalSelector tasks, Class<T> clazz) {
        Set<Goal> toRemove = Sets.newHashSet();

        for (WrappedGoal entry : tasks.getAvailableGoals()) {
            if (clazz.isInstance(entry.getGoal())) {
                toRemove.add(entry.getGoal());
            }
        }

        for (Goal ai : toRemove) {
            tasks.removeGoal(ai);
        }
    }

    /**
     * Finds the first solid block below the specified position and returns the position of that block
     */
    public static BlockPos findGroundBelow(Level world, BlockPos pos) {
        for (int i = pos.getY(); i > 0; i--) {
            BlockPos tempPos = new BlockPos(pos.getX(), i, pos.getZ());
            if (world.getBlockState(tempPos).isFaceSturdy(world, tempPos, Direction.UP)) {
                return tempPos;
            }
        }
        return new BlockPos(pos.getX(), 0, pos.getZ());
    }

    /**
     * Because the stupid constructor is client side only
     */
    public static AABB makeBox(Vec3 pos1, Vec3 pos2) {
        return new AABB(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
    }

    /**
     * Lets entities see you through glass
     */
    public static boolean canEntityBeSeen(Entity viewer, Entity target) {
        HitResult result = viewer.level.clip(new ClipContext(viewer.getEyePosition(1), target.getEyePosition(1), ClipContext.Block.COLLIDER, Fluid.NONE, viewer));
        if (result != null && result.getType() == HitResult.Type.BLOCK) {
            BlockState blockState = viewer.level.getBlockState(((BlockHitResult)result).getBlockPos());
            if (blockState.isFullCube()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Aims to reduce the number of instanceof checks for EntityLivingBase because of changes in the code can cause instanceof check to become insufficient
     * <p>
     * Currently also handles entities comprised of multiple hitboxes
     */
    public static @Nullable
    LivingEntity getLivingEntity(@Nullable Entity entity) {
        if (entity instanceof LivingEntity) {
            return (LivingEntity) entity;
        } else if (entity instanceof PartEntity && ((PartEntity) entity).getParent() instanceof LivingEntity) {
            return (LivingEntity) ((PartEntity) entity).getParent();
        }
        return null;
    }

    /**
     * Find the closest entity that satisfies the condition given
     *
     * @param entityToExclude
     * @param box
     * @param condition
     * @return
     */
    public static @Nullable
    LivingEntity closestEntityExcluding(@Nullable Entity entityToExclude, AABB box, Predicate<LivingEntity> condition) {
        LivingEntity closestEntity = null;
        for (LivingEntity entity : ModUtils.getEntitiesInBox(entityToExclude, box)) {
            if (condition.test(entity) && (closestEntity == null || entity.distanceTo(entityToExclude) < closestEntity.distanceTo(entityToExclude))) {
                closestEntity = entity;
            }
        }
        return closestEntity;
    }

    /**
     * Treats input as a vector and finds the length of that vector
     *
     * @param values
     * @return
     */
    public static double mag(double... values) {
        double sum = 0;
        for (double value : values) {
            sum += Math.pow(value, 2);
        }
        return Math.sqrt(sum);
    }

    public static int minutesToTicks(int minutes) {
        return minutes * 60 * 20;
    }

    public static int secondsToTicks(int seconds) {
        return seconds * 20;
    }

    public static CompoundTag parseNBTFromConfig(Config config) {
        try {
            return JsonToNBT.getTagFromJson(config.root().render(ConfigRenderOptions.concise()));
        } catch (NBTException e) {
            Main.LOG.error("Malformed NBT tag", e);
        }
        return new CompoundTag();
    }

    public static boolean canBlockDamageSource(DamageSource damageSourceIn, LivingEntity entity)
    {
        if (!damageSourceIn.isBypassArmor() && entity.isBlocking())
        {
            Vec3 vec3d = damageSourceIn.getSourcePosition();

            if (vec3d != null)
            {
                Vec3 vec3d1 = entity.getViewVector(1.0F);
                Vec3 vec3d2 = vec3d.subtract(entity.position()).normalize();
                vec3d2 = new Vec3(vec3d2.x, 0.0D, vec3d2.z);

                if (vec3d2.dot(vec3d1) < 0.0D)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static List<Vec3> cubePoints(int xMin, int yMin, int zMin, int xMax, int yMax, int zMax) {
        List<Vec3> points = new ArrayList<>();
        for(int x = xMin; x < xMax; x++) {
            for(int y = yMin; y < yMax; y++) {
                for(int z = zMin; z < zMax; z++) {
                    points.add(new Vec3(x, y, z));
                }
            }
        }
        return points;
    }

    public static void aerialTravel(LivingEntity entity, float strafe, float vertical, float forward) {
        if (entity.isInWater()) {
            entity.moveRelative(strafe, vertical, forward, 0.02F);
            entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);
            entity.motionX *= 0.800000011920929D;
            entity.motionY *= 0.800000011920929D;
            entity.motionZ *= 0.800000011920929D;
        } else if (entity.isInLava()) {
            entity.moveRelative(strafe, vertical, forward, 0.02F);
            entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);
            entity.motionX *= 0.5D;
            entity.motionY *= 0.5D;
            entity.motionZ *= 0.5D;
        } else {
            float f = 0.91F;

            if (entity.onGround) {
                BlockPos underPos = new BlockPos(Mth.floor(entity.posX), Mth.floor(entity.getBoundingBox().minY) - 1, Mth.floor(entity.posZ));
                BlockState underState = entity.world.getBlockState(underPos);
                f = underState.getBlock().getSlipperiness(underState, entity.world, underPos, entity) * 0.91F;
            }

            float f1 = 0.16277136F / (f * f * f);
            entity.moveRelative(strafe, vertical, forward, entity.onGround ? 0.1F * f1 : 0.02F);
            f = 0.91F;

            if (entity.onGround) {
                BlockPos underPos = new BlockPos(Mth.floor(entity.posX), Mth.floor(entity.getBoundingBox().minY) - 1, Mth.floor(entity.posZ));
                BlockState underState = entity.world.getBlockState(underPos);
                f = underState.getBlock().getSlipperiness(underState, entity.world, underPos, entity) * 0.91F;
            }

            entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);
            entity.motionX *= f;
            entity.motionY *= f;
            entity.motionZ *= f;
        }

        entity.prevLimbSwingAmount = entity.limbSwingAmount;
        double d1 = entity.posX - entity.prevPosX;
        double d0 = entity.posZ - entity.prevPosZ;
        float f2 = Mth.sqrt(d1 * d1 + d0 * d0) * 4.0F;

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        entity.limbSwingAmount += (f2 - entity.limbSwingAmount) * 0.4F;
        entity.limbSwing += entity.limbSwingAmount;
    }

    public static boolean attemptTeleport(Vec3 pos, LivingEntity entity)
    {
        double d0 = entity.getX();
        double d1 = entity.getY();
        double d2 = entity.getZ();
        ModUtils.setEntityPosition(entity, pos);
        boolean flag = false;
        BlockPos blockpos = entity.blockPosition();
        Level world = entity.level;
        RandomSource random = entity.getRandom();

        if (world.isBlockLoaded(blockpos))
        {
            entity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);

            if (world.getCollisionBoxes(entity, entity.getBoundingBox()).isEmpty() && !world.containsAnyLiquid(entity.getBoundingBox()))
            {
                flag = true;
            }
        }

        if (!flag)
        {
            entity.setPositionAndUpdate(d0, d1, d2);
            return false;
        }
        else
        {
            for (int j = 0; j < 128; ++j)
            {
                double d6 = (double)j / 127.0D;
                float f = (random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                double d3 = d0 + (entity.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double)entity.width * 2.0D;
                double d4 = d1 + (entity.posY - d1) * d6 + random.nextDouble() * (double)entity.height;
                double d5 = d2 + (entity.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double)entity.width * 2.0D;
                world.spawnParticle(ParticleTypes.PORTAL, d3, d4, d5, f, f1, f2);
            }

            if (entity instanceof EntityCreature)
            {
                ((EntityCreature)entity).getNavigator().clearPath();
            }

            return true;
        }
    }

    public static Vec3 planeProject(Vec3 vec, Vec3 plane)
    {
        return ModUtils.rotateVector2(vec.cross(plane), plane, 90);
    }

    public static boolean mobGriefing(Level world, Entity entity){
        return ForgeEventFactory.getMobGriefingEvent(world, entity);
    }

    public static AABB vecBox(Vec3 vec1, Vec3 vec2) {
        return new AABB(vec1.x, vec1.y, vec1.z, vec2.x, vec2.y, vec2.z);
    }

    public static SoundEvent getConfiguredSound(SoundEvent sound, SoundEvent fallback){
        if (Main.soundsConfig.getBoolean(sound.soundName.getResourcePath())) {
            return sound;
        } else {
            return fallback;
        }
    }

    public static Vec2f getPitchYaw(Vec3 look) {
        double d3 = (double) Mth.sqrt(look.x * look.x + look.z * look.z);
        float yaw = (float)(Mth.atan2(look.z, look.x) * (180D / Math.PI)) - 90.0F;
        float pitch = (float)(-(Mth.atan2(look.y, d3) * (180D / Math.PI)));
        return new Vec2f(pitch, yaw);
    }

    public static void avoidOtherEntities(Entity entity, double speed, int detectionSize, Predicate<? super Entity> filter) {
        double boundingBoxEdgeLength = entity.getBoundingBox().getSize() * 0.5;
        double distanceSq = Math.pow(detectionSize + boundingBoxEdgeLength, 2);

        BiFunction<Vec3, Entity, Vec3> accumulator = (vec, e) ->
                vec.add(entity.position().subtract(e.position()).normalize())
                        .scale((distanceSq - entity.distanceToSqr(e)) / distanceSq);

        Vec3 avoid = entity.level.getEntities(entity,
                entity.getBoundingBox().inflate(detectionSize),
                filter::test).parallelStream()
                .reduce(Vec3.ZERO, accumulator, Vec3::add)
                .scale(speed);

        ModUtils.addEntityVelocity(entity, avoid);
    }

    public static void homeToPosition(Entity entity, double speed, Vec3 target) {
        Vec3 velocityChange = getVelocityToTarget(entity, target).scale(speed);
        ModUtils.addEntityVelocity(entity, velocityChange);
    }

    private static Vec3 getVelocityToTarget(Entity entity, Vec3 target) {
        Vec3 velocityDirection = ModUtils.getEntityVelocity(entity).normalize();
        Vec3 desiredDirection = target.subtract(entity.position()).normalize();
        return desiredDirection.subtract(velocityDirection).normalize();
    }

    public static List<Vec3> getBoundingBoxCorners(AABB box) {
        return new ArrayList<>(Arrays.asList(
                new Vec3(box.maxX, box.maxY, box.maxZ),
                new Vec3(box.maxX, box.maxY, box.minZ),
                new Vec3(box.maxX, box.minY, box.maxZ),
                new Vec3(box.maxX, box.minY, box.minZ),
                new Vec3(box.minX, box.maxY, box.maxZ),
                new Vec3(box.minX, box.maxY, box.minZ),
                new Vec3(box.minX, box.minY, box.maxZ),
                new Vec3(box.minX, box.minY, box.minZ)));
    }

    public static Vec3 direction(Vec3 from, Vec3 to) {
        return to.subtract(from).normalize();
    }

    public static void faceDirection(Mob entity, Vec3 target, int maxDegreeIncrease) {
        ModUtils.facePosition(target, entity, 15, 15);
        entity.getLookControl().setLookAt(target.x, target.y, target.z, 15, 15);
        if (entity instanceof IPitch) {
            ((IPitch)entity).setPitch(target.subtract(entity.getEyePosition(1)));
        }
    }
}
