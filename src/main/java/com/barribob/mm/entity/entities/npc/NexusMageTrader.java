package com.barribob.mm.entity.entities.npc;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.level.Level;

import java.util.List;

import com.barribob.mm.entity.entities.EntityTrader;
import com.barribob.mm.init.ModItems;
import com.barribob.mm.init.ModProfessions;
import com.barribob.mm.mana.ManaProvider;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.TimedMessager;
import com.barribob.mm.util.handlers.ParticleManager;

public class NexusMageTrader extends EntityTrader {
    private byte magic = 4;
    private TimedMessager messager;
    private static final String[] MAGIC_EXPLANATION = {"mage_1", "mage_2", "mage_3", "mage_4", "mage_5", ""};
    private static final int[] MESSAGE_TIMES = {50, 150, 250, 350, 450, 550};

    public NexusMageTrader(Level worldIn) {
        super(worldIn);
        this.setImmovable(true);
        this.setNoGravity(true);
    }

    @Override
    public void setCustomer(Player player) {
        super.setCustomer(player);

        // Players with mana will be scolded
        if (player != null && !level.isClientSide && player.getCapability(ManaProvider.MANA, null).isLocked()) {
            player.sendMessage(new TextComponentString(ChatFormatting.DARK_PURPLE + this.getDisplayName().getFormattedText() + ": " + ChatFormatting.WHITE)
                    .appendSibling(new TextComponentTranslation(ModUtils.LANG_CHAT + "no_mana")));
        }
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        super.useRecipe(recipe);
        if (messager == null && recipe.getItemToSell().getItem().equals(ModItems.CATALYST)) {
            messager = new TimedMessager(MAGIC_EXPLANATION, MESSAGE_TIMES, (s) -> {
                messager = null;
            });
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(9, new EntityAIWatchClosest2(this, Player.class, 5.0F, 1.0F));
    }

    @Override
    public void tick() {
        super.onUpdate();
        if (!level.isClientSide && messager != null) {
            messager.Update(world, ModUtils.getPlayerAreaMessager(this));
        }
        level.broadcastEntityEvent(this, magic);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == magic) {
            // Magic particles centered in the mage's staff
            Vec3 look = this.getVectorForRotation(0, this.renderYawOffset);
            Vec3 pos = ModUtils.entityPos(this).add(new Vec3(0, this.getEyeHeight(), 0));
            Vec3 side = look.scale(0.55).rotateYaw((float) Math.PI * -0.5f);
            Vec3 offset = pos.add(look.scale(0.5f)).add(side).add(new Vec3(0, 0.75, 0));

            ParticleManager.spawnEffect(world, offset, new Vec3(0.3, 0.9, 0.3));
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    protected List<ITradeList> getTrades() {
        return ModProfessions.NEXUS_MAGE.getTrades(0);
    }

    @Override
    protected String getVillagerName() {
        return "Magic Merchant";
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
    }
}
