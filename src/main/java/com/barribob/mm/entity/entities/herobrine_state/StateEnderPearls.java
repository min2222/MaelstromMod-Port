package com.barribob.mm.entity.entities.herobrine_state;

import com.barribob.mm.entity.entities.Herobrine;
import com.barribob.mm.init.ModProfessions;
import com.barribob.mm.util.TimedMessager;

import net.minecraft.core.BlockPos;
import net.minecraft.entity.IMerchant;
import net.minecraft.network.chat.Component;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class StateEnderPearls extends HerobrineState implements IMerchant {
    protected final MerchantRecipeList buyingList = new MerchantRecipeList();
    protected Player buyingPlayer;
    private boolean leftClickMessage = false;
    private TimedMessager messager;

    public StateEnderPearls(Herobrine herobrine) {
        super(herobrine);
        for (Villager.ITradeList list : ModProfessions.HEROBRINE_ENDER_PEARLS.getTrades(0)) {
            list.addMerchantRecipe(this, this.buyingList, this.herobrine.getRandom());
        }
        messager = new TimedMessager(new String[]{"herobrine_pearl_0", "herobrine_pearl_1", ""}, new int[]{40, 100, 100}, (s) -> {
        });
    }

    @Override
    public void update() {
        messager.Update(world, messageToPlayers);
    }

    @Override
    public String getNbtString() {
        return "ender_pearl";
    }

    @Override
    public void rightClick(Player player) {
        if (herobrine.isAlive() && this.buyingPlayer == null) {
            this.setCustomer(player);
            player.displayVillagerTradeGui(this);
        }
    }

    @Override
    public void leftClick(Herobrine herobrine) {
        if (!this.leftClickMessage) {
            messageToPlayers.accept("herobrine_pearl_2");
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
        herobrine.state = new StateFirstBattle(herobrine);
    }

    @Override
    public void verifySellingItem(ItemStack stack) {
    }

    @Override
    public Component getDisplayName() {
    	Component itextcomponent = Component.translatable("herobrine_trading", new Object[0]);
        itextcomponent.getStyle().setInsertion(herobrine.getCachedUniqueIdString());
        return itextcomponent;
    }

    @Override
    public Level getLevel() {
        return this.world;
    }

    @Override
    public BlockPos blockPosition() {
        return herobrine.blockPosition();
    }
}
