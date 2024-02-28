package com.barribob.mm.items.tools;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.entity.particleSpawners.ParticleSpawnerSwordSwing;
import com.barribob.mm.items.ISweepAttackParticles;
import com.barribob.mm.util.ModUtils;

public class ToolVenomDagger extends ToolDagger implements ISweepAttackParticles {
    public ToolVenomDagger(String name, ToolMaterial material, float level) {
        super(name, material, level);
    }

    // Add a poison effect on a full attack
    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        if (target != null) {
            target.addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 1));
            Entity particle = new ParticleSpawnerSwordSwing(player.world);
            particle.copyLocationAndAnglesFrom(target);
            player.level.addFreshEntity(particle);
        }
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("venom_dagger"));
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
