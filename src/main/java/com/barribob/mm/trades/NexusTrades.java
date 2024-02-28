package com.barribob.mm.trades;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

public class NexusTrades {
    public static class MoreComing implements ITradeList {
        private ItemStack base = new ItemStack(Item.BY_BLOCK.get(Blocks.BEDROCK), 1).setHoverName(Component.literal("More Items Coming Soon!"));

        @Override
        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
            recipeList.add(new MerchantRecipe(base, base));
        }
    }
}
