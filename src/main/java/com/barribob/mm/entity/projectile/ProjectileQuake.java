package com.barribob.mm.entity.projectile;

import java.util.List;

import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

/**
 * Projectile from the quake staff
 */
public class ProjectileQuake extends ProjectileGun {
    protected static final float AREA_FACTOR = 0.5f;
    protected int updates = 5;

    public ProjectileQuake(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
    }

    public ProjectileQuake(Level worldIn) {
        super(worldIn);
    }

    public ProjectileQuake(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        BlockState block = level.getBlockState(this.blockPosition());
        if (block.isFullCube()) {
            for (int i = 0; i < 5; i++) {
                ParticleManager.spawnBreak(level, this.position().add(ModRandom.randVec().scale(1.0f).add(ModUtils.yVec(0.75f))), Item.BY_BLOCK.get(block.getBlock()), ModRandom.randVec().scale(0.1).add(ModUtils.yVec(0.1f)));
            }
            if (this.getElement() != Element.NONE) {
                ParticleManager.spawnEffect(level, position().add(ModUtils.yVec(0.5f)).add(ModRandom.randVec()), this.getElement().particleColor);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Keeps the projectile on the surface of the ground
        for (int i = 0; i < updates; i++) {
            if (!level.getBlockState(this.blockPosition()).isFullCube()) {
                this.setPos(this.getX(), this.getY() - 0.25f, this.getZ());
            } else if (level.getBlockState(this.blockPosition().above()).isFullCube()) {
                this.setPos(this.getX(), this.getY() + 0.25f, this.getZ());
            }
        }

        playQuakeSound();

        onQuakeUpdate();

        // If the projectile hits water and looses all of its velocity, despawn
        if (!level.isClientSide && Math.abs(this.getDeltaMovement().x) + Math.abs(this.getDeltaMovement().z) < 0.01f) {
            this.discard();
        }
    }

    protected void onQuakeUpdate() {
        List<Entity> list = level.getEntities(this, this.getBoundingBox().inflate(AREA_FACTOR).expandTowards(0, 0.25f, 0));
        for (Entity entity : list) {
            if (entity instanceof LivingEntity && this.shootingEntity != null && entity != this.shootingEntity) {
                int burnTime = this.isOnFire() ? 5 : 0;
                entity.setSecondsOnFire(burnTime);

                DamageSource source = ModDamageSource.builder()
                        .type(ModDamageSource.PROJECTILE)
                        .indirectEntity(shootingEntity)
                        .directEntity(this)
                        .element(getElement())
                        .stoppedByArmorNotShields().build();

                entity.hurt(source, this.getGunDamage(entity));

                // Apply knockback enchantment
                if (this.getKnockback() > 0) {
                    float f1 = (float) Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z);

                    if (f1 > 0.0F) {
                        entity.push(this.getDeltaMovement().x * this.getKnockback() * 0.6000000238418579D / f1, 0.1D,
                        		this.getDeltaMovement().z * this.getKnockback() * 0.6000000238418579D / f1);
                    }
                }
            }
        }
    }

    protected void playQuakeSound() {
        // Play the block break sound
        BlockPos pos = this.blockPosition();
        BlockState state = level.getBlockState(pos);
        if (state.isFullCube()) {
            level.playLocalSound(this.getX(), this.getY(), this.getZ(), state.getBlock().getSoundType(state, level, pos, this).getStepSound(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
    }

    @Override
    protected void onHit(HitResult result) {
    }
}
