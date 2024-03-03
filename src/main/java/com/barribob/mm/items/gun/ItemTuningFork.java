package com.barribob.mm.items.gun;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.util.EntityTuningForkLazer;
import com.barribob.mm.init.ModCreativeTabs;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeEntity;
import net.minecraftforge.entity.PartEntity;

public class ItemTuningFork extends ItemStaff {
    public ItemTuningFork(String name, float level) {
        super(name, ModItems.STAFF_USE_TIME, level, ModCreativeTabs.ITEMS);
    }

    public float getBaseDamage() {
        return 24 * ModConfig.balance.weapon_damage;
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound(null, player.blockPosition(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.NEUTRAL, 0.5F, 1.0f + ModRandom.getFloat(0.2f));
        world.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0f + ModRandom.getFloat(0.2f));

        Vec3 lazerEnd = player.getEyePosition(1).add(player.getLookAngle().scale(40));

        // Ray trace both blocks and entities
        HitResult raytraceresult = world.clip(new ClipContext(player.getEyePosition(1), lazerEnd, Block.COLLIDER, Fluid.NONE, player));
        if (raytraceresult != null) {
            // If we hit a block, make sure that any collisions with entities are detected up to the hit block
            lazerEnd = raytraceresult.getLocation();
        }

        Entity closestEntity = null;
        for (Entity entity : ModUtils.findEntitiesInLine(player.getEyePosition(1), lazerEnd, world, player)) {
            if (entity.canBeCollidedWith() && (closestEntity == null || entity.distanceToSqr(player) < closestEntity.distanceToSqr(player))) {
                closestEntity = entity;
            }
        }

        if (closestEntity != null) {
            if (closestEntity instanceof IForgeEntity) {
                if(closestEntity.getParts() != null) {
                    PartEntity closestPart = null;
                    for (Entity entity : closestEntity.getParts()) {
                        HitResult result = entity.getBoundingBox().calculateIntercept(player.getEyePosition(1), lazerEnd);
                        if (result != null) {
                            if (entity instanceof PartEntity && (closestPart == null || entity.distanceToSqr(player) < closestPart.distanceToSqr(player))) {
                                closestPart = (PartEntity) entity;
                            }
                        }
                    }
                    if (closestPart != null) {
                        lazerEnd = closestPart.getBoundingBox().calculateIntercept(player.getEyePosition(1), lazerEnd).hitVec;
                        closestEntity.hurt(ModDamageSource.causeElementalPlayerDamage(player, getElement()),
                                ModUtils.getEnchantedDamage(stack, this.getLevel(), this.getBaseDamage()));
                    }
                }
            } else {
                lazerEnd = closestEntity.getBoundingBox().calculateIntercept(player.getEyePosition(1), lazerEnd).hitVec;
                closestEntity.hurt(ModDamageSource.causeElementalPlayerDamage(player, getElement()), ModUtils.getEnchantedDamage(stack, this.getLevel(), this.getBaseDamage()));
            }
        }

        // Spawn an entity to render the ray and additional particles
        EntityTuningForkLazer renderer = new EntityTuningForkLazer(world, player.getEyePosition(1).add(ModUtils.getAxisOffset(player.getLookAngle(), new Vec3(0.5, 0, 0.5))));
        renderer.setPos(lazerEnd.x, lazerEnd.y, lazerEnd.z);
        world.addFreshEntity(renderer);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.getDamageTooltip(ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage())));
        tooltip.add(ModUtils.translateDesc("tuning_fork").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean doesDamage() {
        return true;
    }
}
