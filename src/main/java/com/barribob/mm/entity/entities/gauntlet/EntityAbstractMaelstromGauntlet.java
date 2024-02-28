package com.barribob.mm.entity.entities.gauntlet;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.barribob.mm.entity.ai.AIAerialTimedAttack;
import com.barribob.mm.entity.ai.AiFistWander;
import com.barribob.mm.entity.ai.EntityAIWanderWithGroup;
import com.barribob.mm.entity.ai.FlyingMoveHelper;
import com.barribob.mm.entity.ai.GauntletEntitySenses;
import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.entity.util.DirectionalRender;
import com.barribob.mm.entity.util.IPitch;
import com.barribob.mm.init.ModDimensions;
import com.barribob.mm.renderer.ITarget;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.RenderUtils;
import com.barribob.mm.util.handlers.ParticleManager;
import com.barribob.mm.util.handlers.SoundsHandler;
import com.barribob.mm.world.dimension.crimson_kingdom.WorldGenGauntletSpike;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensing;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.entity.PartEntity;

public abstract class EntityAbstractMaelstromGauntlet extends EntityMaelstromMob implements IEntityMultiPart, DirectionalRender, ITarget, IPitch {
    // We keep track of the look ourselves because minecraft's look is clamped
    protected static final EntityDataAccessor<Float> LOOK = SynchedEntityData.defineId(EntityLeveledMob.class, EntityDataSerializers.FLOAT);
    private final ServerBossEvent bossInfo = (new ServerBossEvent(this.getDisplayName(), BossBarColor.RED, BossBarOverlay.NOTCHED_6));
    private final PartEntity[] hitboxParts;
    private final float boxSize = 0.8f;
    private final PartEntity eye = new PartEntity(this, "eye", 1.2f, 1.2f);
    private final PartEntity behindEye = new PartEntity(this, "behindEye", 1.0f, 1.0f);
    private final PartEntity bottomPalm = new PartEntity(this, "bottomPalm", 1.2f, 1.2f);
    private final PartEntity upLeftPalm = new PartEntity(this, "upLeftPalm", boxSize, boxSize);
    private final PartEntity upRightPalm = new PartEntity(this, "upRightPalm", boxSize, boxSize);
    private final PartEntity rightPalm = new PartEntity(this, "rightPalm", boxSize, boxSize);
    private final PartEntity leftPalm = new PartEntity(this, "leftPalm", boxSize, boxSize);
    private final PartEntity fingers = new PartEntity(this, "fingers", 1.2f, 1.2f);
    protected final PartEntity fist = new PartEntity(this, "fist", 0, 0);
    private IGauntletAction currentAction;
    protected static final byte stopLazerByte = 39;
    private final double punchImpactSize = getMobConfig().getDouble("punch_impact_size");
    private @Nullable MovementTracker movement;
    IGauntletAction defendAction = new DefendAction(this);

    // Lazer state variables
    private Vec3 renderLazerPos;
    private Vec3 prevRenderLazerPos;

    // Used to filter damage from parts
    private boolean damageFromEye;

    // Custom entity see ai
    private final Sensing senses = new GauntletEntitySenses(this);

    public final Consumer<Vec3> punchAtPos = (target) -> {
        currentAction = new PunchAction("gauntlet.punch", () -> target, () -> {}, this, fist);
        currentAction.doAction();
        for (int i = 0; i < 12; i++) {
            this.addEvent(() -> ModUtils.faceDirection(this, target, 15), i);
        }
    };

    public EntityAbstractMaelstromGauntlet(Level worldIn) {
        super(worldIn);
        this.moveControl = new FlyingMoveHelper(this);
        this.navigation = new FlyingPathNavigation(this, worldIn);
        this.hitboxParts = new PartEntity[]{eye, behindEye, bottomPalm, upLeftPalm, upRightPalm, rightPalm, leftPalm, fingers, fist};
        this.setSize(2, 4);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.healthScaledAttackFactor = 0.1;
        if(!level.isClientSide) {
            this.initGauntletAI();
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.26f);
        this.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        ModUtils.removeTaskOfType(this.goalSelector, EntityAIWanderWithGroup.class);
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.getBoundingBox().inflate(targetDistance);
    }

    private void initGauntletAI() {
        float attackDistance = (float) this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue();
        this.goalSelector.addGoal(4, new
                AIAerialTimedAttack(this, attackDistance, 20, 20,
                new GauntletAttackInitiator( 60, this::startAttack, this::defendAttack)));
        this.goalSelector.addGoal(7, new AiFistWander(this, punchAtPos, 120, 10));
    }

