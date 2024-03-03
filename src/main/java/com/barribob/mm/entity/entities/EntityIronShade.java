package com.barribob.mm.entity.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.barribob.mm.entity.action.ActionSpinSlash;
import com.barribob.mm.entity.action.IAction;
import com.barribob.mm.entity.ai.AIJumpAtTarget;
import com.barribob.mm.entity.ai.EntityAIRangedAttack;
import com.barribob.mm.entity.animation.AnimationClip;
import com.barribob.mm.entity.animation.StreamAnimation;
import com.barribob.mm.entity.model.ModelIronShade;
import com.barribob.mm.entity.util.ComboAttack;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LootTableHandler;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityIronShade extends EntityMaelstromMob {
    private ComboAttack attackHandler = new ComboAttack();
    private byte frontFlip = 4;
    private byte spin = 5;
    private int spinDuration = 30;
    private int maxSpinDuration = 30;
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.NOTCHED_6));

    public EntityIronShade(Level worldIn) {
        super(worldIn);
        this.healthScaledAttackFactor = 0.2;
        this.setSize(0.9f, 2.2f);
        if (!worldIn.isClientSide) {
            attackHandler.setAttack(frontFlip, (IAction) (actor, target) -> {
                DamageSource source = ModDamageSource.builder()
                        .directEntity(this)
                        .element(getElement())
                        .type(ModDamageSource.MOB)
                        .disablesShields().build();

                Vec3 pos = this.position().add(ModUtils.yVec(1)).add(this.getLookAngle().scale(2.0f));
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.8F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                ModUtils.handleAreaImpact(1.0f, (e) -> this.getAttack() * getConfigFloat("flip_damage"), this, pos, source, 0.20f, this.getElement() == Element.CRIMSON ? 3 : 0, false);
                actor.level.broadcastEntityEvent(actor, ModUtils.SECOND_PARTICLE_BYTE);
            });
            attackHandler.setAttack(spin, new ActionSpinSlash(3.0f));
        }
    }

    @Override
    public void tick() {
        bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        if (!level.isClientSide && spinDuration < maxSpinDuration && attackHandler.getCurrentAttack() == spin) {
            spinDuration++;
            if (this.tickCount % 5 == 0) {
                attackHandler.getCurrentAttackAction().performAction(this, null);
            }
        } else if (!level.isClientSide && !this.isSwingingArms()) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }
        super.tick();
    }

    @Override
    protected void initAnimation() {
        List<List<AnimationClip<ModelIronShade>>> flipAnimations = new ArrayList<List<AnimationClip<ModelIronShade>>>();
        List<AnimationClip<ModelIronShade>> wisps = new ArrayList<AnimationClip<ModelIronShade>>();
        List<AnimationClip<ModelIronShade>> arms = new ArrayList<AnimationClip<ModelIronShade>>();
        List<AnimationClip<ModelIronShade>> body = new ArrayList<AnimationClip<ModelIronShade>>();
        List<AnimationClip<ModelIronShade>> lowerChains = new ArrayList<AnimationClip<ModelIronShade>>();
        List<AnimationClip<ModelIronShade>> upperChains = new ArrayList<AnimationClip<ModelIronShade>>();

        BiConsumer<ModelIronShade, Float> bodyFlipper = (model, f) -> {
            model.wisps.rotateAngleX = f;
            model.wisps.rotateAngleY = 0;
        };
        BiConsumer<ModelIronShade, Float> bodyBender = (model, f) -> {
            model.body.rotateAngleX = f;
        };
        BiConsumer<ModelIronShade, Float> armMover = (model, f) -> {
            model.rightArm.rotateAngleX = f;
            model.leftArm.rotateAngleX = f;
        };
        BiConsumer<ModelIronShade, Float> lowerChainMover = (model, f) -> {
            model.chainLink1.rotateAngleX = f / 8;
            model.chainLink2.rotateAngleX = f / 4;
            model.chainLink3.rotateAngleX = f / 2;
            model.chainLink4.rotateAngleX = f;
            model.chainLink1_1.rotateAngleX = f / 8;
            model.chainLink2_1.rotateAngleX = f / 4;
            model.chainLink3_1.rotateAngleX = f / 2;
            model.chainLink4_1.rotateAngleX = f;
        };
        BiConsumer<ModelIronShade, Float> ballAndChainMover = (model, f) -> {
            model.chainLink5.rotateAngleX = f;
            model.chainLink6.rotateAngleX = f;
            model.ball.rotateAngleX = f;
            model.chainLink5_1.rotateAngleX = f;
            model.chainLink6_1.rotateAngleX = f;
            model.ball_1.rotateAngleX = f;
        };

        int flipLength = 10;
        int swingDownLength = 4;
        int stopLength = 8;
        int resetLength = 8;

        wisps.add(new AnimationClip(flipLength, 0, 360, bodyFlipper));
        wisps.add(new AnimationClip(swingDownLength + stopLength + resetLength, 360, 360, bodyFlipper));

        body.add(new AnimationClip(flipLength, 0, 0, bodyBender));
        body.add(new AnimationClip(swingDownLength, 0, 50, bodyBender));
        body.add(new AnimationClip(stopLength, 50, 50, bodyBender));
        body.add(new AnimationClip(resetLength, 50, 0, bodyBender));

        arms.add(new AnimationClip(flipLength - 4, 0, -210, armMover));
        arms.add(new AnimationClip(4, -210, -210, armMover));
        arms.add(new AnimationClip(swingDownLength, -210, -90, armMover));
        arms.add(new AnimationClip(stopLength, -90, -90, armMover));
        arms.add(new AnimationClip(resetLength, -90, 0, armMover));

        lowerChains.add(new AnimationClip(flipLength, 48, -16, lowerChainMover));
        lowerChains.add(new AnimationClip(swingDownLength + stopLength, -16, -16, lowerChainMover));
        lowerChains.add(new AnimationClip(resetLength, -16, 48, lowerChainMover));

        upperChains.add(new AnimationClip(flipLength, 0, -32, ballAndChainMover));
        upperChains.add(new AnimationClip(swingDownLength, -32, -32, ballAndChainMover));
        upperChains.add(new AnimationClip(stopLength - 6, -32, -10, ballAndChainMover));
        upperChains.add(new AnimationClip(6, -10, -10, ballAndChainMover));
        upperChains.add(new AnimationClip(resetLength, -10, 0, ballAndChainMover));

        flipAnimations.add(wisps);
        flipAnimations.add(arms);
        flipAnimations.add(body);
        flipAnimations.add(lowerChains);
        flipAnimations.add(upperChains);
        attackHandler.setAttack(frontFlip, IAction.NONE, () -> new StreamAnimation(flipAnimations));

        List<List<AnimationClip<ModelIronShade>>> spinAnimations = new ArrayList<List<AnimationClip<ModelIronShade>>>();
        wisps = new ArrayList<AnimationClip<ModelIronShade>>();
        arms = new ArrayList<AnimationClip<ModelIronShade>>();
        body = new ArrayList<AnimationClip<ModelIronShade>>();
        lowerChains = new ArrayList<AnimationClip<ModelIronShade>>();
        upperChains = new ArrayList<AnimationClip<ModelIronShade>>();

        BiConsumer<ModelIronShade, Float> bodySpinner = (model, f) -> {
            model.wisps.rotateAngleX = 0;
            model.wisps.rotateAngleY = f;
        };

        int chargeLength = 5;
        int speedUpLength = 5;
        int speedUpFasterLength = 5;

        wisps.add(new AnimationClip(chargeLength, 0, -60, bodySpinner));
        wisps.add(new AnimationClip(speedUpLength, 0, 180, bodySpinner));
        wisps.add(new AnimationClip(speedUpFasterLength, 180, 540, bodySpinner));
        wisps.add(new AnimationClip(maxSpinDuration, 540, 3240, bodySpinner));
        wisps.add(new AnimationClip(speedUpFasterLength, 3240, 3240 + 540, bodySpinner));
        wisps.add(new AnimationClip(10, 3240 + 540, 3240 + 540 + 360, bodySpinner));

        body.add(new AnimationClip(15, 0, -20, bodyBender));
        body.add(new AnimationClip(maxSpinDuration, -20, -20, bodyBender));
        body.add(new AnimationClip(15, -20, 0, bodyBender));

        arms.add(new AnimationClip(15, 0, -70, armMover));
        arms.add(new AnimationClip(maxSpinDuration, -70, -70, armMover));
        arms.add(new AnimationClip(15, -70, 0, armMover));

        lowerChains.add(new AnimationClip(15, -48, 0, lowerChainMover));
        lowerChains.add(new AnimationClip(30, 0, 0, lowerChainMover));
        lowerChains.add(new AnimationClip(15, 0, -48, lowerChainMover));

        upperChains.add(new AnimationClip(60, 0, 0, ballAndChainMover));

        spinAnimations.add(wisps);
        spinAnimations.add(arms);
        spinAnimations.add(body);
        spinAnimations.add(lowerChains);
        spinAnimations.add(upperChains);
        attackHandler.setAttack(spin, new ActionSpinSlash(3.0f), () -> new StreamAnimation(spinAnimations));
        this.currentAnimation = new StreamAnimation(flipAnimations);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAIRangedAttack<EntityMaelstromMob>(this, 1.3f, 60, 10, 4.5f, 0.4f));
        this.goalSelector.addGoal(0, new AIJumpAtTarget(this, 0.4f, 0.5f));
    }

    @Override
    public void setAggressive(boolean swingingArms) {
        super.setAggressive(swingingArms);
        if (swingingArms) {
            Byte[] attack = {spin, frontFlip};
            attackHandler.setCurrentAttack(ModRandom.choice(attack));
            level.broadcastEntityEvent(this, attackHandler.getCurrentAttack());

            if (attackHandler.getCurrentAttack() == frontFlip) {
                ModUtils.leapTowards(this, this.getTarget().position(), 0.2f, 0.5f);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        // We want a special black flame for the non-elemental shade, and red flames for the crimson element.
        Vec3 flameColor = getElement() == Element.NONE ? new Vec3(0.1f, 0, 0.1f) : getElement().sweepColor;
        if (id >= 4 && id <= 6) {
            currentAnimation = attackHandler.getAnimation(id);
            getCurrentAnimation().startAnimation();
        } else if (id == EntityHerobrineOne.slashParticleByte) {
            ModUtils.performNTimes(4, (i) -> {
                ModUtils.circleCallback(i, 15, (pos) -> {
                    ParticleManager.spawnColoredFire(level, random, position().add(new Vec3(pos.x, this.getEyeHeight() - 0.3f + ModRandom.getFloat(0.2f), pos.y)), flameColor);
                });
            });
        } else if (id == ModUtils.PARTICLE_BYTE) {
            Vec3 look = this.getVectorForRotation(this.getXRot(), this.yHeadRot);
            Vec3 side = look.yRot((float) Math.PI * -0.5f);
            Vec3 offset = position().add(side.scale(0.5f * ModRandom.randSign())).add(ModUtils.yVec(random.nextFloat()));
            ParticleManager.spawnColoredFire(level, random, offset, flameColor);
            offset = position().add(side.scale(0.5f * ModRandom.randSign())).add(look.scale(-random.nextFloat())).add(ModUtils.yVec(0.1f));
            ParticleManager.spawnColoredFire(level, random, offset, flameColor);
        } else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            Vec3 pos = this.position().add(ModUtils.yVec(1)).add(this.getLookAngle().scale(2.0f));
            for (int i = 0; i < 30; i++) {
                ParticleManager.spawnColoredFire(level, random, pos.add(ModRandom.randVec().add(ModUtils.yVec(ModRandom.getFloat(1.5f)))), flameColor);
            }
        } else {
            super.handleEntityEvent(id);
        }
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
    protected ResourceLocation getDefaultLootTable() {
        if (getElement() == Element.CRIMSON) {
            return LootTableHandler.CRIMSON_MINIBOSS;
        }

        return LootTableHandler.IRON_SHADE;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (attackHandler.getCurrentAttack() == spin) {
            spinDuration = 0;
        } else {
            attackHandler.getCurrentAttackAction().performAction(this, target);
        }
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }
}
