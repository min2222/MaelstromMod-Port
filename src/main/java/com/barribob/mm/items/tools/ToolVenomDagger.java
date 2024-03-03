package com.barribob.mm.items.tools;

import java.util.List;

import com.barribob.mm.entity.particleSpawners.ParticleSpawnerSwordSwing;
import com.barribob.mm.items.ISweepAttackParticles;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ToolVenomDagger extends ToolDagger implements ISweepAttackParticles {
    public ToolVenomDagger(String name, Tier material, float level) {
        super(name, material, level);
    }

    // Add a poison effect on a full attack
    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        if (target != null) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1));
            Entity particle = new ParticleSpawnerSwordSwing(player.level);
            particle.copyPosition(target);
            player.level.addFreshEntity(particle);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("venom_dagger").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Vec3 getColor() {
        return new Vec3(0.2, 0.5, 0.2);
    }

    @Override
    public float getSize() {
        return 0.5f;
    }
}
