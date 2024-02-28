package com.barribob.mm.entity.entities;

import com.barribob.mm.util.handlers.LevelHandler;
import com.barribob.mm.util.handlers.LootTableHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityDreamElk extends EntityLeveledMob {
    /**
     * Timers for animation
     */
    private int attackTimer;
    private int eatGrassTimer;
    private EatBlockGoal grassAI;

    public EntityDreamElk(Level worldIn) {
        super(worldIn);
        this.setSize(1.3964844F, 1.6F);
        this.setLevel(LevelHandler.AZURE_OVERWORLD);
    }

    @Override
    protected void registerGoals() {
        grassAI = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
        this.goalSelector.addGoal(5, grassAI);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.30D);
        this.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        this.getEntityAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6);
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(25);
    }

    @Override
    protected void customServerAiStep() {
        this.eatGrassTimer = this.grassAI.getEatAnimationTick();
        super.customServerAiStep();
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return LootTableHandler.ELK;
    }

    @Override
    public void swing(InteractionHand hand) {
    }

    /**
     * Called frequently so the entity can update its state every tick as required.
     * For example, zombies and skeletons use this to react to sunlight and start to
     * burn.
     */
    @Override
    public void aiStep() {
        super.aiStep();

        if (this.attackTimer > 0) {
            --this.attackTimer;
        }

        if (this.level.isClientSide) {
            this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    @Override
    public boolean getCanSpawnHere() {
        int i = Mth.floor(this.posX);
        int j = Mth.floor(this.getBoundingBox().minY);
        int k = Mth.floor(this.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        return this.world.getBlockState(blockpos.down()).getBlock() == Blocks.GRASS && this.world.getLight(blockpos) > 8 && super.getCanSpawnHere();
    }

    /**
     * Handler for {@link Level#setEntityState}
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 4) {
            this.attackTimer = 10;
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        }
        if (id == 10) {
            this.eatGrassTimer = 40;
        } else {
            super.handleEntityEvent(id);
        }
    }

    /**
     * Handles the animations of the neck (bucking and eating grass)
     *
     * @param partialTickTime
     * @return
     */
    @OnlyIn(Dist.CLIENT)
    public float getHeadRotationAngleX(float partialTickTime) {
        if (this.attackTimer > 0) {
            return 0.6f * this.triangleWave(this.attackTimer - partialTickTime, 10.0F);
        } else if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
            float f = (this.eatGrassTimer - 4 - partialTickTime) / 32.0F;
            return ((float) Math.PI / 5F) + ((float) Math.PI * 7F / 100F) * Mth.sin(f * 28.7F);
        } else {
            return 0;
        }
    }

    /**
     * Taken from the iron golem animation to make the bucking animation
     *
     * @param x
     * @param f
     * @return
     */
    private float triangleWave(float x, float f) {
        return (Math.abs(x % f - f * 0.5F) - f * 0.25F) / (f * 0.25F);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn) {
        this.level.broadcastEntityEvent(this, (byte) 4);
        boolean flag = entityIn.hurt(DamageSource.mobAttack(this), this.getAttack());

        if (flag) {
            entityIn.setDeltaMovement(entityIn.getDeltaMovement().add(0, 0.35D, 0));
            this.applyEnchantments(this, entityIn);
        }

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.HORSE_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 0.5f;
    }
}
