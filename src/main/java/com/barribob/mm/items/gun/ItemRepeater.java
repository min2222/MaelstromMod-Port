package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.entity.projectile.ProjectileRepeater;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemRepeater extends ItemGun {
    int maxRepeats = 5;

    public ItemRepeater(String name, float level) {
        super(name, 60, 2, level);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

        if (stack.hasTag() && stack.getOrCreateTag().contains("repeating")) {
            CompoundTag compound = stack.getOrCreateTag();
            if (!worldIn.isClientSide && compound.getBoolean("repeating") && entityIn instanceof Player && entityIn.tickCount % 5 == 0) {
                this.repeat(worldIn, (Player) entityIn, stack);
                compound.putInt("repeats", compound.getInt("repeats") + 1);
                if (compound.getInt("repeats") >= this.maxRepeats) {
                    compound.putBoolean("repeating", false);
                    compound.putInt("repeats", 0);
                }
            }
        }
    }

    private void repeat(Level world, Player player, ItemStack stack) {
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_LAUNCH, SoundSource.NEUTRAL, 0.5F,
                0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 4.0f;
        float velocity = 3.0f;

        ModProjectile projectile = new ProjectileRepeater(world, player, this.getEnchantedDamage(stack), stack);
        projectile.setElement(getElement());
        projectile.shoot(player, player.getXRot(), player.getYRot(), 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(30);

        world.addFreshEntity(projectile);
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        if (stack.hasTag()) {
            stack.getOrCreateTag().putBoolean("repeating", true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("repeater").withStyle(ChatFormatting.GRAY));
    }
}
