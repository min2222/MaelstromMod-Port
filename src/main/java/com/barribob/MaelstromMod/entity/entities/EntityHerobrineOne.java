package com.barribob.MaelstromMod.entity.entities;

import com.barribob.MaelstromMod.entity.action.*;
import com.barribob.MaelstromMod.entity.ai.EntityAIRangedAttack;
import com.barribob.MaelstromMod.entity.animation.AnimationFireballThrow;
import com.barribob.MaelstromMod.entity.animation.AnimationHerobrineGroundSlash;
import com.barribob.MaelstromMod.entity.animation.AnimationSpinSlash;
import com.barribob.MaelstromMod.entity.projectile.ProjectileFireball;
import com.barribob.MaelstromMod.entity.projectile.ProjectileHerobrineQuake;
import com.barribob.MaelstromMod.entity.util.ComboAttack;
import com.barribob.MaelstromMod.init.ModItems;
import com.barribob.MaelstromMod.util.ModColors;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class EntityHerobrineOne extends EntityLeveledMob implements RangedAttackMob {
    // Swinging arms is the animation for the attack
    private static final EntityDataAccessor<Boolean> SWINGING_ARMS = SynchedEntityData.<Boolean>createKey(EntityLeveledMob.class, EntityDataSerializers.BOOLEAN);
    private ComboAttack attackHandler = new ComboAttack();
    private byte passiveParticleByte = 7;
    private int maxHits = 3;
    private int hits = 5;
    private byte deathParticleByte = 8;
    public static final byte slashParticleByte = 9;
    private byte fireballParticleByte = 10;
    private boolean markedToDespawn = false;
    private byte spinSlash = 4;
    private byte groundSlash = 5;
    private byte fireball = 6;

    public EntityHerobrineOne(Level worldIn) {
        super(worldIn);
        this.healthScaledAttackFactor = 0.2;
        this.setSize(0.8f, 2.0f);
        if (!world.isRemote) {
            attackHandler.setAttack(spinSlash, new ActionSpinSlash());
            attackHandler.setAttack(groundSlash, new ActionGroundSlash(() -> new ProjectileHerobrineQuake(world, this, this.getAttack())));
            attackHandler.setAttack(fireball, new IAction() {
                @Override
                public void performAction(EntityLeveledMob actor, LivingEntity target) {
                    actor.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.4F / (actor.world.rand.nextFloat() * 0.4F + 0.8F));

                    ProjectileFireball projectile = new ProjectileFireball(actor.world, actor, actor.getAttack(), null);
                    ModUtils.throwProjectile(actor, target, projectile, 2.0f, 0.5f, ModUtils.yVec(0.5f));
                }
            });
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void initAnimation() {
        attackHandler.setAttack(spinSlash, new ActionSpinSlash(), () -> new AnimationSpinSlash());
        attackHandler.setAttack(groundSlash, new ActionGroundSlash(() -> new ProjectileHerobrineQuake(world, this, this.getAttack())),
                () -> new AnimationHerobrineGroundSlash());
        attackHandler.setAttack(fireball, new ActionFireball(), () -> new AnimationFireballThrow());
        this.currentAnimation = new AnimationSpinSlash();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(4, new EntityAIRangedAttack<EntityHerobrineOne>(this, 1.0f, 40, 10.0f, 0.2f));
        this.tasks.addTask(1, new FloatGoal(this));
        this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(6, new RandomLookAroundGoal(this));
        this.targetTasks.addTask(1, new HurtByTargetGoal(this, false, new Class[0]));
        this.targetTasks.addTask(2, new NearestAttackableTargetGoal(this, Player.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
        this.getEntityAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30.0D);
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(30);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (hits == 0) {
            hits = maxHits;
            return super.attackEntityFrom(source, amount);
        } else if (source.getTrueSource() instanceof LivingEntity) {
            new ActionTeleport().performAction(this, (LivingEntity) source.getTrueSource());
            this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F + ModRandom.getFloat(0.2f));
            this.setRevengeTarget((LivingEntity) source.getTrueSource());

            hits--;
        }

        return false;
    }

    @Override
    public void onDeath(DamageSource cause) {
        world.setEntityState(this, this.deathParticleByte);
        this.setPosition(0, 0, 0);
        this.setDead();
        super.onDeath(cause);
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        this.attackHandler.getCurrentAttackAction().performAction(this, target);
    }

    /**
     * Gives armor or weapon for entity based on given DifficultyInstance
     */
    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        this.setItemStackToSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SWORD_OF_SHADES));
        this.setItemStackToSlot(EquipmentSlot.OFFHAND, new ItemStack(ModItems.SWORD_OF_SHADES));
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner,
     * natural spawning etc, but not called when entity is reloaded from nbt. Mainly
     * used for initializing attributes and inventory
     */
    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        IEntityLivingData ientitylivingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        return ientitylivingdata;
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        this.dataManager.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
        if (swingingArms) {
            float distance = (float) this.getDistanceSq(this.getAttackTarget().posX, getAttackTarget().getEntityBoundingBox().minY, getAttackTarget().posZ);
            float melee_distance = 4;

            if (distance > Math.pow(melee_distance, 2)) {
                attackHandler.setCurrentAttack(rand.nextInt(2) == 0 ? fireball : groundSlash);
            } else {
                attackHandler.setCurrentAttack(spinSlash);

                if (this.getAttackTarget() != null) {
                    Vec3 dir = getAttackTarget().getPositionVector().subtract(getPositionVector()).normalize();
                    Vec3 leap = new Vec3(dir.x, 0, dir.z).normalize().scale(0.4f).add(ModUtils.yVec(0.3f));
                    this.motionX += leap.x;
                    if (this.motionY < 0.1) {
                        this.motionY += leap.y;
                    }
                    this.motionZ += leap.z;
                }
            }

            this.world.setEntityState(this, attackHandler.getCurrentAttack());

            if (attackHandler.getCurrentAttack() == fireball) {
                this.motionY = 0.7f;
                this.fallDistance = -4;
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.markedToDespawn) {
            this.setDead();
        }

        if (!this.world.isRemote && this.isSwingingArms() && attackHandler.getCurrentAttack() == fireball) {
            this.world.setEntityState(this, this.fireballParticleByte);
        } else {
            this.world.setEntityState(this, this.passiveParticleByte);
        }
    }

    /**
     * Handler for {@link Level#setEntityState}
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id >= 4 && id <= 6) {
            currentAnimation = attackHandler.getAnimation(id);
            getCurrentAnimation().startAnimation();
        } else if (id == this.passiveParticleByte) {
            if (rand.nextInt(2) == 0) {
                ParticleManager.spawnEffect(world, ModUtils.entityPos(this).add(ModRandom.randVec().scale(1.5f)).add(new Vec3(0, 1, 0)), ModColors.AZURE);
            }
        } else if (id == this.deathParticleByte) {
            int particleAmount = 100;
            for (int i = 0; i < particleAmount; i++) {
                ParticleManager.spawnEffect(this.world, ModUtils.entityPos(this).add(ModRandom.randVec().scale(2f)).add(new Vec3(0, 1, 0)), ModColors.AZURE);
            }
        } else if (id == this.fireballParticleByte) {
            int fireballParticles = 5;
            for (int i = 0; i < fireballParticles; i++) {
                Vec3 pos = new Vec3(ModRandom.getFloat(0.5f), this.getEyeHeight() + 1.0f, ModRandom.getFloat(0.5f)).add(ModUtils.entityPos(this));
                ParticleManager.spawnCustomSmoke(world, pos, ProjectileFireball.FIREBALL_COLOR, Vec3.ZERO);
            }
        } else if (id == this.slashParticleByte) {
            Vec3 color = new Vec3(0.5, 0.2, 0.3);
            float particleHeight = 1.2f;
            for (float r = 0.5f; r <= 2; r += 0.5f) {
                for (float sector = 0; sector < 360; sector += 10) {
                    Vec3 pos = new Vec3(Math.cos(sector) * r, particleHeight, Math.sin(sector) * r).add(ModUtils.entityPos(this));
                    ParticleManager.spawnEffect(world, pos, ModColors.AZURE);
                }
            }
        } else {
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void readEntityFromNBT(CompoundTag compound) {
        this.markedToDespawn = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
    }

    public boolean isSwingingArms() {
        return this.dataManager.get(SWINGING_ARMS).booleanValue();
    }
}
