package com.barribob.mm.items.tools;

import java.util.List;
import java.util.UUID;

import com.barribob.mm.items.IExtendedReach;
import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.ModUtils;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

/**
 * The dagger tool is a high damage, but short reached weapon
 */
public class ToolDagger extends ToolSword implements IExtendedReach, ISweepAttackOverride {
    private static final UUID REACH_MODIFIER = UUID.fromString("a6323e02-d8e9-44c6-b941-f5d7155bb406");

    public ToolDagger(String name, Tier material, float level) {
        super(name, material, level);
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit
     * damage.
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<Attribute, AttributeModifier> multimap = super.getDefaultAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(REACH_MODIFIER, "Extended Reach Modifier", -1.0D, Operation.ADDITION));
        }
        return multimap;
    }

    @Override
    public float getDamage() {
        return super.getDamage() * 1.5f;
    }

    @Override
    protected double getAttackSpeed() {
        return -2.1D;
    }

    @Override
    public float getReach() {
        return 2.0f;
    }

    // Remove the sweep attack for the dagger
    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ModUtils.translateDesc("dagger").withStyle(ChatFormatting.GRAY));
    }
}
