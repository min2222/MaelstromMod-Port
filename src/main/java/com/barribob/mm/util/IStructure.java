package com.barribob.mm.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * Keeps track of the world server and placement settings for structures
 */
public interface IStructure {
    public static final ServerLevel worldServer = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
    public static final StructurePlaceSettings settings = new StructurePlaceSettings().setIgnoreEntities(false).setMirror(Mirror.NONE).setRotation(Rotation.NONE);
}
