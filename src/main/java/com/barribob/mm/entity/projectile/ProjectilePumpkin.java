package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

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
        this.prevPos = this.position();
    }

    public ProjectilePumpkin(Level worldIn) {
        super(worldIn);
        this.prevPos = this.position();
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
        if (this.tickCount <= 2) {
            this.prevPos = position();
            return;
        }
        Vec3 vel = this.position().subtract(this.prevPos).normalize();

        maxRings = ModRandom.range(4, 7);
        float tailWidth = 0.25f;
        ParticleManager.spawnSwirl(level, this.position().add(new Vec3(ModRandom.getFloat(tailWidth), ModRandom.getFloat(tailWidth), ModRandom.getFloat(tailWidth))),
                ModColors.YELLOW,
                vel.scale(0.1f),
                ModRandom.range(25, 30));

        if (this.rings < this.maxRings) {
            float circleSize = 1 + ModRandom.getFloat(0.9f);
            float f1 = (float) Math.sqrt(vel.x * vel.x + vel.z * vel.z);
            ModUtils.circleCallback(circleSize, 30, (pos) -> {

                // Conversion code taken from projectile shoot method
                Vec3 outer = pos.xRot((float) (Mth.atan2(vel.y, f1))).yRot((float) (Mth.atan2(vel.x, vel.z)))
                        .add(position());
                ParticleManager.spawnEffect(level, outer, ModColors.YELLOW);
            });
            this.rings++;
        }

        this.prevPos = position();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        ModUtils.handleBulletImpact(result.getEntity(), this, (float) (this.getGunDamage(result.getEntity()) * this.getDistanceTraveled()),
                ModDamageSource.causeElementalThrownDamage(this, this.shootingEntity, getElement()), this.getKnockback());
        super.onHitEntity(result);
    }
}
