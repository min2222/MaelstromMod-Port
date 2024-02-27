package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;

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
    protected void onHit(RayTraceResult result) {
        ModUtils.handleBulletImpact(result.entityHit, this, this.getGunDamage(result.entityHit),
                ModDamageSource.causeElementalThrownDamage(this, this.shootingEntity, this.getElement()), this.getKnockback());

        if (result.entityHit == null) {
            super.onHit(result);
        }
    }
}
