package com.barribob.mm.entity.entities.herobrine_state;

import java.util.function.Consumer;

import com.barribob.mm.entity.entities.Herobrine;
import com.barribob.mm.util.ModUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Allows easy swapping of states for the herobrine npc
 */
public abstract class HerobrineState {
    Level world;
    Herobrine herobrine;

    protected Consumer<String> messageToPlayers = (message) -> {
        if (message != "") {
            for (Player player : herobrine.bossInfo.getPlayers()) {
                player.sendSystemMessage(Component.literal(ChatFormatting.DARK_PURPLE + herobrine.getDisplayName().getString() + ": " + ChatFormatting.WHITE)
                        .append(Component.translatable(ModUtils.LANG_CHAT + message)));
            }
        }
    };

    public HerobrineState(Herobrine herobrine) {
        this.world = herobrine.level;
        this.herobrine = herobrine;
    }

    public abstract String getNbtString();

    public void update() {
    }

    public void rightClick(Player player) {
    }

    public void leftClick(Herobrine herobrine) {
    }
}
