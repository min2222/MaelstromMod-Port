package com.barribob.mm.items.tools;

import java.util.List;

import com.barribob.mm.entity.particleSpawners.ParticleSpawnerSwordSwing;
import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.items.ISweepAttackParticles;
import com.barribob.mm.util.Element;
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

public class ToolFrostSword extends ToolSword implements ISweepAttackOverride, ISweepAttackParticles {
    private static final float targetEntitySize = 1.0f;
    private static final Vec3 particleColor = new Vec3(0.4, 0.4, 0.7f);

    public ToolFrostSword(String name, Tier material, float level) {
        super(name, material, level, Element.AZURE);
    }

    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        ModUtils.doSweepAttack(player, target, getElement(), (e) -> {
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        });

        if (target != null) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));

            Entity particle = new ParticleSpawnerSwordSwing(player.level);
            particle.copyPosition(target);
            player.level.addFreshEntity(particle);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("frost_sword").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Vec3 getColor() {
        return particleColor;
    }

    @Override
    public float getSize() {
        return targetEntitySize;
    }
}
