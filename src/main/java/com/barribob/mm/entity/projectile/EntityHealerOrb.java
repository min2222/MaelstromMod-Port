package com.barribob.mm.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.PotionEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.util.ModColors;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

import java.util.UUID;

public class EntityHealerOrb extends Entity {
    private EntityMaelstromMob owner;
    private Entity target;
    private Vec3 tempTarget;
    @Nullable
    private UUID ownerUniqueId;
    private BlockPos ownerBlockPos;
    @Nullable
    private UUID targetUniqueId;
    private BlockPos targetBlockPos;

    public EntityHealerOrb(Level worldIn) {
        super(worldIn);
        this.setSize(0.3125F, 0.3125F);
        this.noPhysics = true;
    }

    @OnlyIn(Dist.CLIENT)
    public EntityHealerOrb(Level worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
        this(worldIn);
        this.moveTo(x, y, z, this.getYRot(), this.getXRot());
        this.motionX = motionXIn;
        this.motionY = motionYIn;
        this.motionZ = motionZIn;
    }

    public EntityHealerOrb(Level worldIn, EntityMaelstromMob ownerIn, Entity targetIn) {
        this(worldIn);
        this.owner = ownerIn;
        this.setLocationAndAngles(ownerIn.posX, owner.posY, owner.posZ, this.rotationYaw, this.rotationPitch);
        this.target = targetIn;
    }

    @Override
    protected void writeEntityToNBT(CompoundTag compound) {
        if (this.owner != null) {
            BlockPos blockpos = new BlockPos(this.owner);
            CompoundTag nbttagcompound = NBTUtil.createUUIDTag(this.owner.getUniqueID());
            nbttagcompound.setInteger("X", blockpos.getX());
            nbttagcompound.setInteger("Y", blockpos.getY());
            nbttagcompound.setInteger("Z", blockpos.getZ());
            compound.setTag("Owner", nbttagcompound);
        }

        if (this.target != null) {
            BlockPos blockpos1 = new BlockPos(this.target);
            CompoundTag nbttagcompound1 = NBTUtil.createUUIDTag(this.target.getUniqueID());
            nbttagcompound1.setInteger("X", blockpos1.getX());
            nbttagcompound1.setInteger("Y", blockpos1.getY());
            nbttagcompound1.setInteger("Z", blockpos1.getZ());
            compound.setTag("Target", nbttagcompound1);
        }

        compound.setDouble("TXD", this.tempTarget.x);
        compound.setDouble("TYD", this.tempTarget.y);
        compound.setDouble("TZD", this.tempTarget.z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.tempTarget = new Vec3(compound.getDouble("TXD"), compound.getDouble("TYD"), compound.getDouble("TZD"));

        if (compound.hasKey("Owner", 10)) {
            CompoundTag nbttagcompound = compound.getCompoundTag("Owner");
            this.ownerUniqueId = NBTUtil.getUUIDFromTag(nbttagcompound);
            this.ownerBlockPos = new BlockPos(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Y"), nbttagcompound.getInteger("Z"));
        }

        if (compound.hasKey("Target", 10)) {
            CompoundTag nbttagcompound1 = compound.getCompoundTag("Target");
            this.targetUniqueId = NBTUtil.getUUIDFromTag(nbttagcompound1);
            this.targetBlockPos = new BlockPos(nbttagcompound1.getInteger("X"), nbttagcompound1.getInteger("Y"), nbttagcompound1.getInteger("Z"));
        }
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.setDead();
        } else {
            super.onUpdate();

            if (!this.level.isClientSide) {
                if (this.tempTarget == null) {
                    this.findNextTargetPosition();
                }

                /**
                 * My guess is that this reloads the target from nbt
                 */
                if (this.target == null && this.targetUniqueId != null) {
                    for (LivingEntity entitylivingbase : this.world.getEntitiesWithinAABB(LivingEntity.class, new AABB(this.targetBlockPos.add(-2, -2, -2), this.targetBlockPos.add(2, 2, 2)))) {
                        if (entitylivingbase.getUniqueID().equals(this.targetUniqueId)) {
                            this.target = entitylivingbase;
                            break;
                        }
                    }

                    this.targetUniqueId = null;
                }

                /**
                 * Reload owner from nbt
                 */
                if (this.owner == null && this.ownerUniqueId != null) {
                    for (LivingEntity entity : this.world.getEntitiesWithinAABB(LivingEntity.class, new AABB(this.ownerBlockPos.add(-2, -2, -2), this.ownerBlockPos.add(2, 2, 2)))) {
                        if (entity.getUniqueID().equals(this.ownerUniqueId) && entity instanceof EntityMaelstromMob) {
                            this.owner = (EntityMaelstromMob) entity;
                            break;
                        }
                    }

                    this.ownerUniqueId = null;
                }

                /**
                 * Default just flow down if no target
                 */
                if (this.target == null || !this.target.isEntityAlive() || this.target instanceof Player && ((Player) this.target).isSpectator()) {

                    this.target = ModUtils.closestEntityExcluding(this, this.getBoundingBox().grow(10), (entity) -> EntityMaelstromMob.isMaelstromMob(entity) && entity != this.owner);

                    if (!this.hasNoGravity()) {
                        this.motionY -= 0.04D;
                        this.motionX = 0;
                        this.motionZ = 0;
                    }
                } else {
                    Vec3 direction = this.tempTarget.subtract(position()).normalize();
                    this.motionX = direction.x * 0.3;
                    this.motionY = direction.y * 0.7;
                    this.motionZ = direction.z * 0.3;
                }

                /**
                 * Hit detection
                 */
                HitResult raytraceresult = ProjectileHelper.forwardsRaycast(this, true, false, this.owner);

                if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                    this.bulletHit(raytraceresult);
                }
            }

            /**
             * Not entirely sure, but this seems to manually update the position based on the motion
             */
            this.setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            ProjectileHelper.rotateTowardsMovement(this, 0.5F);

            if (this.level.isClientSide) {
                ParticleManager.spawnEffect(world, this.position(), ModColors.PURPLE);
            } else if (this.target != null && !this.target.isDead) {
                /**
                 * Look ahead 2 blocks and if there are blocks in the way, attempt to change direction
                 */
                Vec3 direction = this.tempTarget.subtract(position()).normalize();
                Vec3 lookahead = this.position().add(direction.scale(2));
                HitResult res = world.rayTraceBlocks(this.position(), lookahead, false, true, true);

                double distanceFromTarget = this.position().squareDistanceTo(this.tempTarget);

                if ((res != null && res.typeOfHit == HitResult.Type.BLOCK) || distanceFromTarget < 1) {
                    this.findNextTargetPosition();
                }
            }
        }
    }

