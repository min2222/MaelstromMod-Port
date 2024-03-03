package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ProjectileExplosiveDrill extends ProjectileGun {
    private static final int PARTICLE_AMOUNT = 15;

    public ProjectileExplosiveDrill(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
    }

    public ProjectileExplosiveDrill(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileExplosiveDrill(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < ProjectileExplosiveDrill.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnColoredSmoke(level, position().add(ModRandom.randVec()), ModColors.DARK_GREY, Vec3.ZERO);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level.isClientSide) {
            level.explode(this.shootingEntity, this.getX(), this.getY(), this.getZ(), 3, BlockInteraction.DESTROY);
        }
    }
}
