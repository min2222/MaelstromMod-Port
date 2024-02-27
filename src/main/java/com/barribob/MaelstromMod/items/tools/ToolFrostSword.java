package com.barribob.MaelstromMod.items.tools;

import com.barribob.MaelstromMod.entity.particleSpawners.ParticleSpawnerSwordSwing;
import com.barribob.MaelstromMod.items.ISweepAttackOverride;
import com.barribob.MaelstromMod.items.ISweepAttackParticles;
import com.barribob.MaelstromMod.util.Element;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;

public class ToolFrostSword extends ToolSword implements ISweepAttackOverride, ISweepAttackParticles {
    private static final float targetEntitySize = 1.0f;
    private static final Vec3 particleColor = new Vec3(0.4, 0.4, 0.7f);

    public ToolFrostSword(String name, ToolMaterial material, float level) {
        super(name, material, level, Element.AZURE);
    }

    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        ModUtils.doSweepAttack(player, target, getElement(), (e) -> {
            e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));
        });

        if (target != null) {
            target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 1));

            Entity particle = new ParticleSpawnerSwordSwing(player.world);
            particle.copyLocationAndAnglesFrom(target);
            player.world.spawnEntity(particle);
        }
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("frost_sword"));
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
