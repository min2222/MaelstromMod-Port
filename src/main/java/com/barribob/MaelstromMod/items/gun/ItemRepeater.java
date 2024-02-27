package com.barribob.MaelstromMod.items.gun;

import com.barribob.MaelstromMod.entity.projectile.Projectile;
import com.barribob.MaelstromMod.entity.projectile.ProjectileRepeater;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemRepeater extends ItemGun {
    int maxRepeats = 5;

    public ItemRepeater(String name, float level) {
        super(name, 60, 2, level);
    }

    @Override
    public void onUpdate(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("repeating")) {
            CompoundTag compound = stack.getTagCompound();
            if (!worldIn.isRemote && compound.getBoolean("repeating") && entityIn instanceof Player && entityIn.ticksExisted % 5 == 0) {
                this.repeat(worldIn, (Player) entityIn, stack);
                compound.setInteger("repeats", compound.getInteger("repeats") + 1);
                if (compound.getInteger("repeats") >= this.maxRepeats) {
                    compound.setBoolean("repeating", false);
                    compound.setInteger("repeats", 0);
                }
            }
        }
    }

    private void repeat(Level world, Player player, ItemStack stack) {
        world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundSource.NEUTRAL, 0.5F,
                0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        float inaccuracy = 4.0f;
        float velocity = 3.0f;

        Projectile projectile = new ProjectileRepeater(world, player, this.getEnchantedDamage(stack), stack);
        projectile.setElement(getElement());
        projectile.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity, inaccuracy);
        projectile.setTravelRange(30);

        world.spawnEntity(projectile);
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        if (stack.hasTagCompound()) {
            stack.getTagCompound().setBoolean("repeating", true);
        }
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("repeater"));
    }
}
