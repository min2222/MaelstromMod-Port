package com.barribob.mm.entity.entities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import com.barribob.mm.entity.ai.EntityAIRangedAttack;
import com.barribob.mm.entity.animation.AnimationFloatingSkull;
import com.barribob.mm.entity.projectile.ProjectileSkullAttack;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityFloatingSkull extends EntityMaelstromMob {
    public EntityFloatingSkull(Level worldIn) {
        super(worldIn);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(4, new EntityAIRangedAttack<EntityMaelstromMob>(this, 1.0f, 60, 5, 7.5f, 0.5f));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (level.isClientSide) {
            ParticleManager.spawnDarkFlames(world, rand,
                    new Vec3(this.posX + ModRandom.getFloat(0.5f), this.posY + 0.1f + ModRandom.getFloat(0.1f), this.posZ + ModRandom.getFloat(0.5f)));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected float getSoundPitch() {
        return 0.8f + ModRandom.getFloat(0.1f);
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        if (swingingArms) {
            this.level.broadcastEntityEvent(this, (byte) 4);
        }
    }

    /**
     * Handler for {@link Level#setEntityState}
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.currentAnimation = new AnimationFloatingSkull();
            getCurrentAnimation().startAnimation();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    /**
     * Shoots a projectile in a similar fashion to the snow golem (see
     * EntitySnowman)
     */
    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!level.isClientSide) {
            world.playSound((Player) null, this.posX, this.posY, this.posZ, SoundEvents.BLAZE_AMBIENT, SoundSource.NEUTRAL, 0.5F,
                    0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));

            float inaccuracy = 0.0f;
            float speed = 0.5f;

            ProjectileSkullAttack projectile = new ProjectileSkullAttack(world, this, this.getAttack());
            projectile.shoot(this, this.rotationPitch, this.rotationYaw, 0.0F, speed, inaccuracy);
            projectile.setTravelRange(9f);

            level.addFreshEntity(projectile);
        }
    }
}