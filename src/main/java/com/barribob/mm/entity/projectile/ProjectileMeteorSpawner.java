package com.barribob.mm.entity.projectile;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public class ProjectileMeteorSpawner extends ProjectileGun {
    private static final int EXPOSION_AREA_FACTOR = 6;
    private ItemStack stack;

    public ProjectileMeteorSpawner(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.stack = stack;
        this.setNoGravity(true);
    }

    public ProjectileMeteorSpawner(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileMeteorSpawner(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    protected void spawnParticles() {
    }

    @Override
    protected void onHit(HitResult result) {
        if (this.shootingEntity != null && !level.isClientSide) {
            ProjectileMeteor meteor = new ProjectileMeteor(world, shootingEntity, this.getDamage(), stack);
            meteor.setPosition(this.posX, this.posY + 25, this.posZ);
            meteor.shoot(this.shootingEntity, 90, 0, 0.0F, 1.0f, 0);
            meteor.setTravelRange(100f);
            level.addFreshEntity(meteor);
        }
        super.onHit(result);
    }
}