    public final IGauntletAction startAttack(LivingEntity target) {
        float distanceSq = (float) distanceToSqr(target);
        this.currentAction = getNextAttack(target, distanceSq, currentAction);
        this.currentAction.doAction();
        return currentAction;
    }

    protected abstract IGauntletAction getNextAttack(LivingEntity target, float distanceSq, IGauntletAction previousAction);

    public final @Nullable IGauntletAction defendAttack(LivingEntity target) {
        if (seesDanger(movement, target)) {
            currentAction = defendAction;
            defendAction.doAction();
            return defendAction;
        }
        return null;
    }

    @Override
    public void setTarget(@Nullable LivingEntity entity) {
        if(entity != null && (movement == null || movement.entity != entity)) {
            movement = new MovementTracker(entity, 5);
        } else if (entity == null) {
            movement = null;
        }
        super.setTarget(entity);
    }

    @Override
    public void baseTick() {
        if(movement != null) movement.onUpdate();
        super.baseTick();
    }

    private boolean seesDanger(@Nullable MovementTracker movementTracker, LivingEntity target) {
        if(movementTracker == null) return false;
        Vec3 targetMovement = movementTracker.getMovementOverTicks(5);
        double velocityTowardsThis = ModUtils.direction(target.position(), position())
                .dot(targetMovement);
        return velocityTowardsThis > 3;
    }

    @Override
    public final boolean hurt(DamageSource source, float amount) {
        if (!this.damageFromEye && !source.isBypassArmor()) {
            return false;
        }
        this.damageFromEye = false;
        return super.hurt(source, amount);
    }

    @Override
    public final boolean attackEntityFromPart(@Nonnull PartEntity part, @Nonnull DamageSource source, float damage) {
        if (part == this.eye && (this.currentAction == null || !this.currentAction.isImmuneToDamage())) {
            this.damageFromEye = true;

            // Awaken the gauntlet
            if (damage > 0 && this.isImmovable()) {
                this.setImmovable(false);
            }

            return this.hurt(source, damage);
        }

        if (damage > 0.0F && !source.isBypassArmor()) {
            if (!source.isProjectile()) {
                Entity entity = source.getDirectEntity();

                if (entity instanceof LivingEntity) {
                    this.blockUsingShield((LivingEntity) entity);
                }
            }
            this.playSound(SoundEvents.BLAZE_HURT, 1.0f, 0.6f + ModRandom.getFloat(0.2f));

            return false;
        }

        return false;
    }

    @Override
    public final void aiStep() {
        bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        super.aiStep();
        Vec3[] avec3d = new Vec3[this.hitboxParts.length];
        for (int j = 0; j < this.hitboxParts.length; ++j) {
            avec3d[j] = new Vec3(this.hitboxParts[j].getX(), this.hitboxParts[j].getY(), this.hitboxParts[j].getZ());
        }

        /*
         * Set the hitbox pieces based on the entity's rotation so that even large pitch rotations don't mess up the hitboxes
         */

        setHitboxPosition(fingers, new Vec3(0, -1.5, 0));
        setHitboxPosition(behindEye, new Vec3(-0.5, -0.3, 0));
        setHitboxPosition(eye, new Vec3(0.5, -0.3, 0));
        setHitboxPosition(bottomPalm, new Vec3(-0.4, 0.7, 0));
        setHitboxPosition(rightPalm, new Vec3(0, 0, -0.7));
        setHitboxPosition(leftPalm, new Vec3(0, 0, 0.7));
        setHitboxPosition(upRightPalm, new Vec3(0, -1, -0.7));
        setHitboxPosition(upLeftPalm, new Vec3(0, -1, 0.7));

        Vec3 fistPos = this.position().subtract(ModUtils.yVec(0.5));
        ModUtils.setEntityPosition(fist, fistPos);

        for (int l = 0; l < this.hitboxParts.length; ++l) {
            this.hitboxParts[l].prevPosX = avec3d[l].x;
            this.hitboxParts[l].prevPosY = avec3d[l].y;
            this.hitboxParts[l].prevPosZ = avec3d[l].z;
        }

        if (!level.isClientSide && currentAction != null) {
            currentAction.update();
        }
    }

    private void setHitboxPosition(Entity entity, Vec3 offset) {
        Vec3 lookVec = ModUtils.getLookVec(this.getPitch(), this.yBodyRot);
        Vec3 center = this.position().add(ModUtils.yVec(1.3));

        Vec3 position = center.subtract(ModUtils.Y_AXIS
                .scale(this.fingers.getBoundingBox().getAverageEdgeLength() * 0.5))
                .add(ModUtils.getAxisOffset(lookVec, offset));
        ModUtils.setEntityPosition(entity, position);
    }

