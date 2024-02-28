package com.barribob.mm.player;

import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModDamageSource;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.*;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * Methods taken from the player class to implement a custom sweep event
 */
public class PlayerMeleeAttack {
    private static boolean onPlayerAttackTarget(Player player, Entity target) {
        ItemStack stack = player.getMainHandItem();
        return stack.isEmpty() || !stack.getItem().onLeftClickEntity(stack, player, target);
    }

    /**
     * Attacks for the player the targeted entity with the currently equipped item.
     * The equipped item has hitEntity called on it. Args: targetEntity
     */
    public static void attackTargetEntityWithCurrentItem(Player player, Entity targetEntity) {
        if (!onPlayerAttackTarget(player, targetEntity))
            return;

        if (targetEntity.canBeAttackedWithItem()) {
            if (!targetEntity.hitByEntity(player)) {
                float damage = (float) player.getEntityAttribute(Attributes.ATTACK_DAMAGE).getAttributeValue();
                float bonusDamage;

                if (targetEntity instanceof LivingEntity) {
                    bonusDamage = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), ((LivingEntity) targetEntity).getCreatureAttribute());
                } else {
                    bonusDamage = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);
                }

                float atkCooldown = player.getCooledAttackStrength(0.5F);
                damage = damage * (0.2F + atkCooldown * atkCooldown * 0.8F);
                bonusDamage = bonusDamage * atkCooldown;
                player.resetCooldown();

                if (damage > 0.0F || bonusDamage > 0.0F) {
                    boolean cooldownCharged = atkCooldown > 0.9F;
                    boolean sprintAtk = false;
                    int knockback = 0;
                    knockback = knockback + EnchantmentHelper.getKnockbackModifier(player);

                    if (player.isSprinting() && cooldownCharged) {
                        player.world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.PLAYER_ATTACK_KNOCKBACK,
                                player.getSoundCategory(), 1.0F, 1.0F);
                        ++knockback;
                        sprintAtk = true;
                    }

                    boolean critical = cooldownCharged && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater()
                            && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof LivingEntity;
                    critical = critical && !player.isSprinting();

                    net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, targetEntity, critical,
                            critical ? 1.5F : 1.0F);
                    critical = hitResult != null;
                    if (critical) {
                        damage *= hitResult.getDamageModifier();
                    }

                    damage = damage + bonusDamage;
                    boolean sweepAttack = false;
                    double d0 = player.distanceWalkedModified - player.prevDistanceWalkedModified;

                    if (cooldownCharged && !critical && !sprintAtk && player.onGround && d0 < player.getAIMoveSpeed()) {
                        sweepAttack = true;
                    }

                    float health = 0.0F;
                    boolean hasFireAspect = false;
                    int fireAspect = EnchantmentHelper.getFireAspectModifier(player);

                    if (targetEntity instanceof LivingEntity) {
                        health = ((LivingEntity) targetEntity).getHealth();

                        if (fireAspect > 0 && !targetEntity.isBurning()) {
                            hasFireAspect = true;
                            targetEntity.setFire(1);
                        }
                    }

                    double mx = targetEntity.motionX;
                    double my = targetEntity.motionY;
                    double mz = targetEntity.motionZ;

                    boolean attackSuccessful;
                    // If this is an elemental sword, then apply elemental damage
                    if (player.getHeldItemMainhand().getItem() instanceof IElement) {
                        Element element = ((IElement) player.getHeldItemMainhand().getItem()).getElement();
                        attackSuccessful = targetEntity.attackEntityFrom(ModDamageSource.causeElementalPlayerDamage(player, element), damage);
                    } else {
                        attackSuccessful = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
                    }

                    if (attackSuccessful) {
                        // Apply knockback
                        if (knockback > 0) {
                            if (targetEntity instanceof LivingEntity) {
                                ((LivingEntity) targetEntity).knockBack(player, knockback * 0.5F, Mth.sin(player.rotationYaw * 0.017453292F),
                                        (-Mth.cos(player.rotationYaw * 0.017453292F)));
                            } else {
                                targetEntity.addVelocity(-Mth.sin(player.rotationYaw * 0.017453292F) * knockback * 0.5F, 0.1D,
                                        Mth.cos(player.rotationYaw * 0.017453292F) * knockback * 0.5F);
                            }

                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            player.setSprinting(false);
                        }

                        // Do the overridden sweep attack
                        Item item = player.getHeldItemMainhand().getItem();
                        if (item instanceof ISweepAttackOverride && sweepAttack && targetEntity instanceof LivingEntity) {
                            ((ISweepAttackOverride) item).doSweepAttack(player, (LivingEntity) targetEntity);
                        }

                        if (targetEntity instanceof ServerPlayer && targetEntity.velocityChanged) {
                            ((ServerPlayer) targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = mx;
                            targetEntity.motionY = my;
                            targetEntity.motionZ = mz;
                        }

                        if (critical) {
                            player.world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.PLAYER_ATTACK_CRIT,
                                    player.getSoundCategory(), 1.0F, 1.0F);
                            player.onCriticalHit(targetEntity);
                        }

                        if (!critical && !sweepAttack) {
                            if (cooldownCharged) {
                                player.world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.PLAYER_ATTACK_STRONG,
                                        player.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                player.world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.PLAYER_ATTACK_WEAK,
                                        player.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if (bonusDamage > 0.0F) {
                            player.onEnchantmentCritical(targetEntity);
                        }

                        player.setLastAttackedEntity(targetEntity);

                        if (targetEntity instanceof LivingEntity) {
                            EnchantmentHelper.applyThornEnchantments((LivingEntity) targetEntity, player);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
                        ItemStack itemstack1 = player.getHeldItemMainhand();
                        Entity entity = targetEntity;

                        if (targetEntity instanceof MultiPartEntityPart) {
                            IEntityMultiPart ientitymultipart = ((MultiPartEntityPart) targetEntity).parent;

                            if (ientitymultipart instanceof LivingEntity) {
                                entity = (LivingEntity) ientitymultipart;
                            }
                        }

                        // Decrements item damage I think
                        if (!itemstack1.isEmpty() && entity instanceof LivingEntity) {
                            ItemStack beforeHitCopy = itemstack1.copy();
                            itemstack1.hitEntity((LivingEntity) entity, player);

                            if (itemstack1.isEmpty()) {
                                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, InteractionHand.MAIN_HAND);
                                player.setHeldItem(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                            }
                        }

                        if (targetEntity instanceof LivingEntity) {
                            float f5 = health - ((LivingEntity) targetEntity).getHealth();
                            player.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0F));

                            if (fireAspect > 0) {
                                targetEntity.setFire(fireAspect * 4);
                            }

                            if (player.world instanceof ServerLevel && f5 > 2.0F) {
                                int k = (int) (f5 * 0.5D);
                                ((ServerLevel) player.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, targetEntity.posX,
                                        targetEntity.posY + targetEntity.height * 0.5F, targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        player.addExhaustion(0.1F);
                    } else {
                        player.world.playSound((Player) null, player.posX, player.posY, player.posZ, SoundEvents.PLAYER_ATTACK_NODAMAGE,
                                player.getSoundCategory(), 1.0F, 1.0F);

                        if (hasFireAspect) {
                            targetEntity.extinguish();
                        }
                    }
                }
            }
        }
    }
}
