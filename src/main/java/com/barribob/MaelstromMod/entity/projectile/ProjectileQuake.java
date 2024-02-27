package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.Element;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;

import java.util.List;

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
        BlockState block = world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ));
        if (block.isFullCube()) {
            for (int i = 0; i < 5; i++) {
                ParticleManager.spawnBreak(world, this.getPositionVector().add(ModRandom.randVec().scale(1.0f).add(ModUtils.yVec(0.75f))), Item.getItemFromBlock(block.getBlock()), ModRandom.randVec().scale(0.1).add(ModUtils.yVec(0.1f)));
            }
            if (this.getElement() != Element.NONE) {
                ParticleManager.spawnEffect(world, getPositionVector().add(ModUtils.yVec(0.5f)).add(ModRandom.randVec()), this.getElement().particleColor);
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // Keeps the projectile on the surface of the ground
        for (int i = 0; i < updates; i++) {
            if (!world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).isFullCube()) {
                this.setPosition(this.posX, this.posY - 0.25f, this.posZ);
            } else if (world.getBlockState(new BlockPos(this.posX, this.posY + 1, this.posZ)).isFullCube()) {
                this.setPosition(this.posX, this.posY + 0.25f, this.posZ);
            }
        }

        playQuakeSound();

        onQuakeUpdate();

        // If the projectile hits water and looses all of its velocity, despawn
        if (!world.isRemote && Math.abs(this.motionX) + Math.abs(this.motionZ) < 0.01f) {
            this.setDead();
        }
    }

    protected void onQuakeUpdate() {
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(AREA_FACTOR).expand(0, 0.25f, 0));
        for (Entity entity : list) {
            if (entity instanceof LivingEntity && this.shootingEntity != null && entity != this.shootingEntity) {
                int burnTime = this.isBurning() ? 5 : 0;
                entity.setFire(burnTime);

                DamageSource source = ModDamageSource.builder()
                        .type(ModDamageSource.PROJECTILE)
                        .indirectEntity(shootingEntity)
                        .directEntity(this)
                        .element(getElement())
                        .stoppedByArmorNotShields().build();

                entity.attackEntityFrom(source, this.getGunDamage(entity));

                // Apply knockback enchantment
                if (this.getKnockback() > 0) {
                    float f1 = Mth.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                    if (f1 > 0.0F) {
                        entity.addVelocity(this.motionX * this.getKnockback() * 0.6000000238418579D / f1, 0.1D,
                                this.motionZ * this.getKnockback() * 0.6000000238418579D / f1);
                    }
                }
            }
        }
    }

    protected void playQuakeSound() {
        // Play the block break sound
        BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
        BlockState state = world.getBlockState(pos);
        if (state.isFullCube()) {
            world.playSound(this.posX, this.posY, this.posZ, state.getBlock().getSoundType(state, world, pos, this).getStepSound(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
    }
}
