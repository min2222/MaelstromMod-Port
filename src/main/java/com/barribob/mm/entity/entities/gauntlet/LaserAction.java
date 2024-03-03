package com.barribob.mm.entity.entities.gauntlet;

import java.util.function.Consumer;

import com.barribob.mm.Main;
import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.init.ModBBAnimations;
import com.barribob.mm.packets.MessageDirectionForRender;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.SoundsHandler;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class LaserAction implements IGauntletAction{
    private final EntityLeveledMob entity;
    private boolean isShootingLazer;
    double maxLaserDistance;
    private final float laserExplosionSize;
    int beamLag;
    private final byte stopLaserByte;
    private final Consumer<Vec3> onLaserImpact;

    public LaserAction(EntityLeveledMob entity, byte stopLaserByte, Consumer<Vec3> onLaserImpact) {
        this.entity = entity;
        maxLaserDistance = entity.getMobConfig().getDouble("max_laser_distance");
        beamLag = entity.getMobConfig().getInt("beam_lag");
        laserExplosionSize = entity.getMobConfig().getInt("laser_explosion_size");
        this.stopLaserByte = stopLaserByte;
        this.onLaserImpact = onLaserImpact;
    }

    @Override
    public void doAction() {
        ModBBAnimations.animation(entity, "gauntlet.lazer_eye", false);
        entity.playSound(SoundsHandler.ENTITY_GAUNTLET_LAZER_CHARGE.get(), 2.0f, ModRandom.getFloat(0.2f) + 1.5f);
        int chargeUpTime = 25;
        int laserEndTime = 60;
        for (int i = 0; i < chargeUpTime; i++) {
            entity.addEvent(() -> entity.level.broadcastEntityEvent(entity, ModUtils.PARTICLE_BYTE), i);
        }
        entity.addEvent(() -> isShootingLazer = true, chargeUpTime);
        entity.addEvent(() -> {
            isShootingLazer = false;
            // Have to add delay because there will be 5 more ticks of lazers
            entity.addEvent(() -> entity.level.broadcastEntityEvent(entity, stopLaserByte), beamLag + 1);
        }, laserEndTime);
    }

    @Override
    public void update() {
        if (this.isShootingLazer) {
            if (entity.getTarget() != null) {
                Vec3 laserShootPos = entity.getTarget().position();
                entity.addEvent(() -> {

                    // Extend shooting beyond the target position up to 40 blocks
                    Vec3 laserDirection = laserShootPos.subtract(entity.getEyePosition(1)).normalize();
                    Vec3 lazerPos = laserShootPos.add(laserDirection.scale(maxLaserDistance));
                    // Ray trace both blocks and entities
                    HitResult raytraceresult = entity.level.clip(new ClipContext(entity.getEyePosition(1), lazerPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
                    if (raytraceresult != null) {
                        lazerPos = onLaserImpact(raytraceresult);
                    }

                    for (Entity target : ModUtils.findEntitiesInLine(entity.getEyePosition(1), lazerPos, entity.level, entity)) {
                        DamageSource source = ModDamageSource.builder()
                                .directEntity(entity)
                                .stoppedByArmorNotShields()
                                .element(entity.getElement())
                                .type(ModDamageSource.MAGIC)
                                .build();
                        target.hurt(source, entity.getAttack() * entity.getConfigFloat("laser_damage"));
                    }

                    ModUtils.addEntityVelocity(entity, laserDirection.scale(-0.03f));

                    Main.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new MessageDirectionForRender(entity, lazerPos));
                }, beamLag);
            } else {
                // Prevent the gauntlet from instantly locking onto other targets with the lazer.
                isShootingLazer = false;
                entity.addEvent(() -> entity.level.broadcastEntityEvent(entity, stopLaserByte), beamLag + 1);
            }
        }
    }

    private Vec3 onLaserImpact(HitResult raytraceresult) {
        Vec3 hitPos = raytraceresult.getLocation();
        if(laserExplosionSize > 0) {
            entity.level.explode(entity, hitPos.x, hitPos.y, hitPos.z, laserExplosionSize, ModUtils.mobGriefing(entity.level, entity) ? BlockInteraction.DESTROY : BlockInteraction.NONE);
        }

        onLaserImpact.accept(hitPos);

        if(entity.tickCount % 2 == 0) {
            ModUtils.destroyBlocksInAABB(ModUtils.vecBox(hitPos, hitPos).inflate(0.1), entity.level, entity);
        }
        return hitPos;
    }

    @Override
    public int attackLength() {
        return 60;
    }
}
