package com.barribob.mm.entity.entities;

import java.util.PriorityQueue;

import com.barribob.mm.Main;
import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.animation.Animation;
import com.barribob.mm.entity.animation.AnimationNone;
import com.barribob.mm.entity.util.LeapingEntity;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IAnimatedMob;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.util.handlers.SoundsHandler;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A base class for the mod's mobs. It includes a hodgepodge of attributes and abilities. One is to scale nicely with the leveling system.
 */
public abstract class EntityLeveledMob extends PathfinderMob implements IAnimatedMob, IElement, LeapingEntity {
    protected static final EntityDataAccessor<Float> LEVEL = SynchedEntityData.<Float>defineId(EntityLeveledMob.class, EntityDataSerializers.FLOAT);
    private float regenStartTimer;
    private static float regenStartTime = 60;
    protected static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.<Integer>createKey(EntityLeveledMob.class, EntityDataSerializers.VARINT);

    @OnlyIn(Dist.CLIENT)
    protected Animation currentAnimation;

    protected static final EntityDataAccessor<Boolean> IMMOVABLE = SynchedEntityData.<Boolean>defineId(EntityLeveledMob.class, EntityDataSerializers.BOOLEAN);
    private Vec3 initialPosition = null;
    protected double healthScaledAttackFactor = 0.0; // Factor that determines how much attack is affected by health
    private PriorityQueue<TimedEvent> events = new PriorityQueue<TimedEvent>();
    private boolean leaping = false;

    public EntityLeveledMob(Level worldIn) {
        super(worldIn);
        this.setLevel(LevelHandler.INVASION);
        this.xpReward = 5;
        if(getMobConfig().hasPath("nbt_spawn_data")) {
            this.readFromNBT(ModUtils.parseNBTFromConfig(getMobConfig().getConfig("nbt_spawn_data")));
        }
    }
    
    public float getRenderSizeModifier() {
    	return 1.0F;
    }

    public Config getMobConfig() {
        EntityEntry entry = EntityRegistry.getEntry(this.getClass());
        if(entry != null) {
            String entityName = entry.getName();
            if (Main.mobsConfig.hasPath(entityName)) {
                return Main.mobsConfig.getConfig(entityName);
            }
        }
        return ConfigFactory.empty();
    }

    public float getConfigFloat(String path) {
        return (float) getMobConfig().getDouble(path);
    }

