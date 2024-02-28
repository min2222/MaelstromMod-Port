package com.barribob.mm.entity.entities;

import javax.annotation.Nullable;

import com.barribob.mm.entity.action.ActionGeyser;
import com.barribob.mm.entity.action.ActionGolemSlam;
import com.barribob.mm.entity.ai.EntityAIRangedAttack;
import com.barribob.mm.entity.animation.AnimationAzureGolem;
import com.barribob.mm.entity.animation.AnimationGroundFistBump;
import com.barribob.mm.entity.render.RenderAzureGolem;
import com.barribob.mm.entity.util.ComboAttack;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.LootTableHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityCliffGolem extends EntityLeveledMob implements RangedAttackMob {
    private ComboAttack attackHandler = new ComboAttack();
    private byte groundPoundByte = 4;
    private byte geyserByte = 5;

    public EntityCliffGolem(Level worldIn) {
        super(worldIn);
        this.setSize(1.4F * RenderAzureGolem.AZURE_GOLEM_SIZE, 2.7F * RenderAzureGolem.AZURE_GOLEM_SIZE);
        if (!worldIn.isClientSide) {
            attackHandler.setAttack(groundPoundByte, new ActionGolemSlam());
            attackHandler.setAttack(this.geyserByte, new ActionGeyser());
        }
        this.healthScaledAttackFactor = 0.2;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
        this.currentAnimation = new AnimationAzureGolem();
        attackHandler.setAttack(groundPoundByte, new ActionGolemSlam(), () -> new AnimationAzureGolem());
        attackHandler.setAttack(this.geyserByte, new ActionGeyser(), () -> new AnimationGroundFistBump());
    }

    @Override
    public float getRenderSizeModifier() {
        return RenderAzureGolem.AZURE_GOLEM_SIZE;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAIRangedAttack<EntityCliffGolem>(this, 1f, 60, 15, 20.0f, 0.1f));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    @Override
	public float getVoicePitch() {
        return 0.9f + ModRandom.getFloat(0.1f);
    }

    @Override
    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        return LootTableHandler.SWAMP_BOSS;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.attackHandler.getCurrentAttackAction().performAction(this, target);
    }

    @Override
    public void swing(InteractionHand hand) {
    }

    @Override
    public void setAggressive(boolean swingingArms) {
        if (swingingArms) {
            double distance = this.distanceToSqr(this.getTarget().getX(), getTarget().getBoundingBox().minY, getTarget().getZ());
            double meleeDistance = 7;
            if (distance < Math.pow(meleeDistance, 2)) {
                attackHandler.setCurrentAttack(this.groundPoundByte);
            } else {
                attackHandler.setCurrentAttack(this.geyserByte);
            }

            this.level.broadcastEntityEvent(this, attackHandler.getCurrentAttack());
            this.setDeltaMovement(this.getDeltaMovement().x, 0.63F, this.getDeltaMovement().z);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == this.groundPoundByte || id == this.geyserByte) {
            this.currentAnimation = attackHandler.getAnimation(id);
            getCurrentAnimation().startAnimation();
            this.playSound(SoundEvents.ANVIL_BREAK, 1.0F, 1.0F);
        } else {
            super.handleEntityEvent(id);
        }
    }
}
