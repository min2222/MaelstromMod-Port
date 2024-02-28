package com.barribob.mm.entity.entities.gauntlet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileCrimsonWanderer;
import com.barribob.mm.entity.projectile.ProjectileMegaFireball;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.ParticleManager;

public class EntityAlternativeMaelstromGauntletStage2 extends EntityAbstractMaelstromGauntlet {
    private final IGauntletAction summonAttack;
    private final List<IGauntletAction> attacks;
    private final double maxLaserDistance = getMobConfig().getDouble("max_laser_distance");
    private final double maxFireballDistance = getMobConfig().getDouble("max_fireball_distance");

    public EntityAlternativeMaelstromGauntletStage2(Level worldIn) {
        super(worldIn);
        this.healthScaledAttackFactor = 0.2;
        Supplier<Vec3> position = () -> getTarget() == null ? null : getTarget().position();
        IGauntletAction swirlPunchAttack = new PunchAction("gauntlet.swirl_punch", position, this::summonWanderersAndSmoke, this, fist);
        summonAttack = new SummonMobsAction(this::spawnMob, this, fist);
        IGauntletAction laserAttack = new LaserAction(this, stopLazerByte, (vec3d) -> {});
        IGauntletAction fireballAttack = new FireballThrowAction<>((target) -> target.getEyePosition(1), this::generateFireball, this);

        IGauntletAction punchAttack = new PunchAction("gauntlet.punch", position, () -> {}, this, fist);
        ModRandom.RandomCollection<IGauntletAction> punchAttacks =
                ModRandom.choice(new IGauntletAction[]{ punchAttack, swirlPunchAttack }, rand, new double[] { 2, 1 });
        IGauntletAction comboPunchAttack = new ComboPunchAction(punchAttacks, this);
        attacks = new ArrayList<>(Arrays.asList(swirlPunchAttack, laserAttack, summonAttack, fireballAttack, comboPunchAttack));
    }

    private void spawnMob() {
        if(!trySpawnMob(true)) trySpawnMob(false);
    }

    private boolean trySpawnMob(boolean findGround) {
        EntityLeveledMob mob = ModUtils.spawnMob(world, this.getPosition(), this.getLevel(), getMobConfig().getConfig(findGround ? "summoning_algorithm" : "aerial_summoning_algorithm"), findGround);
        return mob != null;
    }

    private ModProjectile generateFireball() {
        ProjectileMegaFireball fireball = new ProjectileMegaFireball(world, this, this.getAttack() * getConfigFloat("fireball_damage"), null, false);
        fireball.setTravelRange((float) maxFireballDistance);
        return fireball;
    }

    private void summonWanderersAndSmoke() {
        level.broadcastEntityEvent(this, ModUtils.THIRD_PARTICLE_BYTE);
        int chance = getTarget() != null && getTarget().onGround ? 4 : 2;
        if(rand.nextInt(chance) == 0) {
            summonCrimsonWanderer();
        }
    }

    @Override
    protected IGauntletAction getNextAttack(LivingEntity target, float distanceSq, IGauntletAction previousAction) {
        int numMinions = (int) ModUtils.getEntitiesInBox(this, getBoundingBox().grow(50)).stream()
                .filter(EntityMaelstromMob::isMaelstromMob).count();

        double summonWeight = previousAction == summonAttack || numMinions > 3 ? 0 : 0.8;
        double fireballWeight = distanceSq < Math.pow(maxFireballDistance, 2) ? 1 : 0;
        double laserWeight = distanceSq < Math.pow(maxLaserDistance, 2) ? 1 : 0;
        double punchWeight = ModUtils.canEntityBeSeen(this, target) ? Math.sqrt(distanceSq) / 25 : 3;
        double comboPunchWeight = 0.8;

        double[] weights = {punchWeight, laserWeight, summonWeight, fireballWeight, comboPunchWeight};
        return ModRandom.choice(attacks, rand, weights).next();
    }

    private void summonCrimsonWanderer() {
        ProjectileCrimsonWanderer shrapnel = new ProjectileCrimsonWanderer(world, this, getAttack() * 0.5f);
        Vec3 lookVec = ModUtils.getLookVec(getPitch(), rotationYaw);
        Vec3 shrapnelPos = this.position()
                .add(ModRandom.randVec().scale(3))
                .subtract(lookVec.scale(6));
        ModUtils.setEntityPosition(shrapnel, shrapnelPos);
        shrapnel.setNoGravity(false);
        shrapnel.setTravelRange(50);
        level.addFreshEntity(shrapnel);
        ModUtils.setEntityVelocity(shrapnel, lookVec.scale(0.35));
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == ModUtils.THIRD_PARTICLE_BYTE) {
            for (int i = 0; i < 10; i++) {
                Vec3 lookVec = ModUtils.getLookVec(getPitch(), rotationYaw);
                Vec3 pos = ModRandom.randVec().scale(3).add(position()).subtract(lookVec.scale(3));
                ParticleManager.spawnFluff(world, pos, Vec3.ZERO, lookVec.scale(0.1));
            }
        }
        super.handleStatusUpdate(id);
    }
}
