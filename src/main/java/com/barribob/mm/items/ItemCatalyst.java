package com.barribob.mm.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.util.EnumActionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.Main;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.Mana;
import com.barribob.mm.mana.ManaProvider;
import com.barribob.mm.packets.MessageMana;
import com.barribob.mm.packets.MessageManaUnlock;
import com.barribob.mm.util.ModUtils;

public class ItemCatalyst extends ItemBase {
    public ItemCatalyst(String name, CreativeModeTab tab) {
        super(name, tab);
    }

    @Override
    public InteractionResultHolder<ItemStack> onItemRightClick(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && playerIn instanceof ServerPlayer) {
            IMana mana = playerIn.getCapability(ManaProvider.MANA, null);
            if (mana.isLocked()) {
                mana.setLocked(false);
                mana.set(Mana.MAX_MANA); // Fill the mana bar initially
                Main.network.sendTo(new MessageManaUnlock(), (ServerPlayer) playerIn); // Spawn celebratory particles
                Main.network.sendTo(new MessageMana(mana.getMana()), (ServerPlayer) playerIn); // Update the mana bar
                playerIn.playSound(SoundEvents.ILLAGER_CAST_SPELL, 1.0F, 0.4F / (worldIn.rand.nextFloat() * 0.4F + 0.8F));

                if (!playerIn.capabilities.isCreativeMode) {
                    itemstack.shrink(1);
                    return new InteractionResultHolder<ItemStack>(EnumActionResult.SUCCESS, itemstack);
                }
            }
        }
        return new InteractionResultHolder<ItemStack>(EnumActionResult.PASS, itemstack);
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("catalyst"));
    }
}
