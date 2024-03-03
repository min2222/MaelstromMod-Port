package com.barribob.mm.entity.entities;

import javax.annotation.Nonnull;

import com.barribob.mm.Main;
import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.ai.EntityAIAvoidCrowding;
import com.barribob.mm.entity.ai.EntityAIFollowAttackers;
import com.barribob.mm.entity.ai.EntityAIWanderWithGroup;
import com.barribob.mm.init.ModDimensions;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.ManaProvider;
import com.barribob.mm.packets.MessageMana;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LootTableHandler;
import com.barribob.mm.util.handlers.ParticleManager;
import com.google.common.base.Predicate;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * The base mob that most mobs in this mod will extend A lot of these methods are from the EntityMob class to make it behave similarly
 */
public abstract class EntityMaelstromMob extends EntityLeveledMob implements RangedAttackMob {
    // Swinging arms is the animation for the attack
    private static final EntityDataAccessor<Boolean> SWINGING_ARMS = SynchedEntityData.<Boolean>defineId(EntityLeveledMob.class, EntityDataSerializers.BOOLEAN);
    public static final Predicate<LivingEntity> CAN_TARGET = entity -> {
        boolean isConfigFriend = false;
        if (entity != null) {
            ResourceLocation registryName = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
            if(registryName != null) {
                isConfigFriend = Main.maelstromFriendsConfig.getStringList("maelstrom_friends")
                        .contains(registryName.toString());
            }
        }

        return !(entity instanceof EntityMaelstromMob) && !isConfigFriend;
    };

    public static boolean isMaelstromMob(LivingEntity entity) {
        return !CAN_TARGET.apply(entity);
    }

    public EntityMaelstromMob(EntityType<? extends EntityMaelstromMob> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(5, new EntityAIFollowAttackers(this, 1.0D));
        this.goalSelector.addGoal(6, new EntityAIAvoidCrowding(this, 1.0D));
        this.goalSelector.addGoal(7, new EntityAIWanderWithGroup(this, 1.0D));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<Player>(this, Player.class, 1, true, false, null){
            @Override
            @Nonnull
            protected AABB getTargetSearchArea(double targetDistance) {
                return EntityMaelstromMob.this.getTargetableArea(targetDistance);
            }
        });

        if (ModConfig.entities.attackAll) {
            // This makes it so that the entity attack every entity except others of its kind
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Mob>(this, Mob.class, 10, true, false, CAN_TARGET){
                @Override
                @Nonnull
                protected AABB getTargetSearchArea(double targetDistance) {
                    return EntityMaelstromMob.this.getTargetableArea(targetDistance);
                }
            });
        }
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.getBoundingBox().inflate(targetDistance, 4.0D, targetDistance);
    }
    
    public static AttributeSupplier.Builder createAttributes() {
    	return EntityLeveledMob.createAttributes()
    			.add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D)
    			.add(Attributes.FOLLOW_RANGE,20.0D);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.HOSTILE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HOSTILE_DEATH;
    }

    @Override
    protected SoundEvent getFallSound(int heightIn) {
        return heightIn > 4 ? SoundEvents.HOSTILE_BIG_FALL : SoundEvents.HOSTILE_SMALL_FALL;
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        if (this.getElement().equals(Element.AZURE)) {
            return LootTableHandler.AZURE_MAELSTROM;
        } else if (this.getElement().equals(Element.GOLDEN)) {
            return LootTableHandler.GOLDEN_MAELSTROM;
        } else if (this.getElement().equals(Element.CRIMSON)) {
            return LootTableHandler.CRIMSON_MAELSTROM;
        }

        return LootTableHandler.MAELSTROM;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    
    @Override
    public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
    	return this.level.getDifficulty() != Difficulty.PEACEFUL && super.checkSpawnRules(pLevel, pSpawnReason);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    /**
     * Entity won't drop items or experience points if this returns false
     */
    @Override
    protected boolean canDropLoot() {
        return true;
    }

    /**
     * Changes the default "white smoke" spawning from a mob spawner to a purple smoke
     */
    @Override
    public void spawnAnim() {
        if (this.level.isClientSide) {
            for (int i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                ParticleManager.spawnMaelstromLargeSmoke(level, random, new Vec3(this.getX() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth() - d0 * 10.0D,
                        this.getY() + this.random.nextFloat() * this.getBbHeight() - d1 * 10.0D, this.getZ() + this.random.nextFloat() * this.getBbWidth() * 2.0F - this.getBbWidth() - d2 * 10.0D));
            }
        } else {
            this.level.broadcastEntityEvent(this, (byte) 20);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof LivingEntity living && !CAN_TARGET.apply(living)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWINGING_ARMS, Boolean.valueOf(false));
    }

    public boolean isSwingingArms() {
        return this.entityData.get(SWINGING_ARMS).booleanValue();
    }

    @Override
    public void setAggressive(boolean swingingArms) {
        this.entityData.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
    }

    @Override
    public void die(DamageSource cause) {
        if (!level.isClientSide && cause.getEntity() instanceof ServerPlayer) {
            IMana mana = cause.getEntity().getCapability(ManaProvider.MANA).orElse(null);
            if (!mana.isLocked()) {
                mana.replenish(getManaExp());
                Main.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) cause.getEntity()), new MessageMana(mana.getMana()));
            }
        }
        super.die(cause);
    }

    protected float getManaExp() {
        return Math.round(this.getMaxHealth() * 0.05f);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime == 20) {
            if (!this.level.isClientSide && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
                int i = this.getExperiencePoints(this.attackingPlayer);
                i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
                while (i > 0) {
                    int j = ExperienceOrb.getExperienceValue(i);
                    i -= j;
                    this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY(), this.getZ(), j));
                }
            }

            this.discard();

            level.broadcastEntityEvent(this, ModUtils.MAELSTROM_PARTICLE_BYTE);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.MAELSTROM_PARTICLE_BYTE) {
            for (int i = 0; i < 20; i++) {
                ParticleManager.spawnMaelstromLargeSmoke(level, this.random, this.position().add(ModRandom.gaussVec().scale(0.5f).add(ModUtils.yVec(1))));
            }
        }
        super.handleEntityEvent(id);
    }

    @Override
	public boolean removeWhenFarAway(double distance) {
        if (this.level.dimension() == ModDimensions.CRIMSON_KINGDOM_KEY || this.level.dimension() == ModDimensions.NEXUS_KEY) {
            // Allow despawn after about twenty minutes of being idle
            return this.tickCount > 20 * 60 * 20;
        }
        return true;
    }
}
