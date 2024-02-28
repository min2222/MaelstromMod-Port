package com.barribob.mm.entity.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.barribob.mm.entity.ai.AIFlyingRangedAttack;
import com.barribob.mm.entity.ai.AILookAround;
import com.barribob.mm.entity.ai.AIRandomFly;
import com.barribob.mm.entity.ai.FlyingMoveHelper;
import com.barribob.mm.entity.animation.AnimationClip;
import com.barribob.mm.entity.animation.StreamAnimation;
import com.barribob.mm.entity.model.ModelCliffFly;
import com.barribob.mm.entity.projectile.ProjectileSwampSpittle;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class EntityCliffFly extends EntityLeveledFlyingMob {
    public EntityCliffFly(Level worldIn) {
        super(worldIn);
        this.moveControl = new FlyingMoveHelper(this);
        this.setSize(1.0f, 1.8f);
        this.setMobLevel(LevelHandler.CLIFF_OVERWORLD);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new AIRandomFly(this));
        this.goalSelector.addGoal(7, new AILookAround(this));
        this.goalSelector.addGoal(7, new AIFlyingRangedAttack(this, 40, 20, 30, 1.0f));
        this.targetSelector.addTask(1, new EntityAIFindEntityNearestPlayer(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30.0D);
        this.getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.5);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        for (int i = 0; i < 5; i++) {
            ModUtils.throwProjectile(this, target, new ProjectileSwampSpittle(this.getLevel(), this, this.getAttack()));
            this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        }
    }

    @Override
    public void setAggressive(boolean swingingArms) {

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTITY_BEAST_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTITY_BEAST_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTITY_BEAST_HURT;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.FLY_WINGS;
    }

    @Override
    protected float getSoundVolume() {
        return 0.3f;
    }

    @Override
	public float getVoicePitch() {
        return super.getVoicePitch() * 1.5f;
    }

    @Override
    protected void initAnimation() {
        List<List<AnimationClip<ModelCliffFly>>> animationWings = new ArrayList<List<AnimationClip<ModelCliffFly>>>();
        List<AnimationClip<ModelCliffFly>> wings = new ArrayList<AnimationClip<ModelCliffFly>>();
        BiConsumer<ModelCliffFly, Float> wingsY = (model, f) -> {
            model.leftFrontWing.rotateAngleY = -f;
            model.leftFrontWing1.rotateAngleY = -f;
            model.rightFrontWing.rotateAngleY = f;
            model.rightFrontWing2.rotateAngleY = f;

            model.rightBackWing.rotateAngleY = -f;
            model.rightBackWing2.rotateAngleY = -f;
            model.leftBackWing.rotateAngleY = f;
            model.leftBackWing2.rotateAngleY = f;
        };

        wings.add(new AnimationClip(2, 0, 30, wingsY));
        wings.add(new AnimationClip(4, 30, -30, wingsY));
        wings.add(new AnimationClip(2, -30, 0, wingsY));

        animationWings.add(wings);

        currentAnimation = new StreamAnimation(animationWings).loop(true);
        this.currentAnimation.startAnimation();
    }
}
