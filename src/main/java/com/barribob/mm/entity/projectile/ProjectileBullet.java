package com.barribob.mm.entity.projectile;

import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ProjectileBullet extends ProjectileGun {
    public ProjectileBullet(Level worldIn, LivingEntity throwerIn, float damage, ItemStack stack, Element element) {
        super(worldIn, throwerIn, damage, stack, element);
    }

    public ProjectileBullet(Level worldIn, LivingEntity throwerIn, float damage, ItemStack stack) {
        super(worldIn, throwerIn, damage, stack);
    }

    public ProjectileBullet(Level worldIn) {
        super(worldIn);
    }

    public ProjectileBullet(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public Item getItemToRender() {
        if (getElement() == Element.GOLDEN) {
            return ModItems.GOLD_PELLET;
        } else if (getElement() == Element.CRIMSON) {
            return ModItems.CRIMSON_PELLET;
        }
        return ModItems.IRON_PELLET;
    }

    @Override
    protected void spawnParticles() {
        if (getElement() != Element.NONE) {
            ParticleManager.spawnEffect(level, ModUtils.entityPos(this), getElement().particleColor);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        ModUtils.handleBulletImpact(result.getEntity(), this, this.getGunDamage(result.getEntity()), ModDamageSource.causeElementalThrownDamage(this, this.shootingEntity, this.getElement()),
                this.getKnockback());
        super.onHitEntity(result);
    }
}
