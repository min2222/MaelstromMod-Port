package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileExplosiveDrill;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemExplosiveStaff extends ItemStaff {
    public ItemExplosiveStaff(String name, int useTime, float level, CreativeModeTab tab) {
        super(name, useTime, level, tab);
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.NEUTRAL, 1.0F,
                0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 2.0f;
        float velocity = 1.3f;

        ModProjectile projectile = new ProjectileExplosiveDrill(world, player, 0, stack);
        projectile.shoot(player, player.getXRot(), player.getYRot(), 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(20);

        world.addFreshEntity(projectile);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("explosive_staff").withStyle(ChatFormatting.GRAY));
    }
}
