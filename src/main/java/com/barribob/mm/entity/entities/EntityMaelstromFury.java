package com.barribob.mm.entity.entities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

import com.barribob.mm.Main;
import com.barribob.mm.entity.ai.AIFuryDive;
import com.barribob.mm.entity.ai.AIPassiveCircle;
import com.barribob.mm.entity.ai.AIRandomFly;
import com.barribob.mm.entity.ai.FlyingMoveHelper;
import com.barribob.mm.entity.util.IAcceleration;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.packets.MessageModParticles;
import com.barribob.mm.particle.EnumModParticles;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.SoundsHandler;

public class EntityMaelstromFury extends EntityMaelstromMob implements IAcceleration {
    Vec3 acceleration = Vec3.ZERO;
    public EntityMaelstromFury(Level worldIn) {
        super(worldIn);
        this.moveHelper = new FlyingMoveHelper(this);
        this.navigator = new PathNavigateFlying(this, worldIn);
        if(!worldIn.isRemote) {
            ModBBAnimations.animation(this, "fury.fly", false);
        }
        this.setSize(1.2f, 1.2f);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        Vec3 prevAcceleration = acceleration;
        acceleration = ModUtils.getEntityVelocity(this).scale(0.1).add(this.acceleration.scale(0.9));

        if (!level.isClientSide) {
            if(prevAcceleration.y > 0 && acceleration.y <= 0) {
                ModBBAnimations.animation(this, "fury.fly", true);
            }
            else if (prevAcceleration.y <= 0 && acceleration.y > 0) {
                ModBBAnimations.animation(this, "fury.fly", false);
            }
        }
    }

    public Vec3 getAcceleration() {
        return acceleration;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(4, new AIRandomFly(this));
        this.tasks.addTask(3, new AIPassiveCircle<>(this, 30));
        this.tasks.addTask(2, new AIFuryDive(100, 5 * 20, this, this::onDiveStart, this::onDiveEnd, this::whileDiving));
        super.initEntityAI();
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        ModUtils.aerialTravel(this, strafe, vertical, forward);
    }

    private void onDiveStart() {
        playSound(SoundEvents.VEX_CHARGE, 7.0f, 1.7f);
        ModBBAnimations.animation(this, "fury.dive", false);
    }

    private void whileDiving() {
        Vec3 entityVelocity = ModUtils.getEntityVelocity(this);
        Vec3 spearPos = ModUtils.getAxisOffset(entityVelocity.normalize(), ModUtils.X_AXIS.scale(1.7)).add(position());
        DamageSource damageSource = ModDamageSource.builder()
                .type(ModDamageSource.MOB)
                .disablesShields()
                .directEntity(this)
                .element(getElement())
                .build();
        float velocity = (float) entityVelocity.lengthVector();
        ModUtils.handleAreaImpact(0.7f, e -> getAttack() * velocity * 2, this, spearPos, damageSource, 0.5f, 0);
        Main.network.sendToAllTracking(new MessageModParticles(EnumModParticles.EFFECT, spearPos, entityVelocity, ModColors.PURPLE), this);
    }

    private void onDiveEnd() {
        ModBBAnimations.animation(this, "fury.dive", true);
        ModBBAnimations.animation(this, "fury.undive", false);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, @Nonnull BlockState state, @Nonnull BlockPos pos) {
    }

    @Override
    public boolean isOnLadder() {
        return false;
    }

    @Override
    public void performRangedAttack(@Nonnull LivingEntity target, float distanceFactor) {
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.getBoundingBox().grow(targetDistance);
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
    protected boolean canDespawn() {
         return this.tickCount > 20 * getMobConfig().getInt("seconds_existed_to_be_able_to_despawn");
    }
}
