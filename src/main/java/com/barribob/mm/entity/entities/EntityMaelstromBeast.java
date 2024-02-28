package com.barribob.mm.entity.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.barribob.mm.entity.ai.EntityAITimedAttack;
import com.barribob.mm.entity.projectile.*;
import com.barribob.mm.entity.util.IAttack;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LootTableHandler;
import com.barribob.mm.util.handlers.ParticleManager;

public class EntityMaelstromBeast extends EntityMaelstromMob implements IAttack {
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossEvent.Color.PURPLE, BossEvent.Overlay.NOTCHED_20));
    private Consumer<LivingEntity> attack;

    public EntityMaelstromBeast(Level worldIn) {
        super(worldIn);
        this.healthScaledAttackFactor = 0.2;
        this.setSize(1.4f, 2.5f);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(4, new EntityAITimedAttack<>(this, 1.25f, 50, 30, 0.5f, 10f));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!level.isClientSide && this.isRaged()) {
            level.broadcastEntityEvent(this, ModUtils.SECOND_PARTICLE_BYTE);
        }

        if(!level.isClientSide && this.isLeaping() && this.getTarget() != null &&
                this.getDistanceSq(this.getTarget()) < Math.pow(4, 2)){
            this.setLeaping(false);
            onStopLeaping();
        }

        if(!level.isClientSide && this.isLeaping()) {
            AABB box = getBoundingBox().grow(0.25, 0.12, 0.25).offset(0, 0.12, 0);
            ModUtils.destroyBlocksInAABB(box, world, this);
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            ParticleManager.spawnEffect(world, position().add(ModRandom.randVec().scale(2)).add(ModUtils.yVec(this.getEyeHeight())), ModColors.RED);
        }
        super.handleStatusUpdate(id);
    }

    public boolean isRaged() {
        return this.getHealth() <= getMobConfig().getDouble("second_phase_hp");
    }

    @Override
    public int startAttack(LivingEntity target, float distanceSq, boolean strafingBackwards) {

        if(target.posY - posY > 3)
        {
            spray.accept(target);
            return 50;
        }
        else if(distanceSq > Math.pow(12, 2)) {
            leap.accept(target);
            return 50;
        }

        List<Consumer<LivingEntity>> attacks = new ArrayList<>(Arrays.asList(swipe, roar, quake));
        double[] weights = {
                Math.sqrt(Math.max(0, Math.pow(5, 2) - distanceSq)),
                attack != roar ? 0.5 : 0,
                1};

        attack = ModRandom.choice(attacks, rand, weights).next();
        attack.accept(target);
        return 50;
    }

    Runnable spawnQuake = () -> {
        ModProjectile projectile = this.isRaged() ?
                new ProjectileBoneQuake(world, this, this.getAttack() * getConfigFloat("bone_hammer_wave_damage")) :
                new ProjectileBeastQuake(world, this, this.getAttack() * getConfigFloat("hammer_wave_damage"));

        Vec3 projectileOffset = ModUtils.getRelativeOffset(this, new Vec3(2, -2, 0));
        Vec3 forwardPos = ModUtils.getRelativeOffset(this, new Vec3(3, -2, 0)).add(getEyePosition(1));
        ModUtils.throwProjectile(this, forwardPos, projectile, 0, 0.8f, projectileOffset);
    };

    private final Consumer<LivingEntity> quake = target -> {
        ModBBAnimations.animation(this, "beast.quake", false);
        ModUtils.leapTowards(this, target.position(), 0, 0.5f);
        addEvent(spawnQuake, 20);
    };

    @Override
    public void onStopLeaping() {
        ModBBAnimations.animation(this, "beast.leap", true);

        if(getTarget() != null && getTarget().getDistanceSq(this) < Math.pow(12, 2)) {
            ModBBAnimations.animation(this, "beast.slam", false);
            addEvent(spawnQuake, 4);
        }
        else {
            ModBBAnimations.animation(this, "beast.reset", false);
        }
    }

    private final Consumer<LivingEntity> swipe = target -> {
        ModBBAnimations.animation(this, "beast.swipe", false);

        addEvent(() -> {
            Vec3 offset = this.position().add(ModUtils.getRelativeOffset(this, new Vec3(2, 0, 0)));
            Vec3 offset2 = this.position().add(ModUtils.getRelativeOffset(this, new Vec3(1, 0, 1)));
            DamageSource source = ModDamageSource.builder()
                    .type(ModDamageSource.MOB)
                    .directEntity(this)
                    .element(this.getElement())
                    .stoppedByArmorNotShields()
                    .disablesShields().build();

            float damage = this.getAttack() * getConfigFloat("swipe_damage");
            ModUtils.handleAreaImpact(2, (e) -> damage, this, offset, source, 1, 0, false);
            ModUtils.handleAreaImpact(2, (e) -> damage, this, offset2, source, 1, 0, false);

            double width = getMobConfig().getDouble("swipe_width");

            ModUtils.destroyBlocksInAABB(new AABB(getPosition())
                    .grow(width, 1, width)
                    .offset(ModUtils.getRelativeOffset(this, new Vec3(1, 1, 0))), world, this);

            if (EntityMaelstromBeast.this.isRaged()) {
                ModUtils.performNTimes(8, (i) -> spawnBone(world, offset.add(ModRandom.randVec().scale(3)), EntityMaelstromBeast.this));
            }
            this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.4f + ModRandom.getFloat(0.1f));
        }, 15);
    };

    private final Consumer<LivingEntity> roar = target -> {
        ModBBAnimations.animation(this, "beast.roar", false);

        addEvent(() -> {
            if (EntityMaelstromBeast.this.isRaged()) {
                ModUtils.spawnMob(world, getPosition(), getLevel(), getMobConfig().getConfig("spawning_algorithm"));
            } else {
                ModUtils.handleAreaImpact(20, (e) -> {
                    if (e instanceof LivingEntity) {
                        ((LivingEntity) e).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120, 3));
                    }
                    return 0.0f;
                }, this, this.position(), ModDamageSource.MAELSTROM_DAMAGE);
            }
            this.playSound(SoundEvents.ENDERDRAGON_GROWL, 1.0F, 0.9F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        }, 12);
    };

    public final Consumer<LivingEntity> spray = target -> {
        ModUtils.leapTowards(this, target.position(), 0, 1.6f);
        ModBBAnimations.animation(this, "beast.leap", false);

        addEvent(() -> {
            ModBBAnimations.animation(this, "beast.leap", true);
            ModBBAnimations.animation(this, "beast.slam", false);

            Vec3 targetPos = target.getEyePosition(1);
            Vec3 dir = getEyePosition(1).subtract(targetPos);
            Vec3 axis = ModUtils.rotateVector2(dir.cross(ModUtils.Y_AXIS), dir, 90).normalize().scale(5);

            ModUtils.lineCallback(targetPos.add(axis), targetPos.subtract(axis), 5, (pos, i) -> {
                ModProjectile projectile = new ProjectileBeastFireball(world, this, this.getAttack() * getConfigFloat("high_leap_fireball_damage"));
                ModUtils.throwProjectile(this, pos, projectile, 4, 0.7f);
            });

        }, 30);
    };

    private final Consumer<LivingEntity> leap = target -> {
        ModBBAnimations.animation(this, "beast.leap", false);

        addEvent(() -> {
            ModUtils.leapTowards(this, target.position(), 1.8f, (float) 0.75);
            setLeaping(true);
        }, 16);
    };

    public static void spawnBone(Level world, Vec3 pos, EntityLeveledMob entity) {
        if (!level.isClientSide) {
            ProjectileBone projectile = new ProjectileBone(world, entity, entity.getAttack() * entity.getConfigFloat("bone_projectile_damage"));
            projectile.setPosition(pos.x, pos.y + 1.5, pos.z);
            double xDir = ModRandom.getFloat(0.1f);
            double yDir = 1 + ModRandom.getFloat(0.1f);
            double zDir = ModRandom.getFloat(0.1f);
            projectile.shoot(xDir, yDir, zDir, 0.5f, 0.5f);
            level.addFreshEntity(projectile);
        }
    }

    @Override
    public void readEntityFromNBT(CompoundTag compound) {
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }

        super.readEntityFromNBT(compound);
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void updateAITasks() {
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
        super.updateAITasks();
    }

    @Override
    public void addTrackingPlayer(ServerPlayer player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(ServerPlayer player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.POLAR_BEAR_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableHandler.BEAST;
    }

    @Override
    protected float getSoundPitch() {
        return super.getSoundPitch() * 0.8f;
    }

    @Override
    protected float getSoundVolume() {
        return 0.6F;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
    }
}
