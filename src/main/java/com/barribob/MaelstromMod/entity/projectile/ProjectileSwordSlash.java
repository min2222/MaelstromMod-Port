package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.packets.MessageModParticles;
import com.barribob.MaelstromMod.particle.EnumModParticles;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileSwordSlash extends Projectile {
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
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote && this.shootingEntity != null) {
            if (this.world instanceof ServerLevel) {
                Main.network.sendToAllTracking(new MessageModParticles(EnumModParticles.SWEEP_ATTACK, getPositionVector(), Vec3.ZERO, this.getElement().sweepColor), this);
            }

            ModUtils.handleAreaImpact(1.5f, (e) -> this.getDamage(), this.shootingEntity, this.getPositionVector(), ModDamageSource.causeElementalThrownDamage(this, shootingEntity, getElement()),
                    0.2f, 0);
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (result.entityHit == null) {
            super.onHit(result);
            return;
        }
    }
}
