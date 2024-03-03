package com.barribob.mm.entity.entities;

import com.barribob.mm.entity.ai.AIMeleeAndRange;
import com.barribob.mm.entity.animation.AnimationOpenJaws;
import com.barribob.mm.entity.projectile.ProjectileSwampSpittle;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.entity.monster.IMob;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

public class EntitySwampCrawler extends EntityLeveledMob implements RangedAttackMob, IMob {
    private AIMeleeAndRange attackAI;

    public EntitySwampCrawler(Level worldIn) {
        super(worldIn);
        this.setSize(1.5f, 1.3f);
        this.setLevel(LevelHandler.CLIFF_OVERWORLD);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
        this.currentAnimation = new AnimationOpenJaws();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.5F) {
            @Override
            public boolean canUse() {
                return super.canUse() && attackAI != null && !attackAI.isRanged();
            }

            @Override
            public void start() {
                level.broadcastEntityEvent(EntitySwampCrawler.this, (byte) 4);
                super.start();
            }
        });
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        attackAI = new AIMeleeAndRange<EntitySwampCrawler>(this, 1.0f, true, 1.0f, 30, 8.0f, 100, 1.0f, 0.1f);
        this.goalSelector.addGoal(4, attackAI);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            entityIn.hurt(DamageSource.mobAttack(this), this.getAttack());
            return true;
        }
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.30D);
        this.getAttribute(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0D);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(25);
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.95f;
    }

    @Override
    public float getEyeHeight(Pose pose) {
        return 0.65F;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTTIY_CRAWLER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTTIY_CRAWLER_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTTIY_CRAWLER_AMBIENT.get();
    }

    @Override
    protected Item getDropItem() {
        return ModItems.SWAMP_SLIME;
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.level.getDifficulty() != Difficulty.PEACEFUL && super.getCanSpawnHere();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ModUtils.throwProjectile(this, target, new ProjectileSwampSpittle(level, this, this.getAttack()));
    }

    @Override
    public void setAggressive(boolean swingingArms) {
        if (swingingArms) {
            this.level.broadcastEntityEvent(this, (byte) 4);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            getCurrentAnimation().startAnimation();
        } else {
            super.handleEntityEvent(id);
        }
    }
}
