package com.barribob.mm.entity.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.barribob.mm.entity.action.IAction;
import com.barribob.mm.entity.ai.EntityAIRangedAttack;
import com.barribob.mm.entity.ai.EntityAIRangedAttackNoReset;
import com.barribob.mm.entity.animation.AnimationClip;
import com.barribob.mm.entity.animation.AnimationOscillateArms;
import com.barribob.mm.entity.animation.StreamAnimation;
import com.barribob.mm.entity.model.ModelMaelstromIllager;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileHorrorAttack;
import com.barribob.mm.entity.projectile.ProjectileMaelstromWisp;
import com.barribob.mm.entity.util.ComboAttack;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LootTableHandler;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityMaelstromIllager extends EntityMaelstromMob {
    private final byte summonMob = 4;
    private final byte magicMissile = 5;
    private final byte wisp = 6;
    private final byte shield = 7;
    private final byte enemy = 8;
    private final float shieldSize = 4;
    private EntityAIRangedAttack<EntityMaelstromMob> phase1AttackAI;
    private final ComboAttack attackHandler = new ComboAttack();

    private final IAction spawnEnemy = new IAction() {
        @Override
        public void performAction(EntityLeveledMob actor, LivingEntity target) {
            int mobCount = phase2() ? getMobConfig().getInt("summoning_algorithm.second_phase_mobs_per_spawn") :
                    getMobConfig().getInt("summoning_algorithm.first_phase_mobs_per_spawn");
            for (int i = 0; i < mobCount; i++) {
                ModUtils.spawnMob(level, blockPosition(), getLevel(), getMobConfig().getConfig("summoning_algorithm"));
            }
            actor.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
        }
    };

    // Responsible for the boss bar
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossBarColor.PURPLE, BossBarOverlay.NOTCHED_20));

    public EntityMaelstromIllager(Level worldIn) {
        super(worldIn);
        this.setSize(0.9f, 2.5f);
        this.healthScaledAttackFactor = 0.2;
        if (!level.isClientSide) {
            attackHandler.setAttack(magicMissile, (IAction) (actor, target) -> {
                ModUtils.throwProjectile(actor, target, new ProjectileHorrorAttack(level, actor, getAttack() * getConfigFloat("maelstrom_missile_damage")), 6.0f, 1.2f,
                        ModUtils.getRelativeOffset(actor, new Vec3(0, 0, 1)));
                actor.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
            });
            attackHandler.setAttack(wisp, (IAction) (actor, target) -> {
                ModProjectile proj = new ProjectileMaelstromWisp(level, actor, getAttack() * getConfigFloat("ring_damage"));
                proj.setTravelRange(15f);
                ModUtils.throwProjectile(actor, target, proj, 1.0f, 1.0f);
                playSoundWithFallback(SoundsHandler.Hooks.ENTITY_ILLAGER_VORTEX, SoundEvents.BLAZE_AMBIENT);
            });
            attackHandler.setAttack(shield, (IAction) (actor, target) -> {
                DamageSource damageSource = ModDamageSource.builder()
                        .directEntity(actor)
                        .type(ModDamageSource.MAGIC)
                        .element(getElement())
                        .stoppedByArmorNotShields().build();

                ModUtils.handleAreaImpact(shieldSize, (e) -> getAttack() * getConfigFloat("defensive_burst_damage"), actor, position(), damageSource);
                playSoundWithFallback(SoundsHandler.Hooks.ENTITY_ILLAGER_DOME, SoundEvents.FIREWORK_ROCKET_BLAST);
                actor.level.broadcastEntityEvent(actor, ModUtils.THIRD_PARTICLE_BYTE);
            });
            attackHandler.setAttack(enemy, spawnEnemy);
        }
    }

    @Override
    protected void initAnimation() {
        List<List<AnimationClip<ModelMaelstromIllager>>> animationMissile = new ArrayList<List<AnimationClip<ModelMaelstromIllager>>>();
        List<AnimationClip<ModelMaelstromIllager>> rightArm = new ArrayList<AnimationClip<ModelMaelstromIllager>>();
        List<AnimationClip<ModelMaelstromIllager>> leftArm = new ArrayList<AnimationClip<ModelMaelstromIllager>>();

        BiConsumer<ModelMaelstromIllager, Float> leftArmMover = (model, f) -> {
            model.bipedLeftArm.rotateAngleX = f;
            model.bipedLeftArm.rotateAngleY = 0;
            model.bipedLeftArm.rotateAngleZ = f.floatValue() / -6;
        };
        BiConsumer<ModelMaelstromIllager, Float> rightArmMover = (model, f) -> {
            model.bipedRightArm.rotateAngleX = 0;
            model.bipedRightArm.rotateAngleY = 0;
            model.bipedRightArm.rotateAngleZ = 0;
        };

        leftArm.add(new AnimationClip(12, 0, -180, leftArmMover));
        leftArm.add(new AnimationClip(8, -180, -180, leftArmMover));
        leftArm.add(new AnimationClip(4, -180, 0, leftArmMover));

        rightArm.add(new AnimationClip(12, 0, -180, rightArmMover));
        rightArm.add(new AnimationClip(8, -180, -180, rightArmMover));
        rightArm.add(new AnimationClip(4, -180, 0, rightArmMover));

        animationMissile.add(rightArm);
        animationMissile.add(leftArm);

        attackHandler.setAttack(magicMissile, IAction.NONE, () -> new StreamAnimation(animationMissile));

        List<List<AnimationClip<ModelMaelstromIllager>>> animationWisp = new ArrayList<List<AnimationClip<ModelMaelstromIllager>>>();
        rightArm = new ArrayList<AnimationClip<ModelMaelstromIllager>>();
        leftArm = new ArrayList<AnimationClip<ModelMaelstromIllager>>();

        leftArmMover = (model, f) -> {
            model.bipedLeftArm.rotateAngleX = (float) Math.toRadians(-90);
            model.bipedLeftArm.rotateAngleY = f;
            model.bipedLeftArm.rotateAngleZ = 0;
        };
        rightArmMover = (model, f) -> {
            model.bipedRightArm.rotateAngleX = (float) Math.toRadians(-90);
            model.bipedRightArm.rotateAngleY = f;
            model.bipedRightArm.rotateAngleZ = 0;
        };

        leftArm.add(new AnimationClip(10, 0, -90, leftArmMover));
        leftArm.add(new AnimationClip(8, -90, -90, leftArmMover));
        leftArm.add(new AnimationClip(4, -90, 0, leftArmMover));

        rightArm.add(new AnimationClip(10, 0, 90, rightArmMover));
        rightArm.add(new AnimationClip(8, 90, 90, rightArmMover));
        rightArm.add(new AnimationClip(4, 90, 0, rightArmMover));

        animationWisp.add(rightArm);
        animationWisp.add(leftArm);

        attackHandler.setAttack(wisp, IAction.NONE, () -> new StreamAnimation(animationWisp));

        List<List<AnimationClip<ModelMaelstromIllager>>> animationShield = new ArrayList<List<AnimationClip<ModelMaelstromIllager>>>();

        rightArm = new ArrayList<AnimationClip<ModelMaelstromIllager>>();
        leftArm = new ArrayList<AnimationClip<ModelMaelstromIllager>>();

        leftArmMover = (model, f) -> {
            model.bipedLeftArm.rotateAngleX = f;
            model.bipedLeftArm.rotateAngleY = -f * 0.45f;
            model.bipedLeftArm.rotateAngleZ = 0;
        };
        rightArmMover = (model, f) -> {
            model.bipedRightArm.rotateAngleX = f;
            model.bipedRightArm.rotateAngleY = f * 0.45f;
            model.bipedRightArm.rotateAngleZ = 0;
        };

        leftArm.add(new AnimationClip(10, 0, -120, leftArmMover));
        leftArm.add(new AnimationClip(8, -120, -120, leftArmMover));
        leftArm.add(new AnimationClip(4, -120, 0, leftArmMover));

        rightArm.add(new AnimationClip(10, 0, -120, rightArmMover));
        rightArm.add(new AnimationClip(8, -120, -120, rightArmMover));
        rightArm.add(new AnimationClip(4, -120, 0, rightArmMover));

        animationShield.add(rightArm);
        animationShield.add(leftArm);

        attackHandler.setAttack(shield, IAction.NONE, () -> new StreamAnimation(animationShield));
        attackHandler.setAttack(enemy, IAction.NONE, () -> new AnimationOscillateArms(60, this));

        this.currentAnimation = new AnimationOscillateArms(60, this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        phase1AttackAI = new EntityAIRangedAttackNoReset<EntityMaelstromMob>(this, 1.25f, 360, 60, 15.0f, 0.5f);
        this.goalSelector.addGoal(4, phase1AttackAI);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return LootTableHandler.MAELSTROM_ILLAGER;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.isSwingingArms()) {
            if (!source.isProjectile()) {
                Entity entity = source.getDirectEntity();

                if (entity instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity) entity);
                }
            }
            this.playSound(SoundsHandler.ENTITY_CHAOS_KNIGHT_BLOCK, 1.0f, 0.9f + ModRandom.getFloat(0.2f));

            return false;
        }

        float prevHealth = this.getHealth();
        double firstDialogHp = getMobConfig().getDouble("first_dialog_hp");
        double secondDialogHP = getMobConfig().getDouble("second_dialog_hp");
        double secondPhaseHp = getMobConfig().getDouble("second_boss_phase_hp");
        boolean flag = super.hurt(source, amount);

        String message = "";
        if (prevHealth > firstDialogHp && this.getHealth() <= firstDialogHp) {
            message = "illager_1";
        }

        if (prevHealth > secondDialogHP && this.getHealth() <= secondDialogHP) {
            message = "illager_2";
        }

        if (prevHealth > secondPhaseHp && this.getHealth() <= secondPhaseHp) {
            message = "illager_3";
        }

        if (message != "") {
            for (Player player : this.bossInfo.getPlayers()) {
                player.sendSystemMessage(Component.literal(ChatFormatting.DARK_PURPLE + "Maelstrom Illager: " + ChatFormatting.WHITE)
                        .append(Component.translatable(ModUtils.LANG_CHAT + message)));
            }
        }

        return flag;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.phase1AttackAI.setAttackCooldowns(
                phase2() ? 50 : 360,
                phase2() ? 20 : 60
        );

        if (phase2() && attackHandler.getCurrentAttack() != 0) {
            attackHandler.getCurrentAttackAction().performAction(this, target);
        } else {
            spawnEnemy.performAction(this, target);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!phase2() || (phase2() && attackHandler.getCurrentAttack() == enemy)) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        } else if (this.attackHandler != null && this.isSwingingArms()) {
            if (this.attackHandler.getCurrentAttack() == magicMissile) {
                level.broadcastEntityEvent(this, ModUtils.SECOND_PARTICLE_BYTE);
            }
        }
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        super.setSwingingArms(swingingArms);
        if (this.isSwingingArms()) {
            if (phase2()) {
                Byte[] attack = {wisp, magicMissile, enemy};
                double[] weights = {0.5, 0.5, 0.2};
                attackHandler.setCurrentAttack(ModRandom.choice(attack, this.getRandom(), weights).next());
                if (this.getTarget() != null && this.distanceTo(this.getTarget()) < 4) {
                    attackHandler.setCurrentAttack(shield);
                    playSoundWithFallback(SoundsHandler.Hooks.ENTITY_ILLAGER_DOME_CHARGE, SoundsHandler.NONE);
                }
                else {
                    playSoundWithFallback(SoundsHandler.Hooks.ENTITY_ILLAGER_SPELL_CHARGE, SoundsHandler.NONE);
                }
                level.broadcastEntityEvent(this, attackHandler.getCurrentAttack());
            } else {
                level.broadcastEntityEvent(this, summonMob);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == summonMob) {
            this.currentAnimation = new AnimationOscillateArms(60, this);
            currentAnimation.startAnimation();
        } else if (id >= 5 && id <= 8) {
            currentAnimation = attackHandler.getAnimation(id);
            getCurrentAnimation().startAnimation();
        } else if (id == ModUtils.THIRD_PARTICLE_BYTE) {
            for (int i = 0; i < 1000; i++) {
                Vec3 unit = new Vec3(0, 1, 0);
                unit = unit.xRot((float) (Math.PI * ModRandom.getFloat(1)));
                unit = unit.yRot((float) (Math.PI * ModRandom.getFloat(1)));
                unit = unit.normalize().scale(shieldSize);
                ParticleManager.spawnEffect(level, unit.add(position()), ModColors.PURPLE);
            }
        } else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            ParticleManager.spawnMaelstromPotionParticle(level, random, ModUtils.getRelativeOffset(this, new Vec3(0, this.getEyeHeight(), 1)).add(position()), true);
        } else if (id == ModUtils.PARTICLE_BYTE) {
            if (this.isSwingingArms()) {
                float f = this.yBodyRot * 0.017453292F + Mth.cos(this.tickCount * 0.6662F) * 0.25F;
                float f1 = Mth.cos(f);
                float f2 = Mth.sin(f);
                ParticleManager.spawnMaelstromPotionParticle(level, random, new Vec3(this.posX + f1 * 0.6D, this.posY + 1.8D, this.posZ + f2 * 0.6D), true);
                ParticleManager.spawnMaelstromPotionParticle(level, random, new Vec3(this.posX - f1 * 0.6D, this.posY + 1.8D, this.posZ - f2 * 0.6D), true);
            }
        }
        super.handleEntityEvent(id);
    }

    private boolean phase2() {
        return this.getHealth() < getMobConfig().getInt("second_boss_phase_hp");
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }

        super.readAdditionalSaveData(compound);
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    protected void customServerAiStep() {
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        super.customServerAiStep();
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
}