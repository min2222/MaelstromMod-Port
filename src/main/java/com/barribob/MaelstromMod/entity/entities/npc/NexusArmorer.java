package com.barribob.MaelstromMod.entity.entities.npc;

import com.barribob.MaelstromMod.entity.entities.EntityTrader;
import com.barribob.MaelstromMod.init.ModProfessions;
import com.barribob.MaelstromMod.items.armor.ModArmorBase;
import com.barribob.MaelstromMod.util.ModUtils;
import com.barribob.MaelstromMod.util.TimedMessager;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.level.Level;

import java.util.List;

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
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote && messager != null) {
            messager.Update(world, ModUtils.getPlayerAreaMessager(this));
        }
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, Player.class, 3.0F, 1.0F));
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
