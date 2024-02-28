package com.barribob.mm.commands;

import com.barribob.mm.util.Reference;
import com.barribob.mm.util.teleporter.Teleport;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class CommandDimensionTeleport extends CommandBase {
    private final List<String> aliases = Lists.newArrayList(Reference.MOD_ID, "tpdim", "teleport", "dimension");

    @Override
    public String getName() {
        return "tpdimension";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "tpdimension <dimension id>";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1)
            return;

        String s = args[0];

        int dimensionId;

        try {
            dimensionId = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponentString(ChatFormatting.RED + "Dimension Id Invalid, make sure its a number e.g. '0' for the overworld."));
            return;
        }

        try {
            if (sender instanceof ServerPlayer && ((Player) sender).dimension != dimensionId) {
                ServerLevel worldServer = server.getWorld(dimensionId);

                if (worldServer == null || server == null) {
                    sender.sendMessage(new TextComponentString(ChatFormatting.RED + "Dimension: " + dimensionId + " doesn't exist"));
                    return;
                }

                Teleport.teleportToDimension((ServerPlayer) sender, dimensionId, new Teleport(worldServer, sender.getPosition().getX(), sender.getPosition().getY(), sender.getPosition().getZ()));
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(new TextComponentString(ChatFormatting.RED + e.getMessage()));
            return;
        }

    }
}
