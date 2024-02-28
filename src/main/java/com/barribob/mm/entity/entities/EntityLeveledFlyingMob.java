package com.barribob.mm.entity.entities;

import com.barribob.mm.entity.animation.Animation;
import com.barribob.mm.entity.animation.AnimationNone;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IAnimatedMob;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/*
 * Base class that serves as the flying version of EntityLeveledMob
 */
public abstract class EntityLeveledFlyingMob extends FlyingMob implements Enemy, RangedAttackMob, IAnimatedMob, IElement {
    @OnlyIn(Dist.CLIENT)
    protected Animation currentAnimation;
    private float level;

    protected static final EntityDataAccessor<Integer> ELEMENT = SynchedEntityData.<Integer>createKey(EntityLeveledMob.class, EntityDataSerializers.INT);

    public EntityLeveledFlyingMob(Level worldIn) {
        super(worldIn);
    }

    public float getMobLevel() {
        return level;
    }

    public void setMobLevel(float level) {
        this.level = level;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.getLevel().isClientSide && currentAnimation != null && this.getHealth() > 0) {
            currentAnimation.update();
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getLevel().isClientSide && this.getLevel().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }

        /**
         * Periodically check if the animations need to be reinitialized
         */
        if (this.tickCount % 20 == 0) {
        	getLevel().broadcastEntityEvent(this, animationByte);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
    	getLevel().broadcastEntityEvent(this, animationByte);
        if (compound.contains("element")) {
            this.setElement(Element.getElementFromId(compound.getInt("element")));
        }

        super.readAdditionalSaveData(compound);
    }

    public float getAttack() {
        return ModUtils.getMobDamage(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(), 0.0, this.getMaxHealth(), this.getHealth(),
                this.level, this.getElement());
    }

    @Override
    public Animation getCurrentAnimation() {
        return this.currentAnimation == null ? new AnimationNone() : this.currentAnimation;
    }

    @Override
    protected float applyArmorCalculations(DamageSource source, float damage) {
        return super.applyArmorCalculations(source, ModUtils.getArmoredDamage(source, damage, getMobLevel(), getElement()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ELEMENT, Integer.valueOf(Element.NONE.id));
    }

    @Override
    public Element getElement() {
        return Element.getElementFromId(this.entityData.get(ELEMENT));
    }

    public void setElement(Element element) {
        this.entityData.set(ELEMENT, element.id);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("element", getElement().id);
        super.addAdditionalSaveData(compound);
    }
}
