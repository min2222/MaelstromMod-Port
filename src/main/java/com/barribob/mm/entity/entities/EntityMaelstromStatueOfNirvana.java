package com.barribob.mm.entity.entities;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

import com.barribob.mm.entity.action.ActionAerialTeleport;
import com.barribob.mm.entity.action.ActionRayAttack;
import com.barribob.mm.entity.action.ActionRingAttack;
import com.barribob.mm.entity.action.ActionRuneAttack;
import com.barribob.mm.entity.adjustment.MovingRuneAdjustment;
import com.barribob.mm.entity.ai.AIAerialTimedAttack;
import com.barribob.mm.entity.ai.EntityAIWanderWithGroup;
import com.barribob.mm.entity.ai.FlyingMoveHelper;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileHomingFlame;
import com.barribob.mm.entity.projectile.ProjectileMaelstromRune;
import com.barribob.mm.entity.projectile.ProjectileStatueMaelstromMissile;
import com.barribob.mm.entity.util.IAttack;
import com.barribob.mm.entity.util.TimedAttackInitiator;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LootTableHandler;
import com.barribob.mm.util.handlers.ParticleManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityMaelstromStatueOfNirvana extends EntityMaelstromMob implements IAttack {
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossEvent.Color.PURPLE, BossEvent.Overlay.NOTCHED_20));
    private static boolean doTeleportNext;
    Consumer<LivingEntity> previousAttack;

    public EntityMaelstromStatueOfNirvana(Level worldIn) {
        super(worldIn);
        this.moveHelper = new FlyingMoveHelper(this);
        this.navigator = new PathNavigateFlying(this, worldIn);
        this.setSize(1.6f, 3.6f);
        this.healthScaledAttackFactor = 0.2;
        if(!level.isClientSide) {
            initNirvanaAI();
        }
    }

    private void initNirvanaAI() {
        float attackDistance = (float) this.getEntityAttribute(Attributes.FOLLOW_RANGE).getAttributeValue();
        this.tasks.addTask(4,
                new AIAerialTimedAttack(this, attackDistance, 20, 30,
                        new TimedAttackInitiator<>(this, 80)));
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.getBoundingBox().grow(targetDistance);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        ModUtils.removeTaskOfType(this.tasks, EntityAIWanderWithGroup.class);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.bossInfo.setPercent(getHealth() / getMaxHealth());
        if (!level.isClientSide) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (rand.nextInt(8) == 0 && amount > 1) {
            doTeleportNext = true;
        }

        return super.attackEntityFrom(source, amount);
    }

    @Override
    public int startAttack(LivingEntity target, float distanceSq, boolean strafingBackwards) {
        boolean canSee = world.rayTraceBlocks(target.getEyePosition(1), getEyePosition(1), false, true, false) == null;

        List<Consumer<LivingEntity>> attacks = new ArrayList<>(Arrays.asList(
                rayAttack, runeAttack, ringAttack, teleportAttack));
        int i = previousAttack == ringAttack ? 0 : 1;
        double[] weights = {1, 1, i, canSee ? 0 : 2};

        previousAttack = ModRandom.choice(attacks, rand, weights).next();
        if(doTeleportNext) {
            previousAttack = teleportAttack;
            doTeleportNext = false;
        }

        previousAttack.accept(target);

        return 50;
    }

    public Supplier<ModProjectile> maelstromFlame = () -> {
        ProjectileHomingFlame projectile = new ProjectileHomingFlame(world, this, this.getAttack() * getConfigFloat("homing_projectile_damage"));
        projectile.setNoGravity(true);
        projectile.setTravelRange(40);
        return projectile;
    };
    Supplier<ModProjectile> maelstromRune = () -> new ProjectileMaelstromRune(this.world, this, this.getAttack() * getConfigFloat("maelstrom_rune_damage"));
    Supplier<ModProjectile> maelstromMissile = () -> new ProjectileStatueMaelstromMissile(this.world, this, this.getAttack() * getConfigFloat("maelstrom_missile_damage"));

    private final Consumer<LivingEntity> teleportAttack = target -> new ActionAerialTeleport(ModColors.PURPLE).performAction(this, target);

    private final Consumer<LivingEntity> rayAttack = target -> {
        ModBBAnimations.animation(this, "statue.fireball", false);

        addEvent(() -> {
            new ActionRayAttack(maelstromMissile, 1.1f).performAction(this, target);
            new ActionRayAttack(maelstromMissile, 1.1f).performAction(this, target);
            this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, ModRandom.getFloat(0.2f) + 1.3f);
        }, 22);
    };

    private final Consumer<LivingEntity> runeAttack = target -> {
        ModBBAnimations.animation(this, "statue.runes", false);
        addEvent(() -> new ActionRuneAttack(maelstromRune, new MovingRuneAdjustment(target)).performAction(this, target), 12);
    };

    private final Consumer<LivingEntity> ringAttack = target -> {
        ModBBAnimations.animation(this, "statue.summon", false);
        addEvent(() -> new ActionRingAttack(maelstromFlame).performAction(this, target), 15);
        playSound(SoundEvents.ILLAGER_PREPARE_MIRROR, 2.5f, 1.0f + ModRandom.getFloat(0.2f));
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            ParticleManager.spawnEffect(world, ModRandom.randVec().add(this.position()), ModColors.PURPLE);
        } else if(id == ModUtils.SECOND_PARTICLE_BYTE) {
            ModUtils.performNTimes(3, i -> ModUtils.circleCallback(i * 0.5f + 1, 30, pos -> {
                ParticleManager.spawnSwirl(world, position().add(pos), ModColors.PURPLE, Vec3.ZERO, ModRandom.range(10, 15));
                ParticleManager.spawnSwirl(world,
                        position().add(ModUtils.rotateVector2(pos, ModUtils.yVec(1), 90)),
                        ModColors.PURPLE, Vec3.ZERO, ModRandom.range(10, 15));
            }));
        }

        super.handleStatusUpdate(id);
    }

    @Override
    public void setCustomNameTag(@Nonnull String name) {
        super.setCustomNameTag(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void addTrackingPlayer(@Nonnull ServerPlayer player) {
        super.addTrackingPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(@Nonnull ServerPlayer player) {
        super.removeTrackingPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.BLOCK_METAL_PLACE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_METAL_BREAK;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableHandler.GOLDEN_BOSS;
    }

    @Override
    public void performRangedAttack(@Nonnull LivingEntity target, float distanceFactor) {
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        ModUtils.aerialTravel(this, strafe, vertical, forward);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, @Nonnull BlockState state, @Nonnull BlockPos pos) {
    }

    @Override
    public boolean isOnLadder() {
        return false;
    }
}
