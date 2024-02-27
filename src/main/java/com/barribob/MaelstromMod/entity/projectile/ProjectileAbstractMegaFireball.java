package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    protected final void onHit(RayTraceResult result) {
        boolean isShootingEntity = result != null && result.entityHit != null && result.entityHit == this.shootingEntity;
        boolean isPartOfShootingEntity = result != null && result.entityHit != null && (result.entityHit instanceof MultiPartEntityPart && ((MultiPartEntityPart) result.entityHit).parent == this.shootingEntity);
        if (isShootingEntity || isPartOfShootingEntity || !canExplode()) {
            return;
        }

        super.onHit(result);
    }

    protected abstract void onImpact(@Nullable RayTraceResult result);

    @Override
    public void onUpdate() {

        Vec3 vel = ModUtils.getEntityVelocity(this);
        super.onUpdate();
        // Maintain the velocity the entity has
        ModUtils.setEntityVelocity(this, vel);
    }

    @Override
    public void setDead() {
        if (canExplode()) {
            isExploded = true;
            onImpact(null);
        }
        super.setDead();
    }

    @Override
    public final boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if(canBeHit && canExplode()) {
            this.setDead();
            return super.attackEntityFrom(source, amount);
        }
        return false;
    }

    private boolean canExplode() {
        return shootingEntity != null && !isExploded && !world.isRemote;
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
