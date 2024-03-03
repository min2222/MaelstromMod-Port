package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ProjectileRepeater extends ProjectileBullet {
    public ProjectileRepeater(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
    }

    public ProjectileRepeater(Level worldIn) {
        super(worldIn);
    }

    public ProjectileRepeater(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        if (this.getElement() == Element.NONE) {
        	level.addParticle(DustParticleOptions.REDSTONE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        } else {
            ParticleManager.spawnDust(level, position(), this.getElement().particleColor, Vec3.ZERO, ModRandom.range(10, 15));
        }
    }
}