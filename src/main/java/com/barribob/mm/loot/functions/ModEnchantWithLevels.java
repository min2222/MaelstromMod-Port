package com.barribob.mm.loot.functions;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.util.ModRandom;
import com.barribob.mm.util.handlers.LevelHandler;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.storage.loot.RandomValueRange;

public class ModEnchantWithLevels extends LootItemConditionalFunction {
    private final RandomValueRange randomLevel;

    protected ModEnchantWithLevels(LootItemCondition[] conditionsIn, RandomValueRange randomRange) {
        super(conditionsIn);
        this.randomLevel = randomRange;
    }

    @Override
    public ItemStack run(ItemStack stack, LootContext context) {
    	RandomSource rand = context.getRandom();
        if (stack.getItem() instanceof SwordItem && rand.nextFloat() < 0.2) {
            float sharpnessDamage = 0.5f;
            int level = this.randomLevel.generateInt(rand);
            float swordDamage = ModItems.BASE_MELEE_DAMAGE * ModConfig.balance.weapon_damage * LevelHandler.getMultiplierFromLevel(level); // Calculate the standard sword damage
            int maxSharpness = (int) Math.round((swordDamage * (Math.pow(ModConfig.balance.progression_scale, 2) - 1)) / sharpnessDamage); // Approximate the max sharpness to be about two levels

            stack.enchant(Enchantments.SHARPNESS, Math.max(5, rand.nextInt(maxSharpness) + 1));
            return stack;
        }

        return EnchantmentHelper.addRandomEnchantment(rand, stack, ModRandom.range(1, 31), true);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<ModEnchantWithLevels> {

        @Override
        public void serialize(JsonObject object, ModEnchantWithLevels functionClazz, JsonSerializationContext serializationContext) {
            object.add("level", serializationContext.serialize(functionClazz.randomLevel));
        }

        @Override
        public ModEnchantWithLevels deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootItemCondition[] conditionsIn) {
            RandomValueRange randomvaluerange = GsonHelper.deserializeClass(object, "level", deserializationContext, RandomValueRange.class);
            return new ModEnchantWithLevels(conditionsIn, randomvaluerange);
        }
    }
}
