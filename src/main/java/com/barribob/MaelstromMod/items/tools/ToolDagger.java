package com.barribob.MaelstromMod.items.tools;

import com.barribob.MaelstromMod.items.IExtendedReach;
import com.barribob.MaelstromMod.items.ISweepAttackOverride;
import com.barribob.MaelstromMod.util.ModUtils;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

/**
 * The dagger tool is a high damage, but short reached weapon
 */
public class ToolDagger extends ToolSword implements IExtendedReach, ISweepAttackOverride {
    private static final UUID REACH_MODIFIER = UUID.fromString("a6323e02-d8e9-44c6-b941-f5d7155bb406");

    public ToolDagger(String name, ToolMaterial material, float level) {
        super(name, material, level);
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit
     * damage.
     */
    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(Player.REACH_DISTANCE.getName(), new AttributeModifier(REACH_MODIFIER, "Extended Reach Modifier", -1.0D, 0).setSaved(false));
        }
        return multimap;
    }

    @Override
    public float getAttackDamage() {
        return super.getAttackDamage() * 1.5f;
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
    public void addInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(ChatFormatting.GRAY + ModUtils.translateDesc("dagger"));
    }
}
