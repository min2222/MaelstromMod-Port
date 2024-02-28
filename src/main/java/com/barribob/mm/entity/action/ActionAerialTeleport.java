package com.barribob.mm.entity.action;

import com.barribob.mm.Main;
import com.barribob.mm.entity.entities.EntityLeveledMob;
import com.barribob.mm.packets.MessageModParticles;
import com.barribob.mm.particle.EnumModParticles;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

public class ActionAerialTeleport implements IAction {
    Vec3 teleportColor;

    public ActionAerialTeleport(Vec3 teleportColor) {
        this.teleportColor = teleportColor;
    }

    @Override
    public void performAction(EntityLeveledMob actor, LivingEntity target) {
        for(int i = 0; i < 50; i++) {
            Vec3 pos = ModRandom.randVec().normalize().scale(12)
                    .add(target.position());
            boolean canSee = actor.level.clip(new ClipContext(target.getEyePosition(1), pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, actor)) == null;
            Vec3 prevPos = actor.position();
            if(canSee && ModUtils.attemptTeleport(pos, actor)){
                ModUtils.lineCallback(prevPos, pos, 50, (particlePos, j) ->
                        Main.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> actor), new MessageModParticles(EnumModParticles.EFFECT, particlePos, Vec3.ZERO, teleportColor)));
                actor.level.broadcastEntityEvent(actor, ModUtils.SECOND_PARTICLE_BYTE);
                break;
            }
        }
    }
}
