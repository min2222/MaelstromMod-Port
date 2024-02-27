package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

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
        for (int i = 0; i < this.PARTICLE_AMOUNT; i++) {
            ParticleManager.spawnColoredSmoke(world, getPositionVector().add(ModRandom.randVec()), ModColors.DARK_GREY, Vec3.ZERO);
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (!world.isRemote) {
            world.createExplosion(this.shootingEntity, this.posX, this.posY, this.posZ, 3, true);
        }
    }
}
