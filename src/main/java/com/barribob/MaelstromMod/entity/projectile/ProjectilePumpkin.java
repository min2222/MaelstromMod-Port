package com.barribob.MaelstromMod.entity.projectile;

import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

/**
 * The projectile for the maelstrom cannon item
 */
public class ProjectilePumpkin extends ProjectileGun {
    private static final int PARTICLE_AMOUNT = 1;
    private static final int IMPACT_PARTICLE_AMOUNT = 50;
    private static final int EXPOSION_AREA_FACTOR = 4;
    private int rings;
    private int maxRings;
    Vec3 prevPos;

    public ProjectilePumpkin(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.prevPos = this.getPositionVector();
    }

    public ProjectilePumpkin(Level worldIn) {
        super(worldIn);
        this.prevPos = this.getPositionVector();
    }

    public ProjectilePumpkin(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    /**
     * Called every update to spawn particles
     *
     * @param world
     */
    @Override
    protected void spawnParticles() {
        if (this.ticksExisted <= 2) {
            this.prevPos = getPositionVector();
            return;
        }
        Vec3 vel = this.getPositionVector().subtract(this.prevPos).normalize();

        maxRings = ModRandom.range(4, 7);
        float tailWidth = 0.25f;
        ParticleManager.spawnSwirl(world,
                new Vec3(this.posX, this.posY, this.posZ).add(new Vec3(ModRandom.getFloat(tailWidth), ModRandom.getFloat(tailWidth), ModRandom.getFloat(tailWidth))),
                ModColors.YELLOW,
                vel.scale(0.1f),
                ModRandom.range(25, 30));

        if (this.rings < this.maxRings) {
            float circleSize = 1 + ModRandom.getFloat(0.9f);
            float f1 = Mth.sqrt(vel.x * vel.x + vel.z * vel.z);
            ModUtils.circleCallback(circleSize, 30, (pos) -> {

                // Conversion code taken from projectile shoot method
                Vec3 outer = pos.rotatePitch((float) (Mth.atan2(vel.y, f1))).rotateYaw((float) (Mth.atan2(vel.x, vel.z)))
                        .add(getPositionVector());
                ParticleManager.spawnEffect(world, outer, ModColors.YELLOW);
            });
            this.rings++;
        }

        this.prevPos = getPositionVector();
    }

    @Override
    protected void onHit(RayTraceResult result) {
        ModUtils.handleBulletImpact(result.entityHit, this, (float) (this.getGunDamage(result.entityHit) * this.getDistanceTraveled()),
                ModDamageSource.causeElementalThrownDamage(this, this.shootingEntity, getElement()), this.getKnockback());
        super.onHit(result);
    }
}
