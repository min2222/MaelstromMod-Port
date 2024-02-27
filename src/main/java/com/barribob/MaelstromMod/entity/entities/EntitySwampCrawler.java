package com.barribob.MaelstromMod.entity.entities;

import com.barribob.MaelstromMod.entity.ai.AIMeleeAndRange;
import com.barribob.MaelstromMod.entity.animation.AnimationOpenJaws;
import com.barribob.MaelstromMod.entity.projectile.ProjectileSwampSpittle;
import com.barribob.MaelstromMod.init.ModItems;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.LevelHandler;
import com.barribob.MaelstromMod.util.handlers.SoundsHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    protected void initEntityAI() {
        this.tasks.addTask(0, new FloatGoal(this));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.5F) {
            @Override
            public boolean shouldExecute() {
                return super.shouldExecute() && attackAI != null && !attackAI.isRanged();
            }

            @Override
            public void startExecuting() {
                world.setEntityState(EntitySwampCrawler.this, (byte) 4);
                super.startExecuting();
            }
        });
        this.tasks.addTask(6, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, Player.class, 6.0F));
        this.tasks.addTask(8, new RandomLookAroundGoal(this));
        this.targetTasks.addTask(2, new HurtByTargetGoal(this, false, new Class[0]));
        this.targetTasks.addTask(3, new NearestAttackableTargetGoal(this, Player.class, true));
        attackAI = new AIMeleeAndRange<EntitySwampCrawler>(this, 1.0f, true, 1.0f, 30, 8.0f, 100, 1.0f, 0.1f);
        this.tasks.addTask(4, attackAI);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttack());
            return true;
        }
        return false;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.30D);
        this.getEntityAttribute(SWIM_SPEED).setBaseValue(1.0D);
        this.getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(5);
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(25);
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.95f;
    }

    @Override
    public float getEyeHeight() {
        return 0.65F;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_HOSTILE_SPLASH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTTIY_CRAWLER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTTIY_CRAWLER_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTTIY_CRAWLER_AMBIENT;
    }

    @Override
    protected Item getDropItem() {
        return ModItems.SWAMP_SLIME;
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && super.getCanSpawnHere();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.setDead();
        }
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        ModUtils.throwProjectile(this, target, new ProjectileSwampSpittle(world, this, this.getAttack()));
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        if (swingingArms) {
            this.world.setEntityState(this, (byte) 4);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            getCurrentAnimation().startAnimation();
        } else {
            super.handleStatusUpdate(id);
        }
    }
}
