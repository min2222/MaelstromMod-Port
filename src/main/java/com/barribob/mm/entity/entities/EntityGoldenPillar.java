package com.barribob.mm.entity.entities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;

import com.barribob.mm.entity.ai.EntityAITimedAttack;
import com.barribob.mm.entity.projectile.EntityGoldenRune;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.util.IAttack;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LootTableHandler;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityGoldenPillar extends EntityMaelstromMob implements IAttack {
    public EntityGoldenPillar(Level worldIn) {
        super(worldIn);
        this.setSize(1.4f, 3.2f);
        this.setNoGravity(true);
        this.setImmovable(true);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(4, new EntityAITimedAttack<>(this, 0f, 60, 40, 30.0f, 0f));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            // Spawn particles as the eyes
            ModUtils.performNTimes(3, (i) -> {
                Vec3 look = this.getVectorForRotation(0, this.renderYawOffset + (i * 120)).scale(0.5f);
                Vec3 pos = this.position().add(new Vec3(0, this.getEyeHeight(), 0));
                ParticleManager.spawnEffect(world, pos.add(look), ModColors.YELLOW);
            });
        }
        else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            ParticleManager.spawnFirework(world,
                    this.position().add(new Vec3(ModRandom.getFloat(0.25f), 1, ModRandom.getFloat(0.25f))),
                    ModColors.YELLOW,
                    new Vec3(0, 0.15, 0));
        }
        super.handleStatusUpdate(id);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.BLOCK_METAL_PLACE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_METAL_BREAK;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableHandler.GOLDEN_MAELSTROM;
    }

    private Runnable getRune(Vec3 target) {
        return () -> {
            ModProjectile projectile = new EntityGoldenRune(world, this, this.getAttack());
            projectile.setTravelRange(30);
            ModUtils.throwProjectile(this, target, projectile, 4, 0.5f);
            playSound(SoundEvents.BLOCK_METAL_BREAK, 1.0f, 1.0f + ModRandom.getFloat(0.2f));
        };
    }

    @Override
    public int startAttack(LivingEntity target, float distanceSq, boolean strafingBackwards) {

        /*
        Oscillate attacks between attack, and to the side
         */

        addEvent(() -> {
            Vec3 centerTarget = target.getEyePosition(1);
            Vec3 offset = centerTarget
                    .subtract(getEyePosition(1))
                    .cross(ModUtils.yVec(1))
                    .normalize()
                    .scale(3 * ModRandom.randSign());

            getRune(centerTarget).run();
            addEvent(getRune(centerTarget.add(offset)), 10);
            addEvent(getRune(centerTarget.subtract(offset)), 20);
        }, 40);

        for(int i = 0; i < 40; i++) {
            addEvent(() -> level.broadcastEntityEvent(this, ModUtils.SECOND_PARTICLE_BYTE), i);
        }

        return 60;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
    }
}
