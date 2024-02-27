package com.barribob.MaelstromMod.items.gun;

import com.barribob.MaelstromMod.entity.projectile.Projectile;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

import java.util.List;

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
        world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5F,
                0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        for (int i = 0; i < pelletCount; i++) {
            float inaccuracy = 0.0f;
            float speed = 3f;
            float pitch = player.rotationPitch + ModRandom.getFloat(15);
            float yaw = player.rotationYaw + ModRandom.getFloat(15);

            Projectile projectile = factory.get(world, player, stack, this.getEnchantedDamage(stack));
            projectile.setElement(getElement());
            projectile.shoot(player, pitch, yaw, 0.0F, speed, inaccuracy);
            projectile.setTravelRange(25f);

            world.spawnEntity(projectile);
        }
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("boomstick"));
    }
}
