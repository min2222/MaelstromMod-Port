package com.barribob.mm.entity.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.barribob.mm.Main;
import com.barribob.mm.entity.action.ActionAerialTeleport;
import com.barribob.mm.entity.action.ActionRayAttack;
import com.barribob.mm.entity.action.ActionRingAttack;
import com.barribob.mm.entity.action.ActionRuneAttack;
import com.barribob.mm.entity.action.ActionVolley;
import com.barribob.mm.entity.action.IAction;
import com.barribob.mm.entity.adjustment.MovingRuneAdjustment;
import com.barribob.mm.entity.adjustment.RandomRuneAdjustment;
import com.barribob.mm.entity.ai.AIAerialTimedAttack;
import com.barribob.mm.entity.ai.EntityAIWanderWithGroup;
import com.barribob.mm.entity.ai.FlyingMoveHelper;
import com.barribob.mm.entity.projectile.EntityLargeGoldenRune;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileGoldenMissile;
import com.barribob.mm.entity.projectile.ProjectileMaelstromRune;
import com.barribob.mm.entity.projectile.ProjectileStatueMaelstromMissile;
import com.barribob.mm.entity.util.IAttack;
import com.barribob.mm.entity.util.TimedAttackInitiator;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.packets.MessageModParticles;
import com.barribob.mm.particle.EnumModParticles;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.RenderUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityGoldenBoss extends EntityMaelstromMob implements IAttack {
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossBarColor.YELLOW, BossBarOverlay.NOTCHED_6));
    protected static final EntityDataAccessor<Integer> ATTACK_COUNT = SynchedEntityData.defineId(EntityLeveledMob.class, EntityDataSerializers.INT);
    private static boolean doSummonNext;
    private static boolean doTeleportNext;

    public EntityGoldenBoss(Level worldIn) {
        super(worldIn);
        this.moveControl = new FlyingMoveHelper(this);
        this.navigation = new FlyingPathNavigation(this, worldIn);
        this.setSize(1.6f, 3.6f);
        this.healthScaledAttackFactor = 0.2;
        if(!level.isClientSide) {
            initNirvanaAI();
        }
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.getBoundingBox().inflate(targetDistance);
    }

    Supplier<ModProjectile> goldenMissile = () -> new ProjectileGoldenMissile(level, this, this.getAttack() * getConfigFloat("golden_missile_damage"));
    Supplier<ModProjectile> maelstromMissile = () -> new ProjectileStatueMaelstromMissile(level, this, this.getAttack() * getConfigFloat("maelstrom_missile_damage"));
    Supplier<ModProjectile> goldenRune = () -> new EntityLargeGoldenRune(this.level, this, this.getAttack() * getConfigFloat("golden_rune_damage"));
    Supplier<ModProjectile> maelstromRune = () -> new ProjectileMaelstromRune(this.level, this, this.getAttack() * getConfigFloat("maelstrom_rune_damage"));
    Supplier<ModProjectile> maelstromOrGoldenMissile = () -> random.nextInt(2) == 0 ? goldenMissile.get() : maelstromMissile.get();
    IAction goldenRayAction = new ActionRayAttack(goldenMissile, 1.1f);
    IAction maelstromRayAction = new ActionRayAttack(maelstromMissile, 1.1f);

    private final Consumer<LivingEntity> rayAttack = target -> {
        ModBBAnimations.animation(this, "statue.fireball", false);

        addEvent(() -> {
            goldenRayAction.performAction(this, target);
            this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, ModRandom.getFloat(0.2f) + 1.3f);
        }, 22);
    };

    private final Consumer<LivingEntity> secondPhaseRayAttack = target -> {
        ModBBAnimations.animation(this, "statue.fireball", false);

        addEvent(() -> {
            goldenRayAction.performAction(this, target);
            maelstromRayAction.performAction(this, target);
            this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, ModRandom.getFloat(0.2f) + 1.3f);
        }, 22);
    };

    private final Consumer<LivingEntity> runeAttack = target -> {
        ModBBAnimations.animation(this, "statue.runes", false);
        addEvent(() -> new ActionRuneAttack(goldenRune, new RandomRuneAdjustment(target)).performAction(this, target), 12);
    };

    private final Consumer<LivingEntity> secondPhaseRuneAttack = target -> {
        ModBBAnimations.animation(this, "statue.runes", false);
        addEvent(() -> new ActionRuneAttack(maelstromRune, new MovingRuneAdjustment(target)).performAction(this, target), 12);
    };

    private final Consumer<LivingEntity> spawnPillarAttack = target -> {
        ModBBAnimations.animation(this, "statue.summon", false);

        this.addEvent(() -> {
            for(int i = 0; i < getMobConfig().getInt("summoning_algorithm.mobs_per_spawn"); i++) {
                boolean findGround = !getMobConfig().getBoolean("pillar_can_be_summoned_in_air");
                BlockPos spawnCenter = findGround ? ModUtils.findGroundBelow(level, blockPosition()) : blockPosition();
                EntityLeveledMob mob = ModUtils.spawnMob(level, spawnCenter, this.getLevel(),
                        getMobConfig().getConfig("summoning_algorithm"),
                        findGround);
                if (mob != null) {
                    mob.setTarget(target);
                    ModUtils.lineCallback(this.getEyePosition(1), mob.position(), 20, (pos, j) ->
                            Main.NETWORK.sendToAllTracking(new MessageModParticles(EnumModParticles.EFFECT, pos, Vec3.ZERO, mob.getElement().particleColor), this));
                }
            }
        }, 15);
    };

    private final Consumer<LivingEntity> volleyAttack = target -> {
        ModBBAnimations.animation(this, "statue.volley", false);
        Supplier<ModProjectile> projectileSupplier = isSecondPhase() ? maelstromOrGoldenMissile : goldenMissile;
        new ActionVolley(projectileSupplier, 1.6f).performAction(this, target);
    };

    private final Consumer<LivingEntity> teleportAttack = target -> new ActionAerialTeleport(ModColors.YELLOW).performAction(this, target);

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void die(DamageSource cause) {
        // Spawn the second half of the boss
        EntityMaelstromStatueOfNirvana boss = new EntityMaelstromStatueOfNirvana(level);
        boss.copyPosition(this);
        boss.setYHeadRot(this.yHeadRot);
        if (!level.isClientSide) {
            boss.finalizeSpawn(level.getCurrentDifficultyAt(this.blockPosition()), null);
            boss.setLevel(getMobLevel());
            boss.setElement(getElement());
            level.addFreshEntity(boss);

            LivingEntity attackTarget = this.getTarget();
            if(attackTarget != null) {
                boss.setTarget(attackTarget);
                playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 2.5f, 1.0f + ModRandom.getFloat(0.2f));
                new ActionRingAttack(boss.maelstromFlame).performAction(boss, attackTarget);
            }
        }
        this.setPos(0, 0, 0);
        super.die(cause);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        ModUtils.removeTaskOfType(this.goalSelector, EntityAIWanderWithGroup.class);
    }

    private void initNirvanaAI() {
        float attackDistance = (float) this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue();
        this.goalSelector.addGoal(4,
                new AIAerialTimedAttack(this, attackDistance, 20, 30,
                        new TimedAttackInitiator<>(this, 40)));
    }

    @Override
    public void tick() {
        super.tick();
        this.bossInfo.setProgress(getHealth() / getMaxHealth());
        if (!level.isClientSide) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
        }
    }

    @Override
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {
        long pillars = goldenPillars().size();
        return super.isInvulnerableTo(source) || pillars > 0;
    }

    public List<EntityGoldenPillar> goldenPillars() {
        return ModUtils.getEntitiesInBox(this, this.getBoundingBox()
                .grow(getMobConfig().getDouble("pillar_protection_range"))).stream()
                .filter(e -> e instanceof EntityGoldenPillar)
                .filter(e ->  world.rayTraceBlocks(e.getEyePosition(1), getEyePosition(1), false, true, false) == null)
                .map(e -> (EntityGoldenPillar)e)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {

        if(random.nextInt(8) == 0 && amount > 1) {
            doTeleportNext = true;
        }

        double firstSummonHp = getMobConfig().getDouble("first_summon_hp");
        double secondSummonHp = getMobConfig().getDouble("second_summon_hp");
        double thirdSummonHp = getMobConfig().getDouble("third_summon_hp");
        double fourthSummonHp = getMobConfig().getDouble("fourth_summon_hp");

        float prevHealth = this.getHealth();
        boolean flag = super.hurt(source, amount);

        if ((prevHealth > firstSummonHp && this.getHealth() <= firstSummonHp) ||
                (prevHealth > secondSummonHp && this.getHealth() <= secondSummonHp) ||
                (prevHealth > thirdSummonHp && this.getHealth() <= thirdSummonHp) ||
                (prevHealth > fourthSummonHp && this.getHealth() <= fourthSummonHp)) {
            doSummonNext = true;
        }

        return flag;
    }

    @Override
    public int startAttack(LivingEntity target, float distanceSq, boolean strafingBackwards) {
        if(doSummonNext) {
            doSummonNext = false;
            spawnPillarAttack.accept(target);
            addEvent(() -> setAttackCount(0), 25);

            return 40;
        }

        return doNormalAttack(target);
    }

    public int doNormalAttack(LivingEntity target) {
        if(getAttackCount() == 0) {
            setAttackCount(ModRandom.range(4, 7));
        }

        boolean canSee = level.clip(new ClipContext(target.getEyePosition(1), getEyePosition(1), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)) == null;
        List<Consumer<LivingEntity>> attacks = new ArrayList<>(Arrays.asList(
                volleyAttack,
                isSecondPhase() ? secondPhaseRayAttack : rayAttack,
                isSecondPhase() ? secondPhaseRuneAttack : runeAttack,
                teleportAttack));
        double[] weights = {1, 1, 1, canSee ? 0 : 2};

        Consumer<LivingEntity> nextAttack = ModRandom.choice(attacks, random, weights).next();

        if(doTeleportNext) {
            nextAttack = teleportAttack;
            doTeleportNext = false;
        }

        nextAttack.accept(target);

        int cooldown = getAttackCount() == 1 ? 120 : 40;

        addEvent(() -> setAttackCount(getAttackCount() - 1), 25);

        return cooldown;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            Vec3 particleColor = this.isSecondPhase() && random.nextFloat() < 0.5 ? ModColors.PURPLE : ModColors.YELLOW;
            ParticleManager.spawnEffect(level, ModRandom.randVec()
                    .add(this.position()),
                    particleColor);
        } else if(id == ModUtils.SECOND_PARTICLE_BYTE) {
            ModUtils.performNTimes(3, i -> ModUtils.circleCallback(i * 0.5f + 1, 30, pos -> {
                ParticleManager.spawnSwirl(level, position().add(pos), ModColors.YELLOW, Vec3.ZERO, ModRandom.range(10, 15));
                ParticleManager.spawnSwirl(level,
                        position().add(ModUtils.rotateVector2(pos, ModUtils.yVec(1), 90)),
                        ModColors.YELLOW, Vec3.ZERO, ModRandom.range(10, 15));
            }));
        }

        super.handleEntityEvent(id);
    }

    @Override
    public void doRender(RenderManager renderManager, double x, double y, double z, float entityYaw, float partialTicks) {
        for (EntityGoldenPillar e : goldenPillars()) {
            RenderUtils.drawLazer(renderManager, this.position(), e.position(), new Vec3(x, y - 1, z), ModColors.YELLOW, this, partialTicks);
        }
    }

    public boolean isSecondPhase() {
        return this.getHealth() < getMobConfig().getDouble("second_phase_hp");
    }

    @Override
    public void setCustomName(@Nonnull Component name) {
        super.setCustomName(name);
        this.bossInfo.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(@Nonnull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@Nonnull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_COUNT, 0);
    }

    public int getAttackCount() {
        return entityData.get(ATTACK_COUNT);
    }

    protected void setAttackCount(int i) {
    	entityData.set(ATTACK_COUNT, i);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.METAL_PLACE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.METAL_BREAK;
    }

    @Override
    protected float getManaExp() {
        return 0;
    }

    @Override
    public void performRangedAttack(@Nonnull LivingEntity target, float distanceFactor) {
    }

    @Override
    public void travel(Vec3 vec) {
        ModUtils.aerialTravel(this, (float)vec.x, (float)vec.y, (float)vec.z);
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
