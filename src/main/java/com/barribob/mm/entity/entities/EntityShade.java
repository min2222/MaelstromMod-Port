package com.barribob.mm.entity.entities;

import com.barribob.mm.entity.ai.AIJumpAtTarget;
import com.barribob.mm.entity.ai.EntityAITimedAttack;
import com.barribob.mm.entity.util.IAttack;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Represent the attibutes and logic of the shade monster
 */
public class EntityShade extends EntityMaelstromMob implements IAttack {
    public static final float PROJECTILE_INACCURACY = 0;
    public static final float PROJECTILE_VELOCITY = 1.0f;

    public EntityShade(EntityType<? extends EntityMaelstromMob> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAITimedAttack<>(this, 1.0f, 5, 3f, 0.5f));
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
        if (id == ModUtils.PARTICLE_BYTE) {
            if (this.getElement().equals(Element.NONE)) {
                ParticleManager.spawnMaelstromPotionParticle(level, random, this.position().add(ModRandom.randVec()).add(ModUtils.yVec(1)), false);
            }

            ParticleManager.spawnEffect(level, this.position().add(ModRandom.randVec()).add(ModUtils.yVec(1)), getElement().particleColor);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public int startAttack(LivingEntity target, float distanceFactor, boolean strafingBackwards) {
        ModBBAnimations.animation(this, "scout.attack", false);
        ModUtils.leapTowards(this, this.getTarget().position(), 0.4f, 0.3f);

        addEvent(() -> {
            Vec3 pos = this.position().add(ModUtils.yVec(1)).add(this.getLookAngle());
            this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.8F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            ModUtils.handleAreaImpact(0.6f, (e) -> this.getAttack(), this, pos, ModDamageSource.causeElementalMeleeDamage(this, getElement()), 0.20f, 0, false);
        }, 10);

        return 20;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
    }
}