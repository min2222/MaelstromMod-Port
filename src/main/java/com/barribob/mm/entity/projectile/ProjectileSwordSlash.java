package com.barribob.mm.entity.projectile;

import com.barribob.mm.Main;
import com.barribob.mm.packets.MessageModParticles;
import com.barribob.mm.particle.EnumModParticles;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileSwordSlash extends ModProjectile {
    private static final int PARTICLE_AMOUNT = 10;

    public ProjectileSwordSlash(Level worldIn, LivingEntity throwerIn, float damage) {
        super(worldIn, throwerIn, damage);
        this.setNoGravity(true);
        this.setSize(0.25f, 0.25f);
    }

    public ProjectileSwordSlash(Level worldIn) {
        super(worldIn);
    }

    public ProjectileSwordSlash(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public void tick() {
        super.onUpdate();
        if (!level.isClientSide && this.shootingEntity != null) {
            if (this.world instanceof ServerLevel) {
                Main.network.sendToAllTracking(new MessageModParticles(EnumModParticles.SWEEP_ATTACK, position(), Vec3.ZERO, this.getElement().sweepColor), this);
            }

            ModUtils.handleAreaImpact(1.5f, (e) -> this.getDamage(), this.shootingEntity, this.position(), ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()),
                    0.2f, 0);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.entityHit == null) {
            super.onHit(result);
            return;
        }
    }
}
