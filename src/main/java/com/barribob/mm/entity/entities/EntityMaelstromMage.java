package com.barribob.mm.entity.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.barribob.mm.entity.ai.AIJumpAtTarget;
import com.barribob.mm.entity.ai.EntityAIRangedAttack;
import com.barribob.mm.entity.animation.AnimationClip;
import com.barribob.mm.entity.animation.StreamAnimation;
import com.barribob.mm.entity.model.ModelMaelstromMage;
import com.barribob.mm.entity.projectile.ProjectileHorrorAttack;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMaelstromMage extends EntityMaelstromMob {
    public static final float PROJECTILE_INACCURACY = 6.0f;
    public static final float PROJECTILE_SPEED = 1.2f;

    public EntityMaelstromMage(Level worldIn) {
        super(worldIn);
        this.setSize(0.9f, 1.8f);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAIRangedAttack<EntityMaelstromMob>(this, 1.0f, 50, 20, 15.0f, 0.5f));
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

    /**
     * Spawn summoning particles
     */
    @Override
    public void tick() {
        super.tick();

        if (this.level.isClientSide && this.isSwingingArms()) {
            this.prepareShoot();
        }
    }

    protected void prepareShoot() {
        float f = ModRandom.getFloat(0.25f);

        if (getElement() != Element.NONE) {
            ParticleManager.spawnEffect(level, new Vec3(this.getX() + f, this.getY() + this.getEyeHeight() + 1.0f, this.getZ() + f), getElement().particleColor);
        } else {
            ParticleManager.spawnMaelstromPotionParticle(level, random, new Vec3(this.getX() + f, this.getY() + this.getEyeHeight() + 1.0f, this.getZ() + f), true);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (random.nextInt(20) == 0) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }
    }

    @Override
    public void setAggressive(boolean swingingArms) {
        super.setAggressive(swingingArms);
        if (swingingArms) {
            this.level.broadcastEntityEvent(this, (byte) 4);
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

    /**
     * Shoots a projectile in a similar fashion to the snow golem (see
     * EntitySnowman)
     */
    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!level.isClientSide) {
            ProjectileHorrorAttack projectile = new ProjectileHorrorAttack(this.level, this, this.getAttack());
            projectile.posY = this.posY + this.getEyeHeight() + 1.0f; // Raise pos y to summon the projectile above the head
            double d0 = target.posY + target.getEyeHeight() - 0.9f;
            double d1 = target.posX - this.posX;
            double d2 = d0 - projectile.posY;
            double d3 = target.posZ - this.posZ;
            float f = (float) (Math.sqrt(d1 * d1 + d3 * d3) * 0.2F);
            projectile.shoot(d1, d2 + f, d3, EntityMaelstromMage.PROJECTILE_SPEED, EntityMaelstromMage.PROJECTILE_INACCURACY);
            this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level.addFreshEntity(projectile);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
        List<List<AnimationClip<ModelMaelstromMage>>> animation = new ArrayList<List<AnimationClip<ModelMaelstromMage>>>();
        List<AnimationClip<ModelMaelstromMage>> leftArmXStream = new ArrayList<AnimationClip<ModelMaelstromMage>>();
        List<AnimationClip<ModelMaelstromMage>> leftArmZStream = new ArrayList<AnimationClip<ModelMaelstromMage>>();
        List<AnimationClip<ModelMaelstromMage>> leftForearmXStream = new ArrayList<AnimationClip<ModelMaelstromMage>>();
        List<AnimationClip<ModelMaelstromMage>> bodyXStream = new ArrayList<AnimationClip<ModelMaelstromMage>>();
        List<AnimationClip<ModelMaelstromMage>> rightArmXStream = new ArrayList<AnimationClip<ModelMaelstromMage>>();

        BiConsumer<ModelMaelstromMage, Float> leftArmX = (model, f) -> model.leftArm.rotateAngleX = f;
        BiConsumer<ModelMaelstromMage, Float> leftArmZ = (model, f) -> model.leftArm.rotateAngleZ = f;
        BiConsumer<ModelMaelstromMage, Float> leftForearmX = (model, f) -> model.leftForearm.rotateAngleX = f;
        BiConsumer<ModelMaelstromMage, Float> bodyX = (model, f) -> model.body.rotateAngleX = f;
        BiConsumer<ModelMaelstromMage, Float> rightArmX = (model, f) -> model.rightArm.rotateAngleX = f;

        leftForearmXStream.add(new AnimationClip(10, -40, 0, leftForearmX));
        leftForearmXStream.add(new AnimationClip(12, 0, 0, leftForearmX));
        leftForearmXStream.add(new AnimationClip(6, 0, 0, leftForearmX));
        leftForearmXStream.add(new AnimationClip(6, 0, -40, leftForearmX));

        leftArmXStream.add(new AnimationClip(10, 0, -120, leftArmX));
        leftArmXStream.add(new AnimationClip(12, -120, -120, leftArmX));
        leftArmXStream.add(new AnimationClip(4, -120, 60, leftArmX));
        leftArmXStream.add(new AnimationClip(2, 60, 60, leftArmX));
        leftArmXStream.add(new AnimationClip(6, 60, 0, leftArmX));

        leftArmZStream.add(new AnimationClip(10, 0, -25, leftArmZ));
        leftArmZStream.add(new AnimationClip(12, -25, -25, leftArmZ));
        leftArmZStream.add(new AnimationClip(6, -25, -20, leftArmZ));
        leftArmZStream.add(new AnimationClip(6, -25, 0, leftArmZ));

        bodyXStream.add(new AnimationClip(10, 0, -15, bodyX));
        bodyXStream.add(new AnimationClip(14, -15, -15, bodyX));
        bodyXStream.add(new AnimationClip(6, -15, 15, bodyX));
        bodyXStream.add(new AnimationClip(4, 15, 0, bodyX));

        rightArmXStream.add(new AnimationClip(10, 0, -40, rightArmX));
        rightArmXStream.add(new AnimationClip(12, -40, -40, rightArmX));
        rightArmXStream.add(new AnimationClip(6, -40, 40, rightArmX));
        rightArmXStream.add(new AnimationClip(6, 40, 0, rightArmX));

        animation.add(leftArmXStream);
        animation.add(leftArmZStream);
        animation.add(leftForearmXStream);
        animation.add(bodyXStream);
        animation.add(rightArmXStream);

        this.currentAnimation = new StreamAnimation<ModelMaelstromMage>(animation) {
            @Override
            public void setModelRotations(ModelMaelstromMage model, float limbSwing, float limbSwingAmount, float partialTicks) {
                model.leftArm.offsetY = (float) Math.cos(Math.toRadians(tickCount * 4)) * 0.05f;
                model.rightArm.offsetY = (float) Math.cos(Math.toRadians(tickCount * 4)) * 0.05f;
                super.setModelRotations(model, limbSwing, limbSwingAmount, partialTicks);
            }
        };
    }
}