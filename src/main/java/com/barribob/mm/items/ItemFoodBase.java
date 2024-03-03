package com.barribob.mm.items;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemFoodBase extends Item {
	int amount;
	float saturation;
	boolean isWolfFood;
    public ItemFoodBase(String name, CreativeModeTab tab, int amount, float saturation, boolean isWolfFood) {
        super(new Item.Properties().tab(tab));
        this.amount = amount;
        this.saturation = saturation;
        this.isWolfFood = isWolfFood;
    }
    
    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
    	FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(amount).saturationMod(saturation);
    	return this.isWolfFood ? builder.meat().build() : builder.build();
    }
}
