package com.barribob.mm.items.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.entity.projectile.ModProjectile;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

/**
 * The shotgun like weapon
 */
public class ItemBoomstick extends ItemGun {
    protected float pelletCount = 15;

    public ItemBoomstick(String name, float level) {
        super(name, 60, 1, level);
    }

    /**
     * Shoot a bunch of projectiles
     */
    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F,
                0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        for (int i = 0; i < pelletCount; i++) {
            float inaccuracy = 0.0f;
            float speed = 3f;
            float pitch = player.getXRot() + ModRandom.getFloat(15);
            float yaw = player.getYRot() + ModRandom.getFloat(15);

            ModProjectile projectile = factory.get(world, player, stack, this.getEnchantedDamage(stack));
            projectile.setElement(getElement());
            projectile.shoot(player, pitch, yaw, 0.0F, speed, inaccuracy);
            projectile.setTravelRange(25f);

            level.addFreshEntity(projectile);
        }
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("boomstick"));
    }
}
