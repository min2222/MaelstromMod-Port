package com.barribob.MaelstromMod.entity.entities;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.config.ModConfig;
import com.barribob.MaelstromMod.entity.ai.ModGroundNavigator;
import com.barribob.MaelstromMod.entity.animation.Animation;
import com.barribob.MaelstromMod.entity.animation.AnimationNone;
import com.barribob.MaelstromMod.entity.util.LeapingEntity;
import com.barribob.MaelstromMod.util.*;
import com.barribob.MaelstromMod.util.handlers.LevelHandler;
import com.barribob.MaelstromMod.util.handlers.SoundsHandler;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityCreature;
import net.minecraft.world.entity.MoverType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.PriorityQueue;

/**
 * A base class for the mod's mobs. It includes a hodgepodge of attributes and abilities. One is to scale nicely with the leveling system.
 */
public abstract class EntityLeveledMob extends EntityCreature implements IAnimatedMob, IElement, LeapingEntity {
    protected static final EntityDataAccessor<Float> LEVEL = SynchedEntityData.<Float>createKey(EntityLeveledMob.class, EntityDataSerializers.FLOAT);
    private float regenStartTimer;
    private static float regenStartTime = 60;
    protected static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.<Integer>createKey(EntityLeveledMob.class, EntityDataSerializers.VARINT);

    @OnlyIn(Dist.CLIENT)
    protected Animation currentAnimation;

    protected static final EntityDataAccessor<Boolean> IMMOVABLE = SynchedEntityData.<Boolean>createKey(EntityLeveledMob.class, EntityDataSerializers.BOOLEAN);
    private Vec3 initialPosition = null;
    protected double healthScaledAttackFactor = 0.0; // Factor that determines how much attack is affected by health
    private PriorityQueue<TimedEvent> events = new PriorityQueue<TimedEvent>();
    private boolean leaping = false;

    public EntityLeveledMob(Level worldIn) {
        super(worldIn);
        this.setLevel(LevelHandler.INVASION);
        this.experienceValue = 5;
        if(getMobConfig().hasPath("nbt_spawn_data")) {
            this.readFromNBT(ModUtils.parseNBTFromConfig(getMobConfig().getConfig("nbt_spawn_data")));
        }
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
    protected PathNavigate createNavigator(Level worldIn) {
        if (ModConfig.entities.useVanillaPathfinding) {
            return super.createNavigator(worldIn);
        }
        return new ModGroundNavigator(this, worldIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == animationByte && currentAnimation == null) {
            initAnimation();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {
        if(!this.isImmovable()) {
            super.move(type, x, y, z);
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!isDead && this.getHealth() > 0) {
            boolean foundEvent = true;
            while (foundEvent) {
                TimedEvent event = events.peek();
                if (event != null && event.ticks <= this.ticksExisted) {
                    events.remove();
                    event.callback.run();
                } else {
                    foundEvent = false;
                }
            }
        }

        if (world.isRemote && currentAnimation != null && this.getHealth() > 0) {
            currentAnimation.update();
        }

        if (!world.isRemote) {
            if (this.getAttackTarget() == null) {
                if (this.regenStartTimer > this.regenStartTime) {
                    if (this.ticksExisted % 20 == 0) {
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
        if (this.ticksExisted % 20 == 1) {
            world.setEntityState(this, animationByte);
        }

        if (this.isImmovable() && this.initialPosition != null) {
            this.setPosition(initialPosition.x, initialPosition.y, initialPosition.z);
        }
    }

    protected boolean isImmovable() {
        return this.dataManager == null ? false : this.dataManager.get(IMMOVABLE);
    }

    protected void setImmovable(boolean immovable) {
        this.dataManager.set(IMMOVABLE, immovable);
    }

    public void setImmovablePosition(Vec3 pos) {
        this.initialPosition = pos;
        this.setPosition(0, 0, 0);
    }

    // Hold the entity in the same position
    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
        if (this.isImmovable()) {
            if (this.initialPosition == null) {
                this.initialPosition = ModUtils.entityPos(this);
            } else {
                super.setPosition(initialPosition.x, initialPosition.y, initialPosition.z);
            }
        }
    }

    @Override
    public Animation getCurrentAnimation() {
        return this.currentAnimation == null ? new AnimationNone() : this.currentAnimation;
    }

    public float getLevel() {
        return this.dataManager == null ? 0 : this.dataManager.get(LEVEL);
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
        this.dataManager.set(LEVEL, level);
        return this;
    }

    @Override
    public void writeEntityToNBT(CompoundTag compound) {
        compound.setFloat("level", getLevel());
        compound.setBoolean("isImmovable", this.isImmovable());
        compound.setInteger("element", getElement().id);
        compound.setInteger("experienceValue", this.experienceValue);
        if (initialPosition != null) {
            compound.setDouble("initialX", initialPosition.x);
            compound.setDouble("initialY", initialPosition.y);
            compound.setDouble("initialZ", initialPosition.z);
        }
        super.writeEntityToNBT(compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        if (compound.hasKey("level")) {
            this.setLevel(compound.getFloat("level"));
        }
        if (compound.hasKey("element")) {
            this.setElement(Element.getElementFromId(compound.getInteger("element")));
        }
        world.setEntityState(this, animationByte);

        super.readFromNBT(compound);

        if(compound.hasKey("experienceValue")) {
            this.experienceValue = compound.getInteger("experienceValue");
        }
        if (compound.hasKey("isImmovable")) {
            this.setImmovable(compound.getBoolean("isImmovable"));
        }

        // This is required because the position gets set at 0 0 0 from super.readFromNBT, which causes problems
        this.initialPosition = null;
        if (compound.hasKey("initialX")) {
            this.initialPosition = new Vec3(compound.getDouble("initialX"), compound.getDouble("initialY"), compound.getDouble("initialZ"));
        }
    }

    /**
     * Return the shared monster attribute attack
     */
    public float getAttack() {
        return ModUtils.getMobDamage(this.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue(), healthScaledAttackFactor, this.getMaxHealth(),
                this.getHealth(), this.getLevel(), this.getElement());
    }

    @Override
    protected float applyArmorCalculations(DamageSource source, float damage) {
        return super.applyArmorCalculations(source, ModUtils.getArmoredDamage(source, damage, getLevel(), getElement()));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(LEVEL, Float.valueOf(0.0f));
        this.dataManager.register(IMMOVABLE, Boolean.valueOf(false));
        this.dataManager.register(ELEMENT, Integer.valueOf(Element.NONE.id));
    }

    @Override
    public Element getElement() {
        return this.dataManager == null ? Element.getElementFromId(Element.NONE.id) : Element.getElementFromId(this.dataManager.get(ELEMENT));
    }

    public EntityLeveledMob setElement(Element element) {
        this.dataManager.set(ELEMENT, element.id);
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
        events.add(new TimedEvent(runnable, this.ticksExisted + ticksFromNow));
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
        playSoundWithFallback(sound, SoundsHandler.NONE);
    }

    @Override
    public boolean isBeingRidden() {
        return super.isBeingRidden() || (getMobConfig().hasPath("can_be_pushed") && !getMobConfig().getBoolean("can_be_pushed"));
    }
}
