package com.barribob.mm.entity.entities.npc;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.entity.entities.EntityTrader;
import com.barribob.mm.init.ModProfessions;
import com.barribob.mm.items.armor.ModArmorBase;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.TimedMessager;

public class NexusArmorer extends EntityTrader {
    private TimedMessager messager;
    private static final String[] ARMOR_EXPLANATION = {"armor_1", "armor_2", "armor_3", "armor_4", "armor_5", "armor_6", ""};
    private static final int[] MESSAGE_TIMES = {50, 150, 250, 350, 450, 550, 650};

    public NexusArmorer(Level worldIn) {
        super(worldIn);
        this.setImmovable(true);
        this.setSize(0.8f, 1.2f);
        this.setNoGravity(true);
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        super.useRecipe(recipe);
        if (messager == null && recipe.getItemToSell().getItem() instanceof ModArmorBase && ((ModArmorBase) recipe.getItemToSell().getItem()).getLevel() < 2) {
            messager = new TimedMessager(ARMOR_EXPLANATION, MESSAGE_TIMES, (s) -> {
                messager = null;
            });
        }
    }

    @Override
    public void tick() {
        super.onUpdate();
        if (!level.isClientSide && messager != null) {
            messager.Update(world, ModUtils.getPlayerAreaMessager(this));
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(9, new EntityAIWatchClosest2(this, Player.class, 3.0F, 1.0F));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected List<ITradeList> getTrades() {
        return ModProfessions.NEXUS_ARMORER.getTrades(0);
    }

    @Override
    protected String getVillagerName() {
        return "Armorer";
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
    }
}
