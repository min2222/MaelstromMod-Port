package com.barribob.mm.event_handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.barribob.mm.Main;
import com.barribob.mm.init.ModEnchantments;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.ManaProvider;
import com.barribob.mm.packets.MessageMana;
import com.barribob.mm.util.ModUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber()
public class ItemToManaSystem {

    @SubscribeEvent
    public static void onAttemptToUseItem(PlayerInteractEvent.RightClickItem event) {

        if(event.getSide() == LogicalSide.CLIENT) return;

        ItemStack itemStack = event.getItemStack();
        Config config = getManaConfig(itemStack);

        if(config != null) {
            int manaCost = config.getInt("mana_cost");
            CompoundTag compound = itemStack.getOrCreateTag();
            Player player = event.getEntity();
            IMana manaCapability = player.getCapability(ManaProvider.MANA).orElse(null);
            float mana = manaCapability.getMana();

            if (manaCapability.isLocked() && manaCost != 0 && !player.getAbilities().instabuild) {
                cancelUse(event, player, true);
                return;
            }

            if (compound == null || !compound.contains("cooldown")) {
                cancelUse(event, player, false);
                return;
            }

            int cooldown = compound.getInt("cooldown");
            if(cooldown > 0) {
                cancelUse(event, player, false);
                return;
            }

            if(mana < manaCost && !player.getAbilities().instabuild) {
                cancelUse(event, player, false);
                return;
            }

            if (!player.getAbilities().instabuild && manaCost != 0 && player instanceof ServerPlayer serverPlayer) {
                manaCapability.consume(manaCost);
                Main.NETWORK.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new MessageMana(manaCapability.getMana()));
            }
            compound.putInt("cooldown", (int) getEnchantedCooldown(itemStack));
            itemStack.setTag(compound);
        }
    }

    private static void cancelUse(PlayerInteractEvent.RightClickItem event, Player player, boolean sendMessage) {
        if(sendMessage) {
            player.sendSystemMessage(Component.translatable("mana_locked"));
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
    }

    @SubscribeEvent
    public static void aiStep(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            for(ItemStack itemStack : player.getInventory().items) {
                updateCooldowns(itemStack);
            }
            for(ItemStack itemStack : player.getInventory().offhand) {
                updateCooldowns(itemStack);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onTooltip(ItemTooltipEvent event) {
        Config config = getManaConfig(event.getItemStack());
        if (config != null) {
        	List<Component> components = event.getToolTip();
            List<String> tooltips = new ArrayList<>();
        	components.forEach(t -> {
        		tooltips.add(t.getString());
        	});
            ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
            Optional<String> nbtTooltips = tooltips.stream().filter(tooltip ->
                    registryName != null && tooltip.contains(registryName.toString())).findFirst();

            String cooldownTooltip = ModUtils.getCooldownTooltip(ItemToManaSystem.getEnchantedCooldown(event.getItemStack()));
            int manaCost = config.getInt("mana_cost");
            String manaTooltip = ChatFormatting.GRAY + ModUtils.translateDesc("mana_cost") + ": " + ChatFormatting.DARK_PURPLE + manaCost;

            if (nbtTooltips.isPresent()) {
                int index = tooltips.indexOf(nbtTooltips.get());
                addTooltips(tooltips, cooldownTooltip, manaCost, manaTooltip, index);
            } else {
                addTooltips(tooltips, cooldownTooltip, manaCost, manaTooltip, tooltips.size());
            }
        }
    }

    private static void addTooltips(List<String> tooltips, String cooldownTooltip, int manaCost, String manaTooltip, int index) {
        if(manaCost != 0) {
            tooltips.add(index, manaTooltip);
        }
        tooltips.add(index, cooldownTooltip);
    }

    private static void updateCooldowns(ItemStack stack) {
        Config config = getManaConfig(stack);
        if (config != null) {
            CompoundTag compound = stack.getOrCreateTag();
            if(compound == null) {
                compound = new CompoundTag();
            }

            if (compound.contains("cooldown")) {
                int cooldown = compound.getInt("cooldown") - 1;
                compound.putInt("cooldown", Math.max(cooldown, 0));
            } else {
                int initialCooldown = (int) getEnchantedCooldown(stack);
                compound.putInt("cooldown", initialCooldown);
            }

            stack.setTag(compound);
        }
    }

    @Nullable
    public static Config getManaConfig(ItemStack itemStack) {
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        if(registryName != null) {
            String registryPath = registryName.toString().replace(':', '.');
            try {
                if (Main.manaConfig.hasPath(registryPath)) {
                    return Main.manaConfig.getConfig(registryPath);
                }
            } catch(ConfigException.BadPath ignored) {
            }
        }

        return null;
    }

    public static float getEnchantedCooldown(ItemStack stack) {
        Config config = getManaConfig(stack);
        if (config == null) return 0;
        int reload = stack.getEnchantmentLevel(ModEnchantments.reload);
        return config.getInt("cooldown_in_ticks") * (1 - reload * 0.1f);
    }

    public static float getCooldownForDisplay(ItemStack stack) {
        if (stack.hasTag() && stack.getOrCreateTag().contains("cooldown")) {
            float enchantedCooldown = getEnchantedCooldown(stack);
            if(enchantedCooldown == 0) return 0;
            return stack.getOrCreateTag().getInt("cooldown") / enchantedCooldown;
        }

        return 0;
    }
}
