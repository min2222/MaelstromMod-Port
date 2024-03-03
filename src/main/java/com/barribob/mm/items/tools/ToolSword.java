package com.barribob.mm.items.tools;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.init.ModCreativeTabs;
import com.barribob.mm.items.ILeveledItem;
import com.barribob.mm.items.ISweepAttackOverride;
import com.barribob.mm.util.Element;
import com.barribob.mm.util.IElement;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.handlers.LevelHandler;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ToolSword extends SwordItem implements ISweepAttackOverride, ILeveledItem, IElement {
    private float level;
    private Consumer<List<Component>> information = (info) -> {
    };
    private Element element = Element.NONE;

    public ToolSword(String name, Tier material, float level) {
        super(material, 0, 0, new Item.Properties().tab(ModCreativeTabs.ITEMS).stacksTo(1));
        this.level = level;
    }

    public ToolSword(String name, Tier material, float level, Element element) {
        this(name, material, level);
        this.element = element;
    }

    @Override
    public float getLevel() {
        return level;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit
     * damage.
     */
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.<Attribute, AttributeModifier>create();

        if (equipmentSlot == EquipmentSlot.MAINHAND) {
            multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.getDamage(), Operation.ADDITION));
            multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", getAttackSpeed(), Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public float getDamage() {
        return super.getDamage() * LevelHandler.getMultiplierFromLevel(level) * ModConfig.balance.weapon_damage;
    }

    protected double getAttackSpeed() {
        return -2.4000000953674316D;
    }

    public Item setInformation(Consumer<List<Component>> information) {
        this.information = information;
        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(!ModConfig.gui.disableMaelstromArmorItemTooltips) {
            tooltip.add(ModUtils.getDisplayLevel(level));
        }
        if (!element.equals(Element.NONE) && !ModConfig.gui.disableElementalVisuals) {
            tooltip.add(ModUtils.getElementalTooltip(element));
        }
        information.accept(tooltip);
    }

    @Override
    public void doSweepAttack(Player player, LivingEntity entity) {
        ModUtils.doSweepAttack(player, entity, element, (e) -> {
        });
    }

    public static UUID getAttackDamageModifier() {
        return BASE_ATTACK_DAMAGE_UUID;
    }

    @Override
    public Element getElement() {
        return element;
    }

    public ToolSword setElement(Element element) {
        this.element = element;
        return this;
    }
}
