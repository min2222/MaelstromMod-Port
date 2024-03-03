package com.barribob.mm.entity.entities;

import com.barribob.mm.entity.ai.AIFlyingSupport;
import com.barribob.mm.entity.ai.FlyingMoveHelper;
import com.barribob.mm.entity.projectile.EntityHealerOrb;
import com.barribob.mm.entity.util.IAcceleration;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMaelstromHealer extends EntityMaelstromMob implements IAcceleration {
    Vec3 acceleration = Vec3.ZERO;
    protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.<Boolean>defineId(EntityMaelstromHealer.class, EntityDataSerializers.BOOLEAN);
    private Goal flyingAI = new AIFlyingSupport(this, 1.2f, 3.5f, 10f, 60);
    private float timeSinceNoTarget = 0;

    public EntityMaelstromHealer(Level worldIn) {
        super(worldIn);
        this.setSize(0.9f, 2.0f);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 motion = this.getDeltaMovement()
        this.acceleration = motion.scale(0.1).add(this.acceleration.scale(0.9));

        if (this.level.isClientSide) {
            return;
        }

        // Switch to flying mode
        if (!this.isFlying() && ((!this.onGround && ModUtils.isAirBelow(level, blockPosition(), 4)) || this.getTarget() != null)) {
            this.setFlying(true);
            this.goalSelector.addGoal(4, flyingAI);
            this.moveControl = new FlyingMoveHelper(this);
            this.navigation = new FlyingPathNavigation(this, level);
            ModBBAnimations.animation(this, "healer.fly", false);
        }

        // Switch to ground mode
        if (this.isFlying() && !ModUtils.isAirBelow(level, blockPosition(), 4) && this.timeSinceNoTarget > 200) {
            this.setFlying(false);
            this.goalSelector.removeGoal(flyingAI);
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, level);
            ModBBAnimations.animation(this, "healer.fly", true);
            ModBBAnimations.animation(this, "healer.fold_wing", false);
        }

        if (this.getTarget() == null) {
            this.timeSinceNoTarget++;
        }

        if (random.nextInt(20) == 0) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }
    }

    @Override
    public void setTarget(LivingEntity entity) {
        super.setTarget(entity);
        if (entity != null) {
            this.timeSinceNoTarget = 0;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            if (this.getElement().equals(Element.NONE)) {
                ParticleManager.spawnMaelstromPotionParticle(level, random, this.position().add(ModRandom.randVec()).add(ModUtils.yVec(1)), false);
            }

            ParticleManager.spawnEffect(level, this.position().add(ModRandom.randVec()).add(ModUtils.yVec(1)), getElement().particleColor);
        } else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            ParticleManager.spawnSwirl2(level, this.position(), ModColors.PURPLE, Vec3.ZERO);
        }
        super.handleEntityEvent(id);
    }

    public Vec3 getAcceleration() {
        return this.isFlying() ? this.acceleration : Vec3.ZERO;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.isFlying()) {
            if (this.isInWater()) {
                this.moveRelative(strafe, vertical, forward, 0.02F);
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.800000011920929D;
                this.motionY *= 0.800000011920929D;
                this.motionZ *= 0.800000011920929D;
            } else if (this.isInLava()) {
                this.moveRelative(strafe, vertical, forward, 0.02F);
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            } else {
                float f = 0.91F;

                if (this.onGround) {
                    BlockPos underPos = new BlockPos(Mth.floor(this.posX), Mth.floor(this.getBoundingBox().minY) - 1, Mth.floor(this.posZ));
                    BlockState underState = this.world.getBlockState(underPos);
                    f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
                }

                float f1 = 0.16277136F / (f * f * f);
                this.moveRelative(strafe, vertical, forward, this.onGround ? 0.1F * f1 : 0.02F);
                f = 0.91F;

                if (this.onGround) {
                    BlockPos underPos = new BlockPos(Mth.floor(this.posX), Mth.floor(this.getBoundingBox().minY) - 1, Mth.floor(this.posZ));
                    BlockState underState = this.world.getBlockState(underPos);
                    f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
                }

                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= f;
                this.motionY *= f;
                this.motionZ *= f;
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d1 = this.posX - this.prevPosX;
            double d0 = this.posZ - this.prevPosZ;
            float f2 = Mth.sqrt(d1 * d1 + d0 * d0) * 4.0F;

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    protected void setFlying(boolean immovable) {
        this.entityData.set(FLYING, immovable);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, Boolean.valueOf(false));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ModBBAnimations.animation(this, "healer.summon_orb", false);
        this.addEvent(() -> {
            EntityHealerOrb orb = new EntityHealerOrb(level, this, target);
            level.addFreshEntity(orb);
            level.broadcastEntityEvent(this, ModUtils.SECOND_PARTICLE_BYTE);
        }, 10);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTITY_SHADE_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTITY_SHADE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTITY_SHADE_HURT.get();
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean isOnLadder() {
        return false;
    }
}