    @Override
    public final void tick() {
        Vec3 vel = ModUtils.getEntityVelocity(this);
        double speed = vel.length();

        super.tick();

        boolean motionStopped = (getDeltaMovement().x == 0 && vel.x != 0) || (getDeltaMovement().y == 0 && vel.y != 0) || (getDeltaMovement().z == 0 && vel.z != 0);
        if(motionStopped && this.currentAction != null && this.currentAction.shouldExplodeUponImpact() && !level.isClientSide && speed > 0.55f) {
            onBlockPhysicalImpact(speed);
        }

        if (this.isImmovable()) {
            this.setRot(180, 0);
            this.setYHeadRot(180);
        }
    }

    private void onBlockPhysicalImpact(double velocity) {
        Vec3 pos = getEyePosition(1);
        DamageSource source = ModDamageSource.builder()
                .directEntity(this)
                .element(getElement())
                .stoppedByArmorNotShields()
                .type(ModDamageSource.MOB)
                .build();

        boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, this);
        level.explode(this, pos.x, pos.y, pos.z, (float) (velocity * 0.75f * punchImpactSize), false, flag ? BlockInteraction.DESTROY : BlockInteraction.NONE);
        ModUtils.handleAreaImpact((float) (velocity * punchImpactSize), e -> getAttack(), this, pos, source);
    }

    /**
     * Immovability doubles as the gauntlet not being "awakened" or active
     */
    @Override
    protected final void setImmovable(boolean immovable) {
        if (this.isImmovable() && !immovable) {
            this.initGauntletAI(); // Start gauntlet attacks and movements after becoming mobile
        } else if (immovable) {
            ModUtils.removeTaskOfType(goalSelector, AIAerialTimedAttack.class);
            ModUtils.removeTaskOfType(goalSelector, AiFistWander.class);
        }
        super.setImmovable(immovable);
    }

    @Override
    public final void doRender(RenderManager renderManager, double x, double y, double z, float entityYaw, float partialTicks) {
        if (this.renderLazerPos != null) {
            // This sort of jenky way of binding the wrong texture to the original guardian beam creates quite a nice particle beam visual
            renderManager.renderEngine.bindTexture(EnderDragonRenderer.ENDERCRYSTAL_BEAM_TEXTURES);
            // We must interpolate between positions to make the move smoothly
            Vec3 interpolatedPos = renderLazerPos.subtract(this.prevRenderLazerPos).scale(partialTicks).add(prevRenderLazerPos);
            RenderUtils.drawBeam(renderManager, this.getEyePosition(1), interpolatedPos, new Vec3(x, y, z), ModColors.RED, this, partialTicks);
        }
        super.doRender(renderManager, x, y, z, entityYaw, partialTicks);
    }

    @Override
    public final float getEyeHeight() {
        return 1.6f;
    }

    @Override
    public final void setPitch(Vec3 look) {
        float prevLook = this.getPitch();
        float newLook = (float) ModUtils.toPitch(look);
        float deltaLook = 5;
        float clampedLook = Mth.clamp(newLook, prevLook - deltaLook, prevLook + deltaLook);
        this.entityData.set(LOOK, clampedLook);
    }

    @Override
    public final float getPitch() {
        return this.entityData.get(LOOK);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == stopLazerByte) {
            this.renderLazerPos = null;
        } else if (id == ModUtils.PARTICLE_BYTE) {
            // Render particles in a sucking in motion
            for (int i = 0; i < 5; i++) {
                Vec3 lookVec = ModUtils.getLookVec(this.getPitch(), this.yBodyRot);
                Vec3 randOffset = ModUtils.rotateVector2(lookVec, lookVec.cross(ModUtils.Y_AXIS), ModRandom.range(-70, 70));
                randOffset = ModUtils.rotateVector2(randOffset, lookVec, ModRandom.range(0, 360)).scale(1.5f);
                Vec3 velocity = Vec3.ZERO.subtract(randOffset).normalize().scale(0.15f).add(this.getDeltaMovement());
                Vec3 particlePos = this.getEyePosition(1).add(ModUtils.getAxisOffset(lookVec, new Vec3(1, 0, 0))).add(randOffset);
                ParticleManager.spawnDust(level, particlePos, ModColors.RED, velocity, ModRandom.range(5, 7));
            }
        } else if (id == ModUtils.SECOND_PARTICLE_BYTE) {
            // Render particles in some weird circular trig fashion
            ModUtils.circleCallback(2, 16, (pos) -> {
                pos = new Vec3(pos.x, 0, pos.y).add(this.position());
                double y = Math.cos(pos.x + pos.z);
                ParticleManager.spawnSplit(level, pos.add(ModUtils.yVec(y)), ModColors.PURPLE, ModUtils.yVec(-y * 0.1));
            });
        }
        super.handleEntityEvent(id);
    }

    @Override
    public void die(DamageSource cause) {
        if (!level.isClientSide && this.getMobLevel() > 0 && this.dimension == ModDimensions.CRIMSON_KINGDOM.getId()) {

            for (int i = 0; i < 15; i++) {
                level.explode(this, this.getX(), this.getY() + i * 2, this.getZ(), 2, false, BlockInteraction.NONE);
            }

            new WorldGenGauntletSpike().generate(level, this.getRandom(), this.blockPosition().offset(new BlockPos(-3, 0, -3)));
            super.die(cause);
        }
    }

    @Override
    public final void travel(Vec3 vec3) {
        ModUtils.aerialTravel(this, (float)vec3.x, (float)vec3.y, (float)vec3.z);
    }

    /**
     * Add a bit of brightness to the entity, because otherwise it looks pretty black
     */
    @Override
    public int getBrightnessForRender() {
        return Math.min(super.getBrightnessForRender() + 60, 200);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(LOOK, 0f);
        super.defineSynchedData();
    }

    @Override
    @Nonnull
    public Sensing getSensing() {
        return this.senses;
    }

    @Override
    @Nonnull
    public Level getLevel() {
        return level;
    }

    @Override
    public @org.jetbrains.annotations.Nullable PartEntity<?>[] getParts() {
        return this.hitboxParts;
    }

    @Override
    protected boolean canDespawn() {
        return false;
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

    @Override
    public void performRangedAttack(@Nullable LivingEntity target, float distanceFactor) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    /**
     * This is overriden because we do want the main hitbox to clip with blocks while still not clipping with anything else
     */
    @Override
    public final void move(@Nonnull MoverType type, Vec3 vec) {
        this.level.getProfiler().push("move");
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;
        
        if (this.isInWeb) {
            this.isInWeb = false;
            x *= 0.25D;
            y *= 0.05000000074505806D;
            z *= 0.25D;
            this.setDeltaMovement(Vec3.ZERO);
        }

        double d2 = x;
        double d3 = y;
        double d4 = z;

        List<VoxelShape> list1 = this.level.getEntityCollisions(this, this.getBoundingBox().inflate(x, y, z));
        AABB axisalignedbb = this.getBoundingBox();

        if (y != 0.0D) {
            int k = 0;

            for (int l = list1.size(); k < l; ++k) {
                y = list1.get(k).calculateYOffset(this.getBoundingBox(), y);
            }

            this.setBoundingBox(this.getBoundingBox().move(0.0D, y, 0.0D));
        }

        if (x != 0.0D) {
            int j5 = 0;

            for (int l5 = list1.size(); j5 < l5; ++j5) {
                x = list1.get(j5).calculateXOffset(this.getBoundingBox(), x);
            }

            if (x != 0.0D) {
                this.setBoundingBox(this.getBoundingBox().move(x, 0.0D, 0.0D));
            }
        }

        if (z != 0.0D) {
            int k5 = 0;

            for (int i6 = list1.size(); k5 < i6; ++k5) {
                z = list1.get(k5).calculateZOffset(this.getBoundingBox(), z);
            }

            if (z != 0.0D) {
                this.setBoundingBox(this.getBoundingBox().move(0.0D, 0.0D, z));
            }
        }

        boolean flag = this.onGround || d3 != y && d3 < 0.0D;

        if (this.stepHeight > 0.0F && flag && (d2 != x || d4 != z)) {
            double d14 = x;
            double d6 = y;
            double d7 = z;
            AABB axisalignedbb1 = this.getBoundingBox();
            this.setBoundingBox(axisalignedbb);
            y = this.stepHeight;
            List<AABB> list = this.world.getCollisionBoxes(this, this.getBoundingBox().expand(d2, y, d4));
            AABB axisalignedbb2 = this.getBoundingBox();
            AABB axisalignedbb3 = axisalignedbb2.expand(d2, 0.0D, d4);
            double d8 = y;
            int j1 = 0;

            for (int k1 = list.size(); j1 < k1; ++j1) {
                d8 = list.get(j1).calculateYOffset(axisalignedbb3, d8);
            }

            axisalignedbb2 = axisalignedbb2.offset(0.0D, d8, 0.0D);
            double d18 = d2;
            int l1 = 0;

            for (int i2 = list.size(); l1 < i2; ++l1) {
                d18 = list.get(l1).calculateXOffset(axisalignedbb2, d18);
            }

            axisalignedbb2 = axisalignedbb2.offset(d18, 0.0D, 0.0D);
            double d19 = d4;
            int j2 = 0;

            for (int k2 = list.size(); j2 < k2; ++j2) {
                d19 = list.get(j2).calculateZOffset(axisalignedbb2, d19);
            }

            axisalignedbb2 = axisalignedbb2.offset(0.0D, 0.0D, d19);
            AABB axisalignedbb4 = this.getBoundingBox();
            double d20 = y;
            int l2 = 0;

            for (int i3 = list.size(); l2 < i3; ++l2) {
                d20 = list.get(l2).calculateYOffset(axisalignedbb4, d20);
            }

            axisalignedbb4 = axisalignedbb4.offset(0.0D, d20, 0.0D);
            double d21 = d2;
            int j3 = 0;

            for (int k3 = list.size(); j3 < k3; ++j3) {
                d21 = list.get(j3).calculateXOffset(axisalignedbb4, d21);
            }

            axisalignedbb4 = axisalignedbb4.offset(d21, 0.0D, 0.0D);
            double d22 = d4;
            int l3 = 0;

            for (int i4 = list.size(); l3 < i4; ++l3) {
                d22 = list.get(l3).calculateZOffset(axisalignedbb4, d22);
            }

            axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d22);
            double d23 = d18 * d18 + d19 * d19;
            double d9 = d21 * d21 + d22 * d22;

            if (d23 > d9) {
                x = d18;
                z = d19;
                y = -d8;
                this.setEntityBoundingBox(axisalignedbb2);
            } else {
                x = d21;
                z = d22;
                y = -d20;
                this.setEntityBoundingBox(axisalignedbb4);
            }

            int j4 = 0;

            for (int k4 = list.size(); j4 < k4; ++j4) {
                y = list.get(j4).calculateYOffset(this.getBoundingBox(), y);
            }

            this.setEntityBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));

            if (d14 * d14 + d7 * d7 >= x * x + z * z) {
                x = d14;
                y = d6;
                z = d7;
                this.setEntityBoundingBox(axisalignedbb1);
            }
        }

        this.world.profiler.endSection();
        this.world.profiler.startSection("rest");
        this.resetPositionToBB();
        this.collidedHorizontally = d2 != x || d4 != z;
        this.collidedVertically = d3 != y;
        this.onGround = this.collidedVertically && d3 < 0.0D;
        this.collided = this.collidedHorizontally || this.collidedVertically;
        int j6 = Mth.floor(this.posX);
        int i1 = Mth.floor(this.posY - 0.20000000298023224D);
        int k6 = Mth.floor(this.posZ);
        BlockPos blockpos = new BlockPos(j6, i1, k6);
        BlockState iblockstate = this.world.getBlockState(blockpos);

        if (iblockstate.getMaterial() == Material.AIR) {
            BlockPos blockpos1 = blockpos.down();
            BlockState iblockstate1 = this.world.getBlockState(blockpos1);
            Block block1 = iblockstate1.getBlock();

            if (block1 instanceof FenceBlock || block1 instanceof BlockWall || block1 instanceof BlockFenceGate) {
                iblockstate = iblockstate1;
                blockpos = blockpos1;
            }
        }

        this.updateFallState(y, this.onGround, iblockstate, blockpos);

        if (d2 != x) {
            this.motionX = 0.0D;
        }

        if (d4 != z) {
            this.motionZ = 0.0D;
        }

        Block block = iblockstate.getBlock();

        if (d3 != y) {
            block.onLanded(this.world, this);
        }

        try {
            this.doBlockCollisions();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
            this.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }

        this.world.profiler.endSection();
    }

    @Override
    public final void setRenderDirection(Vec3 dir) {
        if (this.renderLazerPos != null) {
            this.prevRenderLazerPos = this.renderLazerPos;
        } else {
            this.prevRenderLazerPos = dir;
        }
        this.renderLazerPos = dir;
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getDisplayName());
        }

        super.readAdditionalSaveData(compound);
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
    protected SoundEvent getAmbientSound() {
        return SoundsHandler.ENTITY_GAUNTLET_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsHandler.ENTITY_GAUNTLET_HURT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsHandler.ENTITY_GAUNTLET_HURT;
    }

    @Override
    public Optional<Vec3> getLazerTarget() {
        return Optional.ofNullable(renderLazerPos);
    }
}
