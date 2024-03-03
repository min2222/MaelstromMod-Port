package com.barribob.mm.items.tools;

import java.util.UUID;

import com.barribob.mm.items.IExtendedReach;
import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.ModUtils;
import com.google.common.collect.Multimap;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ForgeMod;

/**
 * Holds reach properties for an extended reach tool
 */
public class ToolLongsword extends ToolSword implements IExtendedReach, ISweepAttackOverride {
    private static final UUID REACH_MODIFIER = UUID.fromString("a6323e02-d8e9-44c6-b941-f5d7155bb406");
    private float reach = 4;

    public ToolLongsword(String name, Tier material, float level) {
        super(name, material, level);
    }

    @Override
    public float getReach() {
        return this.reach;
    }

    /**
     * Increased sweep attack
     */
    @Override
    public void doSweepAttack(Player player, LivingEntity target) {
        float maxDistanceSq = (float) Math.pow(this.reach, 2);
        ModUtils.doSweepAttack(player, target, getElement(), (e) -> {
        }, maxDistanceSq, 1);
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit
     * damage.
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<Attribute, AttributeModifier> multimap = super.getDefaultAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(REACH_MODIFIER, "Extended Reach Modifier", this.reach - 3.0D, Operation.ADDITION));
        }
        return multimap;
    }

    @Override
    protected double getAttackSpeed() {
        return -2.8000000953674316D;
    }
}
