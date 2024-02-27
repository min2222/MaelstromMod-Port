package com.barribob.MaelstromMod.entity.particleSpawners;

import com.barribob.MaelstromMod.entity.util.EntityParticleSpawner;
import com.barribob.MaelstromMod.items.ISweepAttackParticles;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.handlers.ParticleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleSpawnerSwordSwing extends EntityParticleSpawner {
    public ParticleSpawnerSwordSwing(Level worldIn) {
        super(worldIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void spawnParticles() {
        if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ISweepAttackParticles) {
            ISweepAttackParticles particleSword = (ISweepAttackParticles) Minecraft.getMinecraft().player.getHeldItemMainhand().getItem();
            for (int i = 0; i < 5; i++) {
                ParticleManager.spawnEffect(world, new Vec3(posX, posY + 1, posZ).add(ModRandom.randVec().scale(particleSword.getSize())), particleSword.getColor());
            }
        }
    }
}
