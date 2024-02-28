package com.barribob.mm.items.gun;

import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.entity.util.EntityTuningForkLazer;
import com.barribob.mm.init.ModCreativeTabs;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.ModDamageSource;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.ModUtils;

public class ItemTuningFork extends ItemStaff {
    public ItemTuningFork(String name, float level) {
        super(name, ModItems.STAFF_USE_TIME, level, ModCreativeTabs.ITEMS);
    }

    public float getBaseDamage() {
        return 24 * ModConfig.balance.weapon_damage;
    }

    @Override
    protected void shoot(Level world, Player player, InteractionHand handIn, ItemStack stack) {
        world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_BELL, SoundSource.NEUTRAL, 0.5F, 1.0f + ModRandom.getFloat(0.2f));
        world.playSound(null, player.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 1.0f + ModRandom.getFloat(0.2f));

        Vec3 lazerEnd = player.getEyePosition(1).add(player.getLookVec().scale(40));

        // Ray trace both blocks and entities
        HitResult raytraceresult = world.rayTraceBlocks(player.getEyePosition(1), lazerEnd, false, true, false);
        if (raytraceresult != null) {
            // If we hit a block, make sure that any collisions with entities are detected up to the hit block
            lazerEnd = raytraceresult.hitVec;
        }

        Entity closestEntity = null;
        for (Entity entity : ModUtils.findEntitiesInLine(player.getEyePosition(1), lazerEnd, world, player)) {
            if (entity.canBeCollidedWith() && (closestEntity == null || entity.getDistanceSq(player) < closestEntity.getDistanceSq(player))) {
                closestEntity = entity;
            }
        }

        if (closestEntity != null) {
            if (closestEntity instanceof IEntityMultiPart) {
                if(closestEntity.getParts() != null) {
                    MultiPartEntityPart closestPart = null;
                    for (Entity entity : closestEntity.getParts()) {
                        HitResult result = entity.getBoundingBox().calculateIntercept(player.getEyePosition(1), lazerEnd);
                        if (result != null) {
                            if (entity instanceof MultiPartEntityPart && (closestPart == null || entity.getDistanceSq(player) < closestPart.getDistanceSq(player))) {
                                closestPart = (MultiPartEntityPart) entity;
                            }
                        }
                    }
                    if (closestPart != null) {
                        lazerEnd = closestPart.getBoundingBox().calculateIntercept(player.getEyePosition(1), lazerEnd).hitVec;
                        ((IEntityMultiPart) closestEntity).attackEntityFromPart(closestPart, ModDamageSource.causeElementalPlayerDamage(player, getElement()),
                                ModUtils.getEnchantedDamage(stack, this.getLevel(), this.getBaseDamage()));
                    }
                }
            } else {
                lazerEnd = closestEntity.getBoundingBox().calculateIntercept(player.getEyePosition(1), lazerEnd).hitVec;
                closestEntity.attackEntityFrom(ModDamageSource.causeElementalPlayerDamage(player, getElement()), ModUtils.getEnchantedDamage(stack, this.getLevel(), this.getBaseDamage()));
            }
        }

        // Spawn an entity to render the ray and additional particles
        EntityTuningForkLazer renderer = new EntityTuningForkLazer(world, player.getEyePosition(1).add(ModUtils.getAxisOffset(player.getLookVec(), new Vec3(0.5, 0, 0.5))));
        renderer.setPosition(lazerEnd.x, lazerEnd.y, lazerEnd.z);
        level.addFreshEntity(renderer);
    }

    @Override
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.getDamageTooltip(ModUtils.getEnchantedDamage(stack, this.getLevel(), getBaseDamage())));
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("tuning_fork"));
    }

    @Override
    public boolean doesDamage() {
        return true;
    }
}
