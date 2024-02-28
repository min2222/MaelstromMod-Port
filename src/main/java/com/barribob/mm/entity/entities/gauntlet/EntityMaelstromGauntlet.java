package com.barribob.mm.entity.entities.gauntlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.barribob.mm.entity.entities.EntityMaelstromMob;
import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileMegaFireball;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityMaelstromGauntlet extends EntityAbstractMaelstromGauntlet {
    Supplier<Vec3> position = () -> getTarget() == null ? null : getTarget().position();
    private final IGauntletAction punchAttack = new PunchAction("gauntlet.punch", position, () -> {
    }, this, fist);
    private final IGauntletAction laserAttack = new LaserAction(this, stopLazerByte, (vec3d) -> {
    });
    private final IGauntletAction summonAttack = new SummonMobsAction(this::spawnMob, this, fist);
    private final IGauntletAction fireballAttack = new FireballThrowAction<>((target) -> target.getEyePosition(1), this::generateFireball, this);
    private final double fireballHealth = getMobConfig().getDouble("use_fireball_at_health");
    private final double lazerHealth = getMobConfig().getDouble("use_lazer_at_health");
    private final double spawnHealth = getMobConfig().getDouble("use_spawning_at_health");

    public EntityMaelstromGauntlet(Level worldIn) {
        super(worldIn);
    }

    private void spawnMob() {
        ModUtils.spawnMob(level, this.blockPosition(), this.getMobLevel(), getMobConfig().getConfig("summoning_algorithm"));
    }

    private ModProjectile generateFireball() {
        ProjectileMegaFireball fireball = new ProjectileMegaFireball(level, this, this.getAttack() * getConfigFloat("fireball_damage"), null, true);
        fireball.setTravelRange(30);
        return fireball;
    }

    @Override
    protected IGauntletAction getNextAttack(LivingEntity target, float distanceSq, IGauntletAction previousAction) {
        List<IGauntletAction> attacks = new ArrayList<>(Arrays.asList(punchAttack, laserAttack, summonAttack, fireballAttack));
        int numMinions = (int) ModUtils.getEntitiesInBox(this, getBoundingBox().inflate(20, 10, 20)).stream().filter(EntityMaelstromMob::isMaelstromMob).count();

        double defendWeight = previousAction == this.summonAttack || numMinions > 3 || this.getHealth() > spawnHealth ? 0 : 0.8;
        double fireballWeight = distanceSq < Math.pow(25, 2) && this.getHealth() < fireballHealth ? 1 : 0;
        double lazerWeight = distanceSq < Math.pow(35, 2) && this.getHealth() < lazerHealth ? 1 : 0;
        double punchWeight = ModUtils.canEntityBeSeen(this, target) ? Math.sqrt(distanceSq) / 25 : 3;

        double[] weights = {punchWeight, lazerWeight, defendWeight, fireballWeight};
        return ModRandom.choice(attacks, random, weights).next();
    }
}
