package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ProjectilePiercingBullet extends ProjectileBullet {
    public ProjectilePiercingBullet(Level worldIn, LivingEntity throwerIn, float damage, ItemStack stack) {
        super(worldIn, throwerIn, damage, stack);
    }

    public ProjectilePiercingBullet(Level worldIn) {
        super(worldIn);
    }

    public ProjectilePiercingBullet(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        ModUtils.handleBulletImpact(result.getEntity(), this, this.getGunDamage(result.getEntity()),
                ModDamageSource.causeElementalThrownDamage(this, this.shootingEntity, this.getElement()), this.getKnockback());

        if (result.getEntity() == null) {
            super.onHitEntity(result);
        }
    }
}