    // Because for some reason the default entity ai for 1.12 sends entities
    // off cliffs and holes instead of going around them
    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        if (ModConfig.entities.useVanillaPathfinding) {
            return super.createNavigation(worldIn);
        }
        return super.createNavigation(worldIn);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == animationByte && currentAnimation == null) {
            initAnimation();
        } else {
            super.handleEntityEvent(id);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
    }

    @Override
    public void move(MoverType type, Vec3 vec) {
        if(!this.isImmovable()) {
            super.move(type, vec);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!isDeadOrDying() && this.getHealth() > 0) {
            boolean foundEvent = true;
            while (foundEvent) {
                TimedEvent event = events.peek();
                if (event != null && event.ticks <= this.tickCount) {
                    events.remove();
                    event.callback.run();
                } else {
                    foundEvent = false;
                }
            }
        }

        if (level.isClientSide && currentAnimation != null && this.getHealth() > 0) {
            currentAnimation.update();
        }

        if (!level.isClientSide) {
            if (this.getTarget() == null) {
                if (this.regenStartTimer > EntityLeveledMob.regenStartTime) {
                    if (this.tickCount % 20 == 0) {
                        this.heal(this.getMaxHealth() * 0.015f);
                    }
                } else {
                    this.regenStartTimer++;
                }
            } else {
                this.regenStartTimer = 0;
            }
        }

        /**
         * Periodically check if the animations need to be reinitialized
         */
        if (this.tickCount % 20 == 1) {
            level.broadcastEntityEvent(this, animationByte);
        }

        if (this.isImmovable() && this.initialPosition != null) {
            this.setPos(initialPosition.x, initialPosition.y, initialPosition.z);
        }
    }

    protected boolean isImmovable() {
        return this.entityData.get(IMMOVABLE);
    }

    protected void setImmovable(boolean immovable) {
        this.entityData.set(IMMOVABLE, immovable);
    }

    public void setImmovablePosition(Vec3 pos) {
        this.initialPosition = pos;
        this.setPos(0, 0, 0);
    }

    // Hold the entity in the same position
    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (this.isImmovable()) {
            if (this.initialPosition == null) {
                this.initialPosition = ModUtils.entityPos(this);
            } else {
                super.setPos(initialPosition.x, initialPosition.y, initialPosition.z);
            }
        }
    }

    @Override
    public Animation getCurrentAnimation() {
        return this.currentAnimation == null ? new AnimationNone() : this.currentAnimation;
    }

    public float getMobLevel() {
        return this.entityData.get(LEVEL);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE);
        this.getAttributeMap().registerAttribute(Attributes.FLYING_SPEED);
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
        this.getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0);
        this.getEntityAttribute(Attributes.FLYING_SPEED).setBaseValue(0);
    }

    /**
     * Sets the level, updates attributes, and set health to the updated max health
     */
    public EntityLeveledMob setLevel(float level) {
        this.entityData.set(LEVEL, level);
        return this;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putFloat("level", getMobLevel());
        compound.putBoolean("isImmovable", this.isImmovable());
        compound.putInt("element", getElement().id);
        compound.putInt("experienceValue", this.xpReward);
        if (initialPosition != null) {
            compound.putDouble("initialX", initialPosition.x);
            compound.putDouble("initialY", initialPosition.y);
            compound.putDouble("initialZ", initialPosition.z);
        }
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("level")) {
            this.setLevel(compound.getFloat("level"));
        }
        if (compound.contains("element")) {
            this.setElement(Element.getElementFromId(compound.getInt("element")));
        }
        level.broadcastEntityEvent(this, animationByte);

        super.readAdditionalSaveData(compound);

        if(compound.contains("experienceValue")) {
            this.xpReward = compound.getInt("experienceValue");
        }
        if (compound.contains("isImmovable")) {
            this.setImmovable(compound.getBoolean("isImmovable"));
        }

        // This is required because the position gets set at 0 0 0 from super.readFromNBT, which causes problems
        this.initialPosition = null;
        if (compound.contains("initialX")) {
            this.initialPosition = new Vec3(compound.getDouble("initialX"), compound.getDouble("initialY"), compound.getDouble("initialZ"));
        }
    }

    /**
     * Return the shared monster attribute attack
     */
    public float getAttack() {
        return ModUtils.getMobDamage(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(), healthScaledAttackFactor, this.getMaxHealth(),
                this.getHealth(), this.getMobLevel(), this.getElement());
    }

    @Override
    protected float applyArmorCalculations(DamageSource source, float damage) {
        return super.applyArmorCalculations(source, ModUtils.getArmoredDamage(source, damage, getMobLevel(), getElement()));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LEVEL, Float.valueOf(0.0f));
        this.entityData.define(IMMOVABLE, Boolean.valueOf(false));
        this.entityData.define(ELEMENT, Integer.valueOf(Element.NONE.id));
    }

    @Override
    public Element getElement() {
        return Element.getElementFromId(this.entityData.get(ELEMENT));
    }

    public EntityLeveledMob setElement(Element element) {
        this.entityData.set(ELEMENT, element.id);
        return this;
    }

    public void doRender(RenderManager renderManager, double x, double y, double z, float entityYaw, float partialTicks) {
    }

    /**
     * Adds an event to be executed at a later time. Negative ticks are executed immediately.
     *
     * @param runnable
     * @param ticksFromNow
     */
    public void addEvent(Runnable runnable, int ticksFromNow) {
        events.add(new TimedEvent(runnable, this.tickCount + ticksFromNow));
    }

    private static class TimedEvent implements Comparable<TimedEvent> {
        Runnable callback;
        int ticks;

        public TimedEvent(Runnable callback, int ticks) {
            this.callback = callback;
            this.ticks = ticks;
        }

        @Override
        public int compareTo(TimedEvent event) {
            return event.ticks < ticks ? 1 : -1;
        }
    }

    @Override
    public boolean isLeaping() {
        return leaping;
    }

    @Override
    public void setLeaping(boolean leaping) {
        this.leaping = leaping;
    }

    protected Vec3 getInitialPosition() {
        return initialPosition;
    }

    @Override
    public void onStopLeaping() {
    }

    public void playSoundWithFallback(SoundEvent sound, SoundEvent fallback, float volume, float pitch) {
        playSound(ModUtils.getConfiguredSound(sound, fallback), volume, pitch);
    }

    public void playSoundWithFallback(SoundEvent sound, SoundEvent fallback, float volume) {
        playSound(ModUtils.getConfiguredSound(sound, fallback), volume, 1.0f + ModRandom.getFloat(0.2f));
    }

    public void playSoundWithFallback(SoundEvent sound, SoundEvent fallback) {
        playSoundWithFallback(sound, fallback, 1.0f);
    }

    public void playSoundWithFallback(SoundEvent sound) {
        playSoundWithFallback(sound, SoundsHandler.NONE.get());
    }

    @Override
    public boolean isBeingRidden() {
        return super.isBeingRidden() || (getMobConfig().hasPath("can_be_pushed") && !getMobConfig().getBoolean("can_be_pushed"));
    }
}
