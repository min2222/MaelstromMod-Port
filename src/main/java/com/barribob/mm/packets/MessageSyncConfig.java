package com.barribob.mm.packets;

import java.util.function.Supplier;

import com.barribob.mm.config.ModConfig;
import com.barribob.mm.util.Reference;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.network.NetworkEvent;

public class MessageSyncConfig {
    float progression_scale;
    float weapon_damage;
    float armor_toughness;
    float elemental_factor;

    public MessageSyncConfig(FriendlyByteBuf buf) {
    	this.fromBytes(buf);
    }

    public MessageSyncConfig(float progression_scale, float weapon_damage, float armor_toughness, float elemental_factor) {
        this.progression_scale = progression_scale;
        this.weapon_damage = weapon_damage;
        this.armor_toughness = armor_toughness;
        this.elemental_factor = elemental_factor;
    }

    public void fromBytes(FriendlyByteBuf buf) {
        this.progression_scale = buf.readFloat();
        this.weapon_damage = buf.readFloat();
        this.armor_toughness = buf.readFloat();
        this.elemental_factor = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(this.progression_scale);
        buf.writeFloat(this.weapon_damage);
        buf.writeFloat(this.armor_toughness);
        buf.writeFloat(this.elemental_factor);
    }

    public static class Handler {
        public static boolean onMessage(MessageSyncConfig message, Supplier<NetworkEvent.Context> ctx) {
            ModConfig.balance.progression_scale = message.progression_scale;
            ModConfig.balance.weapon_damage = message.weapon_damage;
            ModConfig.balance.armor_toughness = message.armor_toughness;
            ModConfig.balance.elemental_factor = message.elemental_factor;
            ConfigManager.sync(Reference.MOD_ID, Type.INSTANCE);
            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}
