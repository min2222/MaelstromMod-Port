package com.barribob.mm.items.tools;

import com.barribob.mm.items.IExtendedReach;
import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.ModUtils;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * Holds reach properties for an extended reach tool
 */
public class ToolLongsword extends ToolSword implements IExtendedReach, ISweepAttackOverride {
    private static final UUID REACH_MODIFIER = UUID.fromString("a6323e02-d8e9-44c6-b941-f5d7155bb406");
    private float reach = 4;

    public ToolLongsword(String name, ToolMaterial material, float level) {
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
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(Player.REACH_DISTANCE.getName(), new AttributeModifier(REACH_MODIFIER, "Extended Reach Modifier", this.reach - 3.0D, 0).setSaved(false));
        }
        return multimap;
    }

    @Override
    protected double getAttackSpeed() {
        return -2.8000000953674316D;
    }
}
