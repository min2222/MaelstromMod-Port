package com.barribob.mm.entity.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.barribob.mm.entity.ai.AIJumpAtTarget;
import com.barribob.mm.entity.ai.EntityAITimedAttack;
import com.barribob.mm.entity.animation.AnimationClip;
import com.barribob.mm.entity.animation.StreamAnimation;
import com.barribob.mm.entity.model.ModelMaelstromLancer;
import com.barribob.mm.entity.util.IAttack;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMaelstromLancer extends EntityMaelstromMob implements IAttack {
    public EntityMaelstromLancer(Level worldIn) {
        super(worldIn);
        this.setSize(0.9f, 1.8f);
    }

    @Override
    protected void initAnimation() {
        List<List<AnimationClip<ModelMaelstromLancer>>> animationSpear = new ArrayList<List<AnimationClip<ModelMaelstromLancer>>>();
        List<AnimationClip<ModelMaelstromLancer>> rightArmXStream = new ArrayList<AnimationClip<ModelMaelstromLancer>>();
        List<AnimationClip<ModelMaelstromLancer>> leftArmZStream = new ArrayList<AnimationClip<ModelMaelstromLancer>>();
        List<AnimationClip<ModelMaelstromLancer>> bodyStream = new ArrayList<AnimationClip<ModelMaelstromLancer>>();
        List<AnimationClip<ModelMaelstromLancer>> spearStream = new ArrayList<AnimationClip<ModelMaelstromLancer>>();
        List<AnimationClip<ModelMaelstromLancer>> leftArmXStream = new ArrayList<AnimationClip<ModelMaelstromLancer>>();
        List<AnimationClip<ModelMaelstromLancer>> leftForearmXStream = new ArrayList<AnimationClip<ModelMaelstromLancer>>();

        BiConsumer<ModelMaelstromLancer, Float> rightArmX = (model, f) -> model.rightArm.rotateAngleX = f;
        BiConsumer<ModelMaelstromLancer, Float> leftArmZ = (model, f) -> model.leftArm.rotateAngleZ = f;
        BiConsumer<ModelMaelstromLancer, Float> bodyX = (model, f) -> model.body.rotateAngleX = f;
        BiConsumer<ModelMaelstromLancer, Float> spearX = (model, f) -> model.spear.rotateAngleX = -f;
        BiConsumer<ModelMaelstromLancer, Float> leftArmX = (model, f) -> model.leftArm.rotateAngleX = -f;
        BiConsumer<ModelMaelstromLancer, Float> leftForearmX = (model, f) -> model.leftForearm.rotateAngleX = f;

        leftArmXStream.add(new AnimationClip(6, 0, -65, leftArmX));
        leftArmXStream.add(new AnimationClip(4, -65, -65, leftArmX));
        leftArmXStream.add(new AnimationClip(5, -65, 80, leftArmX));
        leftArmXStream.add(new AnimationClip(10, 80, 80, leftArmX));
        leftArmXStream.add(new AnimationClip(5, 80, 0, leftArmX));

        leftArmZStream.add(new AnimationClip(10, 0, 0, leftArmZ));
        leftArmZStream.add(new AnimationClip(5, 0, -20, leftArmZ));
        leftArmZStream.add(new AnimationClip(10, -20, -20, leftArmZ));
        leftArmZStream.add(new AnimationClip(5, -20, 0, leftArmZ));

        bodyStream.add(new AnimationClip(6, 0, 0, bodyX));
        bodyStream.add(new AnimationClip(4, 0, -15, bodyX));
        bodyStream.add(new AnimationClip(5, -15, 30, bodyX));
        bodyStream.add(new AnimationClip(10, 30, 30, bodyX));
        bodyStream.add(new AnimationClip(5, 30, 0, bodyX));

        spearStream.add(new AnimationClip(10, 0, 0, spearX));
        spearStream.add(new AnimationClip(5, 0, -50, spearX));
        spearStream.add(new AnimationClip(10, -50, -50, spearX));
        spearStream.add(new AnimationClip(5, -50, 0, spearX));

        leftForearmXStream.add(new AnimationClip(10, -50, -50, leftForearmX));
        leftForearmXStream.add(new AnimationClip(5, -50, 0, leftForearmX));
        leftForearmXStream.add(new AnimationClip(10, 0, 0, leftForearmX));
        leftForearmXStream.add(new AnimationClip(5, 0, -50, leftForearmX));

        rightArmXStream.add(new AnimationClip(6, 0, -40, rightArmX));
        rightArmXStream.add(new AnimationClip(4, -40, -40, rightArmX));
        rightArmXStream.add(new AnimationClip(5, -40, 40, rightArmX));
        rightArmXStream.add(new AnimationClip(10, 40, 40, rightArmX));
        rightArmXStream.add(new AnimationClip(5, 40, 0, rightArmX));

        animationSpear.add(rightArmXStream);
        animationSpear.add(leftArmZStream);
        animationSpear.add(bodyStream);
        animationSpear.add(spearStream);
        animationSpear.add(leftArmXStream);
        animationSpear.add(leftForearmXStream);

        this.currentAnimation = new StreamAnimation<ModelMaelstromLancer>(animationSpear) {
            @Override
            public void setModelRotations(ModelMaelstromLancer model, float limbSwing, float limbSwingAmount, float partialTicks) {
                model.leftArm.offsetY = (float) Math.cos(Math.toRadians(tickCount * 4)) * 0.05f;
                model.rightArm.offsetY = (float) Math.cos(Math.toRadians(tickCount * 4)) * 0.05f;
                super.setModelRotations(model, limbSwing, limbSwingAmount, partialTicks);
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAITimedAttack<EntityMaelstromLancer>(this, 1.0f, 10, 5, 0.5f, 20.0f));
        this.goalSelector.addGoal(0, new AIJumpAtTarget(this, 0.4f, 0.5f));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTITY_SHADE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTITY_SHADE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTITY_SHADE_HURT.get();
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (random.nextInt(20) == 0) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            getCurrentAnimation().startAnimation();
        } else if (id == ModUtils.PARTICLE_BYTE) {
            if (this.getElement().equals(Element.NONE)) {
                ParticleManager.spawnMaelstromPotionParticle(level, random, this.position().add(ModRandom.randVec()).add(ModUtils.yVec(1)), false);
            }

            ParticleManager.spawnEffect(level, this.position().add(ModRandom.randVec()).add(ModUtils.yVec(1)), getElement().particleColor);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void aiStep() {
        if (!level.isClientSide && this.isLeaping()) {
            Vec3 dir = this.getLookAngle().scale(2.2);
            Vec3 pos = this.position().add(ModUtils.yVec(0.8f)).add(dir);
            ModUtils.handleAreaImpact(0.2f, (e) -> this.getAttack(), this, pos, ModDamageSource.causeElementalMeleeDamage(this, getElement()), 0.20f, 0, false);
        }
        super.aiStep();
    }

    @Override
    public int startAttack(LivingEntity target, float distanceSq, boolean strafingBackwards) {
        this.level.broadcastEntityEvent(this, (byte) 4);

        addEvent(() -> {
            ModUtils.leapTowards(this, target.position(), 0.9f, 0.3f);
            this.setLeaping(true);
            this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, ModRandom.getFloat(0.1f) + 1.2f);
        }, 10);

        return 40;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
    }
}
