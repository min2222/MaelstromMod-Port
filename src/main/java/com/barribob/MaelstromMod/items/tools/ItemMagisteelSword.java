package com.barribob.MaelstromMod.items.tools;

import com.barribob.MaelstromMod.entity.projectile.Projectile;
import com.barribob.MaelstromMod.entity.projectile.ProjectileSwordSlash;
import com.barribob.MaelstromMod.util.Element;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;

import java.util.List;

public class ItemMagisteelSword extends ToolSword {
    public ItemMagisteelSword(String name, ToolMaterial material, float level, Element element) {
        super(name, material, level, element);
    }

    @Override
    public boolean onEntitySwing(LivingEntity entityLiving, ItemStack stack) {
        if (!entityLiving.world.isRemote && entityLiving instanceof Player) {
            Player player = (Player) entityLiving;
            float attackDamage = (float) player.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue();
            float atkCooldown = player.getCooledAttackStrength(0.5F);

            if (atkCooldown > 0.9F) {
                Projectile proj = new ProjectileSwordSlash(player.world, player, attackDamage);
                proj.setElement(this.getElement());
                proj.setTravelRange(4.5f);
                proj.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5f, 0);
                player.world.spawnEntity(proj);
                player.world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F,
                        0.9F);
                if (!player.capabilities.isCreativeMode) {
                    stack.damageItem(1, player);
                }
            }
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("magisteel_sword"));
    }
}
