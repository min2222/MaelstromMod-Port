package com.barribob.mm.entity.entities;

import com.barribob.mm.entity.ai.EntityAIRangedAttack;
import com.barribob.mm.entity.projectile.ProjectileHorrorAttack;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityHorror extends EntityMaelstromMob {
    public static final float PROJECTILE_INACCURACY = 0;
    public static final float PROJECTILE_VELOCITY = 0.5f;
    public static final float PROJECTILE_VARIATION_FACTOR = 0.5f;

    public EntityHorror(Level worldIn) {
        super(worldIn);
        this.setSize(1.3F, 1.3F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAIRangedAttack<EntityMaelstromMob>(this, 1.0f, 20, 5.0f, 0.5f));
    }

    /**
     * Spawns smoke out of the middle of the entity
     */
    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            for (int i = 0; i < 5; i++) {
                ParticleManager.spawnMaelstromSmoke(level, random, new Vec3(this.getX() + ModRandom.getFloat(0.4f), this.getY() + 1, this.getZ() + ModRandom.getFloat(0.4f)), true);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!level.isClientSide) {
            ProjectileHorrorAttack projectile = new ProjectileHorrorAttack(this.level, this, this.getAttack());
            double xDir = (random.nextFloat() - random.nextFloat()) * PROJECTILE_VARIATION_FACTOR;
            double yDir = 1;
            double zDir = (random.nextFloat() - random.nextFloat()) * PROJECTILE_VARIATION_FACTOR;
            projectile.shoot(xDir, yDir, zDir, PROJECTILE_VELOCITY, PROJECTILE_INACCURACY);
            this.playSound(SoundEvents.ANVIL_BREAK, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            this.level.addFreshEntity(projectile);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTITY_HORROR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTITY_HORROR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTITY_HORROR_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 0.25f;
    }
}
