package com.barribob.mm.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.barribob.mm.util.ModUtils;

public abstract class ProjectileAbstractMegaFireball extends ProjectileGun {
    private boolean canBeHit;
    private boolean isExploded;

    public ProjectileAbstractMegaFireball(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack, boolean canBeHit) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
        this.setSize(1, 1);
        this.canBeHit = canBeHit;
    }

    public ProjectileAbstractMegaFireball(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
        this.setSize(1, 1);
    }

    public ProjectileAbstractMegaFireball(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
        this.setSize(1, 1);
    }

    @Override
    protected final void onHitEntity(EntityHitResult result) {
        boolean isShootingEntity = result != null && result.getEntity() != null && result.getEntity() == this.shootingEntity;
        boolean isPartOfShootingEntity = result != null && result.getEntity() != null && (result.getEntity() instanceof PartEntity && ((PartEntity) result.getEntity()).getParent() == this.shootingEntity);
        if (isShootingEntity || isPartOfShootingEntity || !canExplode()) {
            return;
        }

        super.onHitEntity(result);
    }

    protected abstract void onImpact(@Nullable HitResult result);

    @Override
    public void tick() {

        Vec3 vel = ModUtils.getEntityVelocity(this);
        super.tick();
        // Maintain the velocity the entity has
        ModUtils.setEntityVelocity(this, vel);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (canExplode()) {
            isExploded = true;
            onImpact(null);
        }
        super.remove(reason);
    }

    @Override
    public final boolean hurt(@Nonnull DamageSource source, float amount) {
        if(canBeHit && canExplode()) {
            this.discard();
            return super.hurt(source, amount);
        }
        return false;
    }

    private boolean canExplode() {
        return shootingEntity != null && !isExploded && !level.isClientSide;
    }

    @Override
    public final boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public final boolean canBeAttackedWithItem() {
        return canBeHit;
    }

    @Override
    public int getBrightnessForRender() {
        return 200;
    }
}