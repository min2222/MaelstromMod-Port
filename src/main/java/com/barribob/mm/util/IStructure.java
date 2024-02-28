package com.barribob.mm.util;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Keeps track of the world server and placement settings for structures
 */
public interface IStructure {
    public static final ServerLevel worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(0);
    public static final PlacementSettings settings = new PlacementSettings().setChunk(null).setIgnoreEntities(false).setIgnoreStructureBlock(false).setMirror(Mirror.NONE).setRotation(Rotation.NONE);

}
