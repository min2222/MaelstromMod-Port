package com.barribob.mm.entity.entities.gauntlet;

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

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class EntityAlternativeMaelstromGauntletStage1 extends EntityAbstractMaelstromGauntlet {
    private final IGauntletAction summonAttack = new SummonMobsAction(this::spawnMob, this, fist);
    private final double fireballHealth = getMobConfig().getDouble("use_fireball_at_health");
    private final double lazerHealth = getMobConfig().getDouble("use_lazer_at_health");
    private final double spawnHealth = getMobConfig().getDouble("use_spawning_at_health");
    private final double maxLaserDistance = getMobConfig().getDouble("max_laser_distance");
    private final double maxFireballDistance = getMobConfig().getDouble("max_fireball_distance");

    List<IGauntletAction> attacks;

    public EntityAlternativeMaelstromGauntletStage1(Level worldIn) {
        super(worldIn);
        Supplier<Vec3> position = () -> getLazerTarget() == null ? null : getTarget().position();
        IGauntletAction punchAttack = new PunchAction("gauntlet.punch", position, () -> {
        }, this, fist);
        IGauntletAction laserAttack = new LaserAction(this, stopLazerByte, (vec3d) -> {
        });
        IGauntletAction fireballAttack = new FireballThrowAction<>((target) -> target.getEyePosition(1), this::generateFireball, this);
        attacks = new ArrayList<>(Arrays.asList(punchAttack, laserAttack, summonAttack, fireballAttack));
    }

    private void spawnMob() {
        if(!trySpawnMob(true)) trySpawnMob(false);
    }

    private boolean trySpawnMob(boolean findGround) {
        EntityLeveledMob mob = ModUtils.spawnMob(level, this.blockPosition(), this.getMobLevel(), getMobConfig().getConfig(findGround ? "summoning_algorithm" : "aerial_summoning_algorithm"), findGround);
        return mob != null;
    }

    private ModProjectile generateFireball() {
        ProjectileMegaFireball fireball = new ProjectileMegaFireball(level, this, this.getAttack() * getConfigFloat("fireball_damage"), null, false);
        fireball.setTravelRange((float) maxFireballDistance);
        return fireball;
    }

    @Override
    protected IGauntletAction getNextAttack(LivingEntity target, float distanceSq, IGauntletAction previousAction) {
        int numMinions = (int) ModUtils.getEntitiesInBox(this, getBoundingBox().inflate(50))
                .stream().filter(EntityMaelstromMob::isMaelstromMob).count();

        double defendWeight = previousAction == this.summonAttack || numMinions > 3 || this.getHealth() > spawnHealth ? 0 : 0.8;
        double fireballWeight = distanceSq < Math.pow(maxFireballDistance, 2) && this.getHealth() < fireballHealth ? 1 : 0;
        double laserWeight = distanceSq < Math.pow(maxLaserDistance, 2) && this.getHealth() < lazerHealth ? 1 : 0;
        double punchWeight = ModUtils.canEntityBeSeen(this, target) ? Math.sqrt(distanceSq) / 25 : 3;

        double[] weights = {punchWeight, laserWeight, defendWeight, fireballWeight};
        return ModRandom.choice(attacks, random, weights).next();
    }

    @Override
    public void die(DamageSource cause) {
        EntityAlternativeMaelstromGauntletStage2 secondStage = new EntityAlternativeMaelstromGauntletStage2(level);
        secondStage.copyPosition(this);
        secondStage.setYHeadRot(this.yHeadRot);
        if(!level.isClientSide) {
            secondStage.finalizeSpawn((ServerLevelAccessor) this.level, level.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            secondStage.setLevel(getMobLevel());
            secondStage.setElement(getElement());
            secondStage.setTarget(getTarget());
            level.addFreshEntity(secondStage);
            for(int i = 0; i < 30; i++) {
                summonCrimsonWanderer();
            }
        }
        this.setPos(0, 0, 0);
        super.die(cause);
    }

    private void summonCrimsonWanderer() {
        ProjectileCrimsonWanderer shrapnel = new ProjectileCrimsonWanderer(level, this, getAttack() * 0.5f);
        Vec3 shrapnelPos = this.position()
                .add(ModRandom.randVec().normalize().scale(4));
        ModUtils.setEntityPosition(shrapnel, shrapnelPos);
        shrapnel.setNoGravity(false);
        shrapnel.setTravelRange(30);
        level.addFreshEntity(shrapnel);
        Vec3 vel = ModUtils.direction(getEyePosition(1), shrapnelPos).scale(0.1);
        ModUtils.setEntityVelocity(shrapnel, vel);
    }
}
