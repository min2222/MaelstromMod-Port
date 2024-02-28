package com.barribob.mm.entity.entities.herobrine_state;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.barribob.mm.entity.entities.Herobrine;
import com.barribob.mm.init.ModProfessions;

import net.minecraft.core.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.level.Level;

public class StateCrimsonKey extends HerobrineState implements IMerchant {
    protected final MerchantRecipeList buyingList = new MerchantRecipeList();
    protected Player buyingPlayer;
    protected boolean gtfo = false;
    private boolean leftClickMessage = false;

    public StateCrimsonKey(Herobrine herobrine) {
        super(herobrine);
        for (EntityVillager.ITradeList list : ModProfessions.HEROBRINE_CRIMSON_KEY.getTrades(0)) {
            list.addMerchantRecipe(this, this.buyingList, this.herobrine.getRNG());
        }
    }

    @Override
    public String getNbtString() {
        return "crimson_key";
    }

    @Override
    public void rightClick(Player player) {
        if (!this.gtfo) {
            this.messageToPlayers.accept("herobrine_crimson_0");
            this.gtfo = true;
        }
        if (herobrine.isEntityAlive() && this.buyingPlayer == null) {
            this.setCustomer(player);
            player.displayVillagerTradeGui(this);
        }
    }

    @Override
    public void leftClick(Herobrine herobrine) {
        if (!this.leftClickMessage) {
            messageToPlayers.accept("herobrine_crimson_1");
            leftClickMessage = true;
        }
        super.leftClick(herobrine);
    }

    @Override
    public void setCustomer(Player player) {
        this.buyingPlayer = player;
    }

    @Override
    public Player getCustomer() {
        return this.buyingPlayer;
    }

    @Override
    public MerchantRecipeList getRecipes(Player player) {
        return this.buyingList;
    }

    @Override
    public void setRecipes(MerchantRecipeList recipeList) {
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        messageToPlayers.accept("herobrine_crimson_dimension_0");
        herobrine.state = new StateCrimsonDimension(herobrine);
    }

    @Override
    public void verifySellingItem(ItemStack stack) {
    }

    @Override
    public ITextComponent getDisplayName() {
        ITextComponent itextcomponent = new TextComponentTranslation("herobrine_trading", new Object[0]);
        itextcomponent.getStyle().setInsertion(herobrine.getCachedUniqueIdString());
        return itextcomponent;
    }

    @Override
    public Level getWorld() {
        return this.world;
    }

    @Override
    public BlockPos getPos() {
        return new BlockPos(herobrine);
    }
}
