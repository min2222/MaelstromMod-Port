package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.Main;
import com.barribob.mm.packets.MessageLeap;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class ItemLeapStaff extends ItemStaff {
    public ItemLeapStaff(String name, int maxDamage, float level, CreativeModeTab tab) {
        super(name, maxDamage, level, tab);
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        if(player instanceof ServerPlayer) {
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_FLAP, SoundSource.NEUTRAL, 0.5F,
                    0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
            Main.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new MessageLeap());
            player.fallDistance = -1;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("leap_staff").withStyle(ChatFormatting.GRAY));
        tooltip.add(ModUtils.translateDesc("fall_damage_reduction").withStyle(ChatFormatting.GRAY));
    }
}
