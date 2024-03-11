package com.barribob.mm.entity.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.barribob.mm.entity.action.IAction;
import com.barribob.mm.entity.ai.EntityAIRangedAttackNoReset;
import com.barribob.mm.entity.animation.AnimationClip;
import com.barribob.mm.entity.animation.StreamAnimation;
import com.barribob.mm.entity.model.ModelBeast;
import com.barribob.mm.entity.projectile.ProjectileBeastAttack;
import com.barribob.mm.entity.util.ComboAttack;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LootTableHandler;
import com.barribob.mm.util.handlers.SoundsHandler;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

public class EntityBeast extends EntityMaelstromMob {
    private ComboAttack attackHandler = new ComboAttack();
    private byte leap = 4;
    private byte spit = 5;

    // Responsible for the boss bar
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.NOTCHED_20));

    public EntityBeast(EntityType<? extends EntityMaelstromMob> type, Level worldIn) {
        super(type, worldIn);
        this.healthScaledAttackFactor = 0.2;
        if (!worldIn.isClientSide) {
            attackHandler.setAttack(leap, (IAction) (actor, target) -> ModUtils.leapTowards(actor, target.position(), 1.0f, 0.5f));
            attackHandler.setAttack(spit, (IAction) (actor, target) -> {
                for (int i = 0; i < 5; i++) {
                    ProjectileBeastAttack projectile = new ProjectileBeastAttack(actor.level, actor, actor.getAttack() * getConfigFloat("spit_damage"));
                    double d0 = target.getY() + target.getEyeHeight();
                    double d1 = target.getX() - actor.getX();
                    double d2 = d0 - projectile.getY();
                    double d3 = target.getZ() - actor.getZ();
                    float f = (float) (Math.sqrt(d1 * d1 + d3 * d3) * 0.2F);
                    projectile.setElement(getElement());
                    projectile.shoot(d1, d2 + f, d3, 1, 8);
                    actor.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F / (actor.getRandom().nextFloat() * 0.4F + 0.8F));
                    actor.level.addFreshEntity(projectile);
                }
            });
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
        List<List<AnimationClip<ModelBeast>>> animationLeap = new ArrayList<List<AnimationClip<ModelBeast>>>();
        List<AnimationClip<ModelBeast>> head = new ArrayList<AnimationClip<ModelBeast>>();

        BiConsumer<ModelBeast, Float> headZ = (model, f) -> {
            model.head.rotateAngleZ = f;
            model.jaw.rotateAngleX = f / 2;
        };

        head.add(new AnimationClip<>(20, 0, 40, headZ));
        head.add(new AnimationClip<>(8, 40, 40, headZ));
        head.add(new AnimationClip<>(12, 40, 0, headZ));

        animationLeap.add(head);

        List<List<AnimationClip<ModelBeast>>> animationSpit = new ArrayList<List<AnimationClip<ModelBeast>>>();
        List<AnimationClip<ModelBeast>> jaw = new ArrayList<AnimationClip<ModelBeast>>();

        BiConsumer<ModelBeast, Float> jawX = (model, f) -> {
            model.head.rotateAngleZ = 0;
            model.jaw.rotateAngleX = f;
        };

        jaw.add(new AnimationClip<>(20, 0, 20, jawX));
        jaw.add(new AnimationClip<>(8, 20, 20, jawX));
        jaw.add(new AnimationClip<>(12, 20, 0, jawX));

        animationSpit.add(jaw);

        attackHandler.setAttack(leap, IAction.NONE, () -> new StreamAnimation<>(animationLeap));
        attackHandler.setAttack(spit, IAction.NONE, () -> new StreamAnimation<>(animationSpit));

        this.currentAnimation = new StreamAnimation<>(animationSpit);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new EntityAIRangedAttackNoReset<EntityMaelstromMob>(this, 1.0f, 40, 24, 8.0f, 0.5f));
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0D);
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.95f;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.attackHandler.getCurrentAttackAction().performAction(this, target);
        if (attackHandler.getCurrentAttack() == leap) {
            setLeaping(true);
        }
    }

    @Override
    public void aiStep() {
        if (!level.isClientSide && this.isLeaping()) {
            ModUtils.handleAreaImpact(2.5f, (e) -> this.getAttack() * getConfigFloat("leap_damage"), this,
                    this.position(), ModDamageSource.causeElementalMeleeDamage(this, getElement()), 0.3f, 0, false);
        }
        super.aiStep();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }

        super.readAdditionalSaveData(compound);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void customServerAiStep() {
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        super.customServerAiStep();
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
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTITY_BEAST_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTITY_BEAST_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTITY_BEAST_HURT.get();
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        if (this.getElement() == Element.CRIMSON) {
            return LootTableHandler.CRIMSON_MINIBOSS;
        }
        return LootTableHandler.SWAMP_BOSS;
    }

    @Override
    protected float getSoundVolume() {
        return 0.5f;
    }

    @Override
    public void setAggressive(boolean swingingArms) {
        super.setAggressive(swingingArms);
        if (this.isSwingingArms()) {
            attackHandler.setCurrentAttack(ModRandom.choice(new Byte[]{leap, spit}));
            level.broadcastEntityEvent(this, attackHandler.getCurrentAttack());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 4 || id == 5) {
            currentAnimation = attackHandler.getAnimation(id);
            getCurrentAnimation().startAnimation();
        }
        super.handleEntityEvent(id);
    }

    @Override
    public void onStopLeaping() {
    }
}
