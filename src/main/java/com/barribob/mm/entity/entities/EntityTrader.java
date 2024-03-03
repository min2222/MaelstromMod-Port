package com.barribob.mm.entity.entities;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;

/**
 * For Mod entities whose main job is to be a trader of some sort
 * Sourced from the EntityVillager
 */
public abstract class EntityTrader extends EntityLeveledMob implements IMerchant {
    protected MerchantRecipeList buyingList;
    @Nullable
    protected Player buyingPlayer;

    public EntityTrader(Level worldIn) {
        super(worldIn);
        this.setSize(0.8f, 1.8f);
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new GroundPathNavigation(this, worldIn);
    }

    /**
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
    protected boolean canDespawn() {
        return false;
    }

    /**
     * Generate the buying list for the villager (the trades and items)
     */
    protected void populateBuyingList() {
        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }

        List<EntityVillager.ITradeList> trades = this.getTrades();

        if (trades != null) {
            for (EntityVillager.ITradeList list : trades) {
                list.addMerchantRecipe(this, this.buyingList, this.rand);
            }
        }
    }

    /**
     * Return the initial trades for the villager
     */
    protected abstract List<EntityVillager.ITradeList> getTrades();

    /**
     * When the entity is right clicked
     */
    @Override
    public boolean processInteract(Player player, InteractionHand hand) {
        if (this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking() && this.getTarget() == null) {
            if (this.buyingList == null) {
                this.populateBuyingList();
            }

            if (hand == InteractionHand.MAIN_HAND) {
                player.addStat(StatList.TALKED_TO_VILLAGER);
            }

            this.onTraderInteract(player);

            if (!this.level.isClientSide && !this.buyingList.isEmpty()) {
                this.setCustomer(player);
                player.displayVillagerTradeGui(this);
            } else if (this.buyingList.isEmpty()) {
                return super.processInteract(player, hand);
            }

            return true;
        } else {
            return super.processInteract(player, hand);
        }
    }

    protected void onTraderInteract(Player player) {
    }

    public boolean isTrading() {
        return this.buyingPlayer != null;
    }

    /**
     * The following few methods are used in the IMerchant class to handle the
     * trading interface
     */
    @Override
    @Nullable
    public Player getCustomer() {
        return this.buyingPlayer;
    }

    @Override
    public void setCustomer(@Nullable Player player) {
        this.buyingPlayer = player;
    }

    @Override
    @Nullable
    public MerchantRecipeList getRecipes(Player player) {
        if (this.buyingList == null) {
            this.populateBuyingList();
        }

        return this.buyingList;
    }

    @Override
    public void setRecipes(MerchantRecipeList recipeList) {
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        recipe.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound(SoundEvents.VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());

        if (recipe.getRewardsExp()) {
            this.level.addFreshEntity(new ExperienceOrb(this.world, this.posX, this.posY + 0.5D, this.posZ, 5));
        }
    }

    /**
     * Notifies the merchant of a possible merchantrecipe being fulfilled or not.
     * Usually, this is just a sound byte being played depending if the suggested
     * itemstack is not null.
     */
    @Override
    public void verifySellingItem(ItemStack stack) {
        if (!this.level.isClientSide && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval();
            this.playSound(stack.isEmpty() ? SoundEvents.VILLAGER_NO : SoundEvents.VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public Level getWorld() {
        return this.world;
    }

    @Override
    public BlockPos getPos() {
        return new BlockPos(this);
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    @Override
    public ITextComponent getDisplayName() {
        Team team = this.getTeam();
        String s = this.getCustomNameTag();

        if (s != null && !s.isEmpty()) {
            TextComponentString textcomponentstring = new TextComponentString(ScorePlayerTeam.formatPlayerName(team, s));
            textcomponentstring.getStyle().setHoverEvent(this.getHoverEvent());
            textcomponentstring.getStyle().setInsertion(this.getCachedUniqueIdString());
            return textcomponentstring;
        } else {
            if (this.buyingList == null) {
                this.populateBuyingList();
            }

            ITextComponent itextcomponent = new TextComponentTranslation(this.getVillagerName(), new Object[0]);
            itextcomponent.getStyle().setHoverEvent(this.getHoverEvent());
            itextcomponent.getStyle().setInsertion(this.getCachedUniqueIdString());

            if (team != null) {
                itextcomponent.getStyle().setColor(team.getColor());
            }

            return itextcomponent;

        }
    }

    /**
     * The name displayed when the trading gui is up
     */
    protected abstract String getVillagerName();
}
