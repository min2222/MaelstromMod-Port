package com.barribob.mm.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

/**
 * Fireball for the Gauntlet. Main things are that it can be collided with (shot down) and it spawns more projectiles on impact
 *
 * @author Barribob
 */
public class ProjectileMegaFireball extends ProjectileAbstractMegaFireball {
    private static final int PARTICLE_AMOUNT = 15;
    private static final int IMPACT_PARTICLE_AMOUNT = 30;
    private static final int EXPOSION_AREA_FACTOR = 4;

    public ProjectileMegaFireball(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack, boolean canTakeDamage) {
        super(worldIn, throwerIn, baseDamage, stack, canTakeDamage);
    }

    public ProjectileMegaFireball(Level worldIn) {
        super(worldIn);
    }

    public ProjectileMegaFireball(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    protected void spawnParticles() {
        for (int i = 0; i < PARTICLE_AMOUNT; i++) {
            Vec3 origin = this.position().add(ModUtils.getAxisOffset(ModUtils.getEntityVelocity(this).normalize(), new Vec3(1, 0, 0)));
            Vec3 smokePos = origin.add(ModRandom.randVec());
            world.spawnParticle(ParticleTypes.SMOKE_LARGE, smokePos.x, smokePos.y, smokePos.z, 0, 0, 0);
            ParticleManager.spawnEffect(world, origin.add(ModRandom.randVec()), ModColors.FIREBALL_ORANGE);
        }
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < IMPACT_PARTICLE_AMOUNT; i++) {
            Vec3 pos = this.position().add(ModRandom.randVec().scale(EXPOSION_AREA_FACTOR));
            Vec3 vel = pos.subtract(this.position()).normalize().scale(world.rand.nextFloat() * 0.3f);
            this.world.spawnParticle(ParticleTypes.FLAME, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
        }
    }

    @Override
    protected void onImpact(@Nullable HitResult result) {
        int fireFactor = this.isBurning() ? 10 : 5;

        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.EXPLOSION)
                .directEntity(this)
                .indirectEntity(this.shootingEntity)
                .element(this.getElement())
                .stoppedByArmorNotShields().build();

        ModUtils.handleAreaImpact(7, this::getGunDamage, this.shootingEntity, this.position(), source, 0, fireFactor);
        boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this.shootingEntity);
        if(!level.isClientSide) {
            this.world.newExplosion(null, this.posX, this.posY, this.posZ, 3, true, flag);

            for (int i = 0; i < 10; i++) {
                Vec3 vel = ModRandom.randVec().normalize().scale(0.5f).add(ModUtils.yVec(1));
                ProjectileFireball shrapenel = new ProjectileFireball(world, shootingEntity, this.getDamage() * 0.5f, null);
                ModUtils.setEntityPosition(shrapenel, this.position().add(ModUtils.yVec(1)).add(ModRandom.randVec()));
                shrapenel.setNoGravity(false);
                shrapenel.setTravelRange(50);
                level.addFreshEntity(shrapenel);
                ModUtils.setEntityVelocity(shrapenel, vel);
            }
        }
    }

    @Override
    public void tick() {
        if (this.tickCount % 3 == 0) {
            this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.2f, ModRandom.getFloat(0.2f) + 1.0f);
        }

        super.onUpdate();
    }
}