    /**
     * Find the next direction to move. Prioritizes horizontal movement over vertical movement
     *
     * @return
     */
    public void findNextTargetPosition() {
        Vec3 targetPos = this.target.position().add(new Vec3(target.motionX, target.motionY, target.motionZ).scale(3));
        Vec3 diff = targetPos.subtract(this.position());
        double xMag = Math.abs(diff.x);
        double zMag = Math.abs(diff.z);

        if (xMag + zMag > 0.8) // If were close enough, just go directly to the entity
        {
            // Randomly choose x or z position, with higher weight on the larger magnitude
            // This is random mostly because it shouldn't keep choosing a direction if it gets stuck
            if (rand.nextDouble() * xMag > rand.nextDouble() * zMag) {
                targetPos = new Vec3(targetPos.x, this.posY, this.posZ); // Go in the x direction only
            } else {
                targetPos = new Vec3(this.posX, this.posY, targetPos.z); // Go in the z direction only
            }
        }

        this.tempTarget = targetPos;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 16384.0D;
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }

    protected void bulletHit(HitResult result) {
        LivingEntity entity = ModUtils.getLivingEntity(result.entityHit);
        if (EntityMaelstromMob.isMaelstromMob(entity) && entity != null && owner != null) {
            level.broadcastEntityEvent(this, ModUtils.PARTICLE_BYTE);
            this.playSound(SoundEvents.ILLAGER_CAST_SPELL, 1.0F, 1.0F);
            entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100));
            entity.heal((float) (owner.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue() * 4));
        } else if (result.entityHit != null && owner != null) {
            result.entityHit.attackEntityFrom(ModDamageSource.causeElementalThrownDamage(this, owner, owner.getElement()), owner.getAttack());
        }

        this.setDead();
    }

    /**
     * Spawn impact healing particles
     */
    @Override
    public void handleEntityEvent(byte id) {
        if (id == ModUtils.PARTICLE_BYTE) {
            for (int i = -5; i < 2; i++) {
                final float yOff = i * 0.5f;
                ModUtils.circleCallback(1, 20, (pos) -> {
                    pos = new Vec3(pos.x, yOff, pos.y);
                    ParticleManager.spawnDust(world, pos.add(this.position()), ModColors.PURPLE, ModUtils.yVec(0.1f), ModRandom.range(10, 20));
                });
            }
        }
        super.handleEntityEvent(id);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.level.isClientSide) {
            ((ServerLevel) this.world).spawnParticle(ParticleTypes.CRIT, this.posX, this.posY, this.posZ, 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.setDead();
        }

        return true;
    }

    @Override
    public SoundSource getSoundCategory() {
        return SoundSource.HOSTILE;
    }
}
