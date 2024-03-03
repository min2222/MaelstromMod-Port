package com.barribob.mm.items;

import java.util.List;

import com.barribob.mm.Main;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.Mana;
import com.barribob.mm.mana.ManaProvider;
import com.barribob.mm.packets.MessageMana;
import com.barribob.mm.packets.MessageManaUnlock;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class ItemCatalyst extends ItemBase {
    public ItemCatalyst(String name, CreativeModeTab tab) {
        super(name, tab);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide && playerIn instanceof ServerPlayer) {
            IMana mana = playerIn.getCapability(ManaProvider.MANA).orElse(null);
            if (mana.isLocked()) {
                mana.setLocked(false);
                mana.set(Mana.MAX_MANA); // Fill the mana bar initially
                Main.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn), new MessageManaUnlock()); // Spawn celebratory particles
                Main.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn), new MessageMana(mana.getMana())); // Update the mana bar
                playerIn.playSound(SoundEvents.EVOKER_CAST_SPELL, 1.0F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

                if (!playerIn.getAbilities().instabuild) {
                    itemstack.shrink(1);
                    return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, itemstack);
                }
            }
        }
        return new InteractionResultHolder<ItemStack>(InteractionResult.PASS, itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(ModUtils.translateDesc("catalyst").withStyle(ChatFormatting.GRAY));
    }
}
