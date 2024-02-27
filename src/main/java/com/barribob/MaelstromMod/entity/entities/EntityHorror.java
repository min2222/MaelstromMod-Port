package com.barribob.MaelstromMod.entity.entities;

import com.barribob.MaelstromMod.entity.ai.EntityAIRangedAttack;
import com.barribob.MaelstromMod.entity.projectile.ProjectileHorrorAttack;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import com.barribob.MaelstromMod.util.handlers.SoundsHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class EntityHorror extends EntityMaelstromMob {
    public static final float PROJECTILE_INACCURACY = 0;
    public static final float PROJECTILE_VELOCITY = 0.5f;
    public static final float PROJECTILE_VARIATION_FACTOR = 0.5f;

    public EntityHorror(Level worldIn) {
        super(worldIn);
        this.setSize(1.3F, 1.3F);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(4, new EntityAIRangedAttack<EntityMaelstromMob>(this, 1.0f, 20, 5.0f, 0.5f));
    }

    /**
     * Spawns smoke out of the middle of the entity
     */
    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            for (int i = 0; i < 5; i++) {
                ParticleManager.spawnMaelstromSmoke(world, rand, new Vec3(this.posX + ModRandom.getFloat(0.4f), this.posY + 1, this.posZ + ModRandom.getFloat(0.4f)), true);
            }
        }
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        if (!world.isRemote) {
            ProjectileHorrorAttack projectile = new ProjectileHorrorAttack(this.world, this, this.getAttack());
            double xDir = (rand.nextFloat() - rand.nextFloat()) * PROJECTILE_VARIATION_FACTOR;
            double yDir = 1;
            double zDir = (rand.nextFloat() - rand.nextFloat()) * PROJECTILE_VARIATION_FACTOR;
            projectile.shoot(xDir, yDir, zDir, PROJECTILE_VELOCITY, PROJECTILE_INACCURACY);
            this.playSound(SoundEvents.BLOCK_ANVIL_BREAK, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.world.spawnEntity(projectile);
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
