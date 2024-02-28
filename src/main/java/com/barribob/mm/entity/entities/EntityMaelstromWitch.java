package com.barribob.mm.entity.entities;

import com.barribob.mm.entity.action.ActionDarkMissile;
import com.barribob.mm.entity.action.ActionThrowPotion;
import com.barribob.mm.entity.ai.EntityAIRangedAttack;
import com.barribob.mm.entity.animation.AnimationWitchFlail;
import com.barribob.mm.entity.util.ComboAttack;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.LootTableHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMaelstromWitch extends EntityMaelstromMob {
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.NOTCHED_12));
    private ComboAttack attackHandler = new ComboAttack();
    private byte lingeringPotions = 4;
    private byte rapidPotions = 5;
    private byte throwWood = 6;
    private EntityAIRangedAttack rangedAttack;
    private EntityAIRangedAttack rageRangedAttack;
    private float threshold;
    private boolean isRaged = false;

    public EntityMaelstromWitch(Level worldIn) {
        super(worldIn);
        threshold = this.getMaxHealth() * 0.3f;
        this.healthScaledAttackFactor = 0.2;
        this.setSize(0.9f, 1.8f);
        if (!worldIn.isClientSide) {
            attackHandler.setAttack(lingeringPotions, new ActionThrowPotion(Items.LINGERING_POTION));
            attackHandler.setAttack(rapidPotions, new ActionThrowPotion(Items.SPLASH_POTION));
            attackHandler.setAttack(throwWood, new ActionDarkMissile());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
        attackHandler.setAttack(lingeringPotions, new ActionThrowPotion(Items.LINGERING_POTION), () -> new AnimationWitchFlail());
        attackHandler.setAttack(rapidPotions, new ActionThrowPotion(Items.SPLASH_POTION), () -> new AnimationWitchFlail());
        attackHandler.setAttack(throwWood, new ActionDarkMissile(), () -> new AnimationWitchFlail());
        this.currentAnimation = new AnimationWitchFlail();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        rangedAttack = new EntityAIRangedAttack<EntityMaelstromWitch>(this, 1.0f, 60, 30, 12.0f, 0.4f);
        rageRangedAttack = new EntityAIRangedAttack<EntityMaelstromWitch>(this, 1.0f, 35, 30, 12.0f, 0.4f);
        this.goalSelector.addGoal(4, rangedAttack);
    }

    @Override
    public void tick() {
        super.tick();
        bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.isSwingingArms() && this.tickCount % 3 == 0 && this.getTarget() != null) {
            attackHandler.getCurrentAttackAction().performAction(this, this.getTarget());
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.isMagic()) {
            return false;
        }

        float prevHealth = this.getHealth();
        boolean flag = super.hurt(source, amount);
        if (prevHealth > threshold && this.getHealth() < threshold && rangedAttack != null) {
            this.goalSelector.removeGoal(rangedAttack);
            this.goalSelector.addGoal(4, rageRangedAttack);
            this.isRaged = true;
        }
        return flag;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        super.setSwingingArms(swingingArms);
        if (swingingArms) {
            Byte[] attack = {lingeringPotions, rapidPotions, throwWood};
            attackHandler.setCurrentAttack(ModRandom.choice(attack));
            level.broadcastEntityEvent(this, attackHandler.getCurrentAttack());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id >= 4 && id <= 6) {
            currentAnimation = attackHandler.getAnimation(id);
            getCurrentAnimation().startAnimation();
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.WITCH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return LootTableHandler.SWAMP_BOSS;
    }

    @Override
	public float getVoicePitch() {
        return 0.6f;
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean save(CompoundTag compound) {
        compound.putBoolean("raged", this.isRaged);
        return super.save(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("raged")) {
            this.isRaged = compound.getBoolean("raged");
            if (isRaged && rangedAttack != null) {
                this.goalSelector.removeGoal(rangedAttack);
                this.goalSelector.addGoal(4, rageRangedAttack);
            }
        }
    }
}
