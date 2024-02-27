package com.barribob.MaelstromMod.entity.entities;

import com.barribob.MaelstromMod.util.handlers.LevelHandler;
import com.barribob.MaelstromMod.util.handlers.LootTableHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityDreamElk extends EntityLeveledMob {
    /**
     * Timers for animation
     */
    private int attackTimer;
    private int eatGrassTimer;
    private EntityAIEatGrass grassAI;

    public EntityDreamElk(Level worldIn) {
        super(worldIn);
        this.setSize(1.3964844F, 1.6F);
        this.setLevel(LevelHandler.AZURE_OVERWORLD);
    }

    @Override
    protected void initEntityAI() {
        grassAI = new EntityAIEatGrass(this);
        ;
        this.tasks.addTask(0, new FloatGoal(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
        this.tasks.addTask(5, grassAI);
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 0.6D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, Player.class, 6.0F));
        this.tasks.addTask(8, new RandomLookAroundGoal(this));
        this.targetTasks.addTask(2, new HurtByTargetGoal(this, false, new Class[0]));
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
    protected void updateAITasks() {
        this.eatGrassTimer = this.grassAI.getEatingGrassTimer();
        super.updateAITasks();
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableHandler.ELK;
    }

    @Override
    public void swingArm(InteractionHand hand) {
    }

    /**
     * Called frequently so the entity can update its state every tick as required.
     * For example, zombies and skeletons use this to react to sunlight and start to
     * burn.
     */
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.attackTimer > 0) {
            --this.attackTimer;
        }

        if (this.world.isRemote) {
            this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
        }
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    @Override
    public boolean getCanSpawnHere() {
        int i = Mth.floor(this.posX);
        int j = Mth.floor(this.getEntityBoundingBox().minY);
        int k = Mth.floor(this.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        return this.world.getBlockState(blockpos.down()).getBlock() == Blocks.GRASS && this.world.getLight(blockpos) > 8 && super.getCanSpawnHere();
    }

    /**
     * Handler for {@link Level#setEntityState}
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 4) {
            this.attackTimer = 10;
            this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
        }
        if (id == 10) {
            this.eatGrassTimer = 40;
        } else {
            super.handleStatusUpdate(id);
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
    public boolean attackEntityAsMob(Entity entityIn) {
        this.world.setEntityState(this, (byte) 4);
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttack());

        if (flag) {
            entityIn.motionY += 0.35D;
            this.applyEnchantments(this, entityIn);
        }

        this.playSound(SoundEvents.ENTITY_IRONGOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected float getSoundVolume() {
        return 0.5f;
    }
}
