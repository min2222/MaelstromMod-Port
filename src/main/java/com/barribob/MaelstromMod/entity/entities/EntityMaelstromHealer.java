package com.barribob.MaelstromMod.entity.entities;

import com.barribob.MaelstromMod.entity.ai.AIFlyingSupport;
import com.barribob.MaelstromMod.entity.ai.FlyingMoveHelper;
import com.barribob.MaelstromMod.entity.projectile.EntityHealerOrb;
import com.barribob.MaelstromMod.entity.util.IAcceleration;
import com.barribob.MaelstromMod.init.ModBBAnimations;
import com.barribob.MaelstromMod.util.Element;
import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import com.barribob.MaelstromMod.util.handlers.SoundsHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMaelstromHealer extends EntityMaelstromMob implements IAcceleration {
    Vec3 acceleration = Vec3.ZERO;
    protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.<Boolean>createKey(EntityMaelstromHealer.class, EntityDataSerializers.BOOLEAN);
    private EntityAIBase flyingAI = new AIFlyingSupport(this, 1.2f, 3.5f, 10f, 60);
    private float timeSinceNoTarget = 0;

    public EntityMaelstromHealer(Level worldIn) {
        super(worldIn);
        this.setSize(0.9f, 2.0f);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        Vec3 motion = new Vec3(this.motionX, this.motionY, this.motionZ);
        this.acceleration = motion.scale(0.1).add(this.acceleration.scale(0.9));

        if (this.world.isRemote) {
            return;
        }

        // Switch to flying mode
        if (!this.isFlying() && ((!this.onGround && ModUtils.isAirBelow(world, getPosition(), 4)) || this.getAttackTarget() != null)) {
            this.setFlying(true);
            this.tasks.addTask(4, flyingAI);
            this.moveHelper = new FlyingMoveHelper(this);
            this.navigator = new PathNavigateFlying(this, world);
            ModBBAnimations.animation(this, "healer.fly", false);
        }

        // Switch to ground mode
        if (this.isFlying() && !ModUtils.isAirBelow(world, getPosition(), 4) && this.timeSinceNoTarget > 200) {
            this.setFlying(false);
            this.tasks.removeTask(flyingAI);
            this.moveHelper = new EntityMoveHelper(this);
            this.navigator = new PathNavigateGround(this, world);
            ModBBAnimations.animation(this, "healer.fly", true);
            ModBBAnimations.animation(this, "healer.fold_wing", false);
        }

        if (this.getAttackTarget() == null) {
            this.timeSinceNoTarget++;
        }

        if (rand.nextInt(20) == 0) {
            world.setEntityState(this, ModUtils.PARTICLE_BYTE);
        }
    }

    @Override
    public void setAttackTarget(LivingEntity entity) {
        super.setAttackTarget(entity);
        if (entity != null) {
            this.timeSinceNoTarget = 0;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            if (this.getElement().equals(Element.NONE)) {
                ParticleManager.spawnMaelstromPotionParticle(world, rand, this.getPositionVector().add(ModRandom.randVec()).add(ModUtils.yVec(1)), false);
            }

            ParticleManager.spawnEffect(world, this.getPositionVector().add(ModRandom.randVec()).add(ModUtils.yVec(1)), getElement().particleColor);
        } else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            ParticleManager.spawnSwirl2(world, this.getPositionVector(), ModColors.PURPLE, Vec3.ZERO);
        }
        super.handleStatusUpdate(id);
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
                    BlockPos underPos = new BlockPos(Mth.floor(this.posX), Mth.floor(this.getEntityBoundingBox().minY) - 1, Mth.floor(this.posZ));
                    BlockState underState = this.world.getBlockState(underPos);
                    f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.91F;
                }

                float f1 = 0.16277136F / (f * f * f);
                this.moveRelative(strafe, vertical, forward, this.onGround ? 0.1F * f1 : 0.02F);
                f = 0.91F;

                if (this.onGround) {
                    BlockPos underPos = new BlockPos(Mth.floor(this.posX), Mth.floor(this.getEntityBoundingBox().minY) - 1, Mth.floor(this.posZ));
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
        return this.dataManager == null ? false : this.dataManager.get(FLYING);
    }

    protected void setFlying(boolean immovable) {
        this.dataManager.set(FLYING, immovable);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FLYING, Boolean.valueOf(false));
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        ModBBAnimations.animation(this, "healer.summon_orb", false);
        this.addEvent(() -> {
            EntityHealerOrb orb = new EntityHealerOrb(world, this, target);
            world.spawnEntity(orb);
            world.setEntityState(this, ModUtils.SECOND_PARTICLE_BYTE);
        }, 10);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTITY_SHADE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTITY_SHADE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTITY_SHADE_HURT;
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
