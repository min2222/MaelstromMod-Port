package com.barribob.MaelstromMod.items.gun;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.packets.MessageLeap;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemLeapStaff extends ItemStaff {
    public ItemLeapStaff(String name, int maxDamage, float level, CreativeModeTab tab) {
        super(name, maxDamage, level, tab);
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        if(player instanceof ServerPlayer) {
            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundSource.NEUTRAL, 0.5F,
                    0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
            Main.network.sendTo(new MessageLeap(), (ServerPlayer) player);
            player.fallDistance = -1;
        }
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("leap_staff"));
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("fall_damage_reduction"));
    }
}
