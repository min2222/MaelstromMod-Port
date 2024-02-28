package com.barribob.mm.entity.projectile;

import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ProjectileMonolithFireball extends ProjectileGun {
    private static final int EXPOSION_AREA_FACTOR = 2;
    public static final Vec3 FIREBALL_COLOR = new Vec3(1.0, 0.6, 0.5);

    public ProjectileMonolithFireball(Level worldIn, LivingEntity throwerIn, float baseDamage, ItemStack stack) {
        super(worldIn, throwerIn, baseDamage, stack);
        this.setNoGravity(true);
    }

    public ProjectileMonolithFireball(Level worldIn) {
        super(worldIn);
        this.setNoGravity(true);
    }

    public ProjectileMonolithFireball(Level worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.setNoGravity(true);
    }

    @Override
    public void onUpdate() {
        this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.5f, ModRandom.getFloat(0.2f) + 1.0f);
        super.onUpdate();
    }

    @Override
    protected void spawnParticles() {
        float size = 0.25f;
        ParticleManager.spawnEffect(this.world, position().add(ModRandom.randVec().scale(size)), ModColors.RED);
        world.spawnParticle(ParticleTypes.LAVA, this.posX, this.posY, this.posZ, 0, 0, 0);

        float groundHeight = ModUtils.findGroundBelow(world, getPosition()).getY() + 1.2f;
        Vec3 indicationPos = new Vec3(posX, groundHeight, posZ);
        ModUtils.circleCallback(EXPOSION_AREA_FACTOR, 6, (pos) -> {
            Vec3 circleOffset = rotateCircleOverTime(pos);
            ParticleManager.spawnEffect(world, indicationPos.add(circleOffset), ModColors.RED);
        });
    }

    private Vec3 rotateCircleOverTime(Vec3 pos) {
        Vec3 circleOffset = new Vec3(pos.x, 0, pos.y);
        circleOffset = ModUtils.rotateVector2(circleOffset, ModUtils.Y_AXIS, tickCount * 2);
        return circleOffset;
    }

    @Override
    protected void spawnImpactParticles() {
        for (int i = 0; i < 30; i++) {
            this.world.spawnParticle(ParticleTypes.EXPLOSION_LARGE, this.posX + ModRandom.getFloat(EXPOSION_AREA_FACTOR),
                    this.posY + ModRandom.getFloat(EXPOSION_AREA_FACTOR), this.posZ + ModRandom.getFloat(EXPOSION_AREA_FACTOR), 0, 0, 0);
            this.world.spawnParticle(ParticleTypes.FLAME, this.posX + ModRandom.getFloat(EXPOSION_AREA_FACTOR), this.posY + ModRandom.getFloat(EXPOSION_AREA_FACTOR),
                    this.posZ + ModRandom.getFloat(EXPOSION_AREA_FACTOR), 0, 0, 0);
            ParticleManager.spawnEffect(world, position().add(ModRandom.randVec().scale(EXPOSION_AREA_FACTOR * 2)), ModColors.RED);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        float knockbackFactor = 1.1f + this.getKnockback() * 0.4f;
        int fireFactor = this.isBurning() ? 8 : 3;

        DamageSource source = ModDamageSource.builder()
                .type(ModDamageSource.EXPLOSION)
                .directEntity(this)
                .indirectEntity(shootingEntity)
                .stoppedByArmorNotShields()
                .element(getElement()).build();

        ModUtils.handleAreaImpact(EXPOSION_AREA_FACTOR, this::getGunDamage, this.shootingEntity, this.position(),
                source, knockbackFactor, fireFactor);
        if (!level.isClientSide) {
            for (int j = 0; j < 5; j++) {
                Vec3 pos = position().add(ModRandom.randVec().scale(EXPOSION_AREA_FACTOR - 1));
                if (world.isBlockFullCube(new BlockPos(pos).down()) && world.isAirBlock(new BlockPos(pos))) {
                    world.setBlockState(new BlockPos(pos), Blocks.FIRE.getDefaultState());
                }
            }
        }
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
        super.onHit(result);
    }
}
