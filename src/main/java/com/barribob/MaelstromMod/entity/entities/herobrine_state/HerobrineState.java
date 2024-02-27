package com.barribob.MaelstromMod.entity.entities.herobrine_state;

import com.barribob.MaelstromMod.entity.entities.Herobrine;
import com.barribob.MaelstromMod.util.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * Allows easy swapping of states for the herobrine npc
 */
public abstract class HerobrineState {
    Level world;
    Herobrine herobrine;

    protected Consumer<String> messageToPlayers = (message) -> {
        if (message != "") {
            for (Player player : herobrine.bossInfo.getPlayers()) {
                player.sendMessage(new TextComponentString(ChatFormatting.DARK_PURPLE + herobrine.getDisplayName().getFormattedText() + ": " + ChatFormatting.WHITE)
                        .appendSibling(new TextComponentTranslation(ModUtils.LANG_CHAT + message)));
            }
        }
    };

    public HerobrineState(Herobrine herobrine) {
        this.world = herobrine.world;
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
