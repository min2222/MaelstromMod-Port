package com.barribob.MaelstromMod.entity.entities;

import com.barribob.MaelstromMod.entity.action.IAction;
import com.barribob.MaelstromMod.entity.ai.EntityAIRangedAttackNoReset;
import com.barribob.MaelstromMod.entity.animation.AnimationClip;
import com.barribob.MaelstromMod.entity.animation.StreamAnimation;
import com.barribob.MaelstromMod.entity.model.ModelBeast;
import com.barribob.MaelstromMod.entity.projectile.ProjectileBeastAttack;
import com.barribob.MaelstromMod.entity.util.ComboAttack;
import com.barribob.MaelstromMod.util.Element;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.handlers.LootTableHandler;
import com.barribob.MaelstromMod.util.handlers.SoundsHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class EntityBeast extends EntityMaelstromMob {
    private ComboAttack attackHandler = new ComboAttack();
    private byte leap = 4;
    private byte spit = 5;

    // Responsible for the boss bar
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossEvent.Color.PURPLE, BossEvent.Overlay.NOTCHED_20));

    public EntityBeast(Level worldIn) {
        super(worldIn);
        this.setSize(2.8f, 2.2f);
        this.healthScaledAttackFactor = 0.2;
        if (!worldIn.isRemote) {
            attackHandler.setAttack(leap, (IAction) (actor, target) -> ModUtils.leapTowards(actor, target.getPositionVector(), 1.0f, 0.5f));
            attackHandler.setAttack(spit, (IAction) (actor, target) -> {
                for (int i = 0; i < 5; i++) {
                    ProjectileBeastAttack projectile = new ProjectileBeastAttack(actor.world, actor, actor.getAttack() * getConfigFloat("spit_damage"));
                    double d0 = target.posY + target.getEyeHeight();
                    double d1 = target.posX - actor.posX;
                    double d2 = d0 - projectile.posY;
                    double d3 = target.posZ - actor.posZ;
                    float f = Mth.sqrt(d1 * d1 + d3 * d3) * 0.2F;
                    projectile.setElement(getElement());
                    projectile.shoot(d1, d2 + f, d3, 1, 8);
                    actor.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 1.0F / (actor.getRNG().nextFloat() * 0.4F + 0.8F));
                    actor.world.spawnEntity(projectile);
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

        head.add(new AnimationClip(20, 0, 40, headZ));
        head.add(new AnimationClip(8, 40, 40, headZ));
        head.add(new AnimationClip(12, 40, 0, headZ));

        animationLeap.add(head);

        List<List<AnimationClip<ModelBeast>>> animationSpit = new ArrayList<List<AnimationClip<ModelBeast>>>();
        List<AnimationClip<ModelBeast>> jaw = new ArrayList<AnimationClip<ModelBeast>>();

        BiConsumer<ModelBeast, Float> jawX = (model, f) -> {
            model.head.rotateAngleZ = 0;
            model.jaw.rotateAngleX = f;
        };

        jaw.add(new AnimationClip(20, 0, 20, jawX));
        jaw.add(new AnimationClip(8, 20, 20, jawX));
        jaw.add(new AnimationClip(12, 20, 0, jawX));

        animationSpit.add(jaw);

        attackHandler.setAttack(leap, IAction.NONE, () -> new StreamAnimation(animationLeap));
        attackHandler.setAttack(spit, IAction.NONE, () -> new StreamAnimation(animationSpit));

        this.currentAnimation = new StreamAnimation(animationSpit);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(4, new EntityAIRangedAttackNoReset<EntityMaelstromMob>(this, 1.0f, 40, 24, 8.0f, 0.5f));
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
        this.getEntityAttribute(SWIM_SPEED).setBaseValue(1.0D);
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.95f;
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        this.attackHandler.getCurrentAttackAction().performAction(this, target);
        if (attackHandler.getCurrentAttack() == leap) {
            setLeaping(true);
        }
    }

    @Override
    public void onLivingUpdate() {
        if (!world.isRemote && this.isLeaping()) {
            ModUtils.handleAreaImpact(2.5f, (e) -> this.getAttack() * getConfigFloat("leap_damage"), this,
                    this.getPositionVector(), ModDamageSource.causeElementalMeleeDamage(this, getElement()), 0.3f, 0, false);
        }
        super.onLivingUpdate();
    }

    @Override
    public void readEntityFromNBT(CompoundTag compound) {
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }

        super.readEntityFromNBT(compound);
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void updateAITasks() {
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
        super.updateAITasks();
    }

    @Override
    public void addTrackingPlayer(ServerPlayer player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(ServerPlayer player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTITY_BEAST_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTITY_BEAST_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTITY_BEAST_HURT;
    }

    @Override
    protected ResourceLocation getLootTable() {
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
    public void setSwingingArms(boolean swingingArms) {
        super.setSwingingArms(swingingArms);
        if (this.isSwingingArms()) {
            attackHandler.setCurrentAttack(ModRandom.choice(new Byte[]{leap, spit}));
            world.setEntityState(this, attackHandler.getCurrentAttack());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 4 || id == 5) {
            currentAnimation = attackHandler.getAnimation(id);
            getCurrentAnimation().startAnimation();
        }
        super.handleStatusUpdate(id);
    }

    @Override
    public void onStopLeaping() {
    }
}
