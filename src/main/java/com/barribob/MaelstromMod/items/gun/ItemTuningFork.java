package com.barribob.MaelstromMod.items.gun;

import com.barribob.MaelstromMod.config.ModConfig;
import com.barribob.MaelstromMod.entity.util.EntityTuningForkLazer;
import com.barribob.MaelstromMod.init.ModCreativeTabs;
import com.barribob.MaelstromMod.init.ModItems;
import com.barribob.MaelstromMod.util.ModDamageSource;
import com.barribob.MaelstromMod.util.ModRandom;
import com.barribob.MaelstromMod.util.ModUtils;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;

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

        Vec3 lazerEnd = player.getPositionEyes(1).add(player.getLookVec().scale(40));

        // Ray trace both blocks and entities
        RayTraceResult raytraceresult = world.rayTraceBlocks(player.getPositionEyes(1), lazerEnd, false, true, false);
        if (raytraceresult != null) {
            // If we hit a block, make sure that any collisions with entities are detected up to the hit block
            lazerEnd = raytraceresult.hitVec;
        }

        Entity closestEntity = null;
        for (Entity entity : ModUtils.findEntitiesInLine(player.getPositionEyes(1), lazerEnd, world, player)) {
            if (entity.canBeCollidedWith() && (closestEntity == null || entity.getDistanceSq(player) < closestEntity.getDistanceSq(player))) {
                closestEntity = entity;
            }
        }

        if (closestEntity != null) {
            if (closestEntity instanceof IEntityMultiPart) {
                if(closestEntity.getParts() != null) {
                    MultiPartEntityPart closestPart = null;
                    for (Entity entity : closestEntity.getParts()) {
                        RayTraceResult result = entity.getEntityBoundingBox().calculateIntercept(player.getPositionEyes(1), lazerEnd);
                        if (result != null) {
                            if (entity instanceof MultiPartEntityPart && (closestPart == null || entity.getDistanceSq(player) < closestPart.getDistanceSq(player))) {
                                closestPart = (MultiPartEntityPart) entity;
                            }
                        }
                    }
                    if (closestPart != null) {
                        lazerEnd = closestPart.getEntityBoundingBox().calculateIntercept(player.getPositionEyes(1), lazerEnd).hitVec;
                        ((IEntityMultiPart) closestEntity).attackEntityFromPart(closestPart, ModDamageSource.causeElementalPlayerDamage(player, getElement()),
                                ModUtils.getEnchantedDamage(stack, this.getLevel(), this.getBaseDamage()));
                    }
                }
            } else {
                lazerEnd = closestEntity.getEntityBoundingBox().calculateIntercept(player.getPositionEyes(1), lazerEnd).hitVec;
                closestEntity.attackEntityFrom(ModDamageSource.causeElementalPlayerDamage(player, getElement()), ModUtils.getEnchantedDamage(stack, this.getLevel(), this.getBaseDamage()));
            }
        }

        // Spawn an entity to render the ray and additional particles
        EntityTuningForkLazer renderer = new EntityTuningForkLazer(world, player.getPositionEyes(1).add(ModUtils.getAxisOffset(player.getLookVec(), new Vec3(0.5, 0, 0.5))));
        renderer.setPosition(lazerEnd.x, lazerEnd.y, lazerEnd.z);
        world.spawnEntity(renderer);
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
