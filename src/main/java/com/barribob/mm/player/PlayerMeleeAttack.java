package com.barribob.mm.player;

import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModDamageSource;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

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
    public static void attackTargetEntityWithCurrentItem(Player player, Entity pTarget) {
    	if (!onPlayerAttackTarget(player, pTarget)) return;
    	if (pTarget.isAttackable()) {
    		if (!pTarget.skipAttackInteraction(player)) {
    			float f = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
    			float f1;
    			if (pTarget instanceof LivingEntity) {
    				f1 = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), ((LivingEntity)pTarget).getMobType());
    			} else {
    				f1 = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), MobType.UNDEFINED);
    			}

    			float f2 = player.getAttackStrengthScale(0.5F);
    			f *= 0.2F + f2 * f2 * 0.8F;
    			f1 *= f2;
    			if (f > 0.0F || f1 > 0.0F) {
    				boolean flag = f2 > 0.9F;
    				boolean flag1 = false;
    				float i = (float)player.getAttributeValue(Attributes.ATTACK_KNOCKBACK); // Forge: Initialize player value to the attack knockback attribute of the player, which is by default 0
    				i += EnchantmentHelper.getKnockbackBonus(player);
    				if (player.isSprinting() && flag) {
    					player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, player.getSoundSource(), 1.0F, 1.0F);
    					++i;
    					flag1 = true;
    				}

    				boolean flag2 = flag && player.fallDistance > 0.0F && !player.isOnGround() && !player.onClimbable() && !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() && pTarget instanceof LivingEntity;
    				flag2 = flag2 && !player.isSprinting();
    				net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, pTarget, flag2, flag2 ? 1.5F : 1.0F);
    				flag2 = hitResult != null;
    				if (flag2) {
    					f *= hitResult.getDamageModifier();
    				}

    				f += f1;
    				boolean flag3 = false;
    				double d0 = (double)(player.walkDist - player.walkDistO);
    				if (flag && !flag2 && !flag1 && player.isOnGround() && d0 < (double)player.getSpeed()) {
    					ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
    					flag3 = itemstack.canPerformAction(net.minecraftforge.common.ToolActions.SWORD_SWEEP);
    				}

    				float f4 = 0.0F;
    				boolean flag4 = false;
    				int j = EnchantmentHelper.getFireAspect(player);
    				if (pTarget instanceof LivingEntity) {
    					f4 = ((LivingEntity)pTarget).getHealth();
    					if (j > 0 && !pTarget.isOnFire()) {
    						flag4 = true;
    						pTarget.setSecondsOnFire(1);
    					}
    				}

    				Vec3 vec3 = pTarget.getDeltaMovement();
    				boolean flag5;
                 
    				// If this is an elemental sword, then apply elemental damage
    				if (player.getMainHandItem().getItem() instanceof IElement) {
    					Element element = ((IElement) player.getMainHandItem().getItem()).getElement();
    					flag5 = pTarget.hurt(ModDamageSource.causeElementalPlayerDamage(player, element), f);
    				} else {
    					flag5 = pTarget.hurt(DamageSource.playerAttack(player), f);
    				}
                 
    				if (flag5) {
    					if (i > 0) {
    						if (pTarget instanceof LivingEntity) {
    							((LivingEntity)pTarget).knockback((double)((float)i * 0.5F), (double)Mth.sin(player.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(player.getYRot() * ((float)Math.PI / 180F))));
    						} else {
    							pTarget.push((double)(-Mth.sin(player.getYRot() * ((float)Math.PI / 180F)) * (float)i * 0.5F), 0.1D, (double)(Mth.cos(player.getYRot() * ((float)Math.PI / 180F)) * (float)i * 0.5F));
    						}

    						player.setDeltaMovement(player.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
    						player.setSprinting(false);
    					}

                        // Do the overridden sweep attack
    					Item item = player.getMainHandItem().getItem();
    					if (item instanceof ISweepAttackOverride && flag3 && pTarget instanceof LivingEntity) {
    						((ISweepAttackOverride) item).doSweepAttack(player, (LivingEntity) pTarget);
    					}

    					if (pTarget instanceof ServerPlayer && pTarget.hurtMarked) {
    						((ServerPlayer)pTarget).connection.send(new ClientboundSetEntityMotionPacket(pTarget));
    						pTarget.hurtMarked = false;
    						pTarget.setDeltaMovement(vec3);
    					}
    					
    					if (flag2) {
    						player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, player.getSoundSource(), 1.0F, 1.0F);
    						player.crit(pTarget);
    					}

    					if (!flag2 && !flag3) {
    						if (flag) {
    							player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, player.getSoundSource(), 1.0F, 1.0F);
    						} else {
    							player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, player.getSoundSource(), 1.0F, 1.0F);
    						}
    					}

    					if (f1 > 0.0F) {
    						player.magicCrit(pTarget);
    					}

    					player.setLastHurtMob(pTarget);
             	      if (pTarget instanceof LivingEntity) {
             	    	  EnchantmentHelper.doPostHurtEffects((LivingEntity)pTarget, player);
             	      }

             	      EnchantmentHelper.doPostDamageEffects(player, pTarget);
             	      ItemStack itemstack1 = player.getMainHandItem();
             	      Entity entity = pTarget;
             	      if (pTarget instanceof net.minecraftforge.entity.PartEntity) {
             	    	  entity = ((net.minecraftforge.entity.PartEntity<?>) pTarget).getParent();
             	      }

             	      if (!player.level.isClientSide && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
             	    	  ItemStack copy = itemstack1.copy();
             	    	  itemstack1.hurtEnemy((LivingEntity)entity, player);
             	    	  if (itemstack1.isEmpty()) {
             	    		  net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, InteractionHand.MAIN_HAND);
             	    		  player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
             	    	  }
             	      }

             	      if (pTarget instanceof LivingEntity) {
             	    	  float f5 = f4 - ((LivingEntity)pTarget).getHealth();
             	    	  player.awardStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
             	    	  if (j > 0) {
             	    		  pTarget.setSecondsOnFire(j * 4);
             	    	  }
             	    	  
             	    	  if (player.level instanceof ServerLevel && f5 > 2.0F) {
             	    		  int k = (int)((double)f5 * 0.5D);
             	    		  ((ServerLevel)player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, pTarget.getX(), pTarget.getY(0.5D), pTarget.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
             	    	  }
             	      }

             	      player.causeFoodExhaustion(0.1F);
    				} else {
    					player.level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, player.getSoundSource(), 1.0F, 1.0F);
    					if (flag4) {
    						pTarget.clearFire();
    					}
    				}
    			}
    			player.resetAttackStrengthTicker(); // FORGE: Moved from beginning of attack() so that getAttackStrengthScale() returns an accurate value during all attack events

    		}
    	}
    }
}
