package com.barribob.mm.invasion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.world.storage.MapStorage;

import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import com.barribob.mm.Main;
import com.barribob.mm.util.GenUtils;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.teleporter.NexusToOverworldTeleporter;
import com.barribob.mm.world.gen.WorldGenCustomStructures;

public class InvasionUtils {
    public static int TOWER_RADIUS = 50;
    public static int NUM_CIRCLE_POINTS = 16;
    public static int BED_DISTANCE = 75;
    public static int MAX_LAND_VARIATION = 8;

    public static Optional<BlockPos> trySpawnInvasionTower(BlockPos centralPos, Level world, Set<BlockPos> spawnedInvasionPositions) {
        BlockPos structureSize = WorldGenCustomStructures.invasionTower.getSize(world);
        BlockPos halfStructureSize = new BlockPos(structureSize.getX() * 0.5f, 0, structureSize.getZ() * 0.5f);
        BlockPos quarterStructureSize = new BlockPos(halfStructureSize.getX() * 0.5f, 0, halfStructureSize.getZ() * 0.5f);

        Function<Vec3, BlockPos> toTowerPos = pos -> {
            BlockPos pos2 = centralPos.offset(pos.x, 0, pos.y).subtract(halfStructureSize);
            int y = ModUtils.getAverageGroundHeight(world, pos2.getX() + quarterStructureSize.getX(),
                    pos2.getZ() + quarterStructureSize.getZ(), halfStructureSize.getX(), halfStructureSize.getZ(), MAX_LAND_VARIATION);
            return new BlockPos(pos2.getX(), y, pos2.getZ());
        };

        Predicate<BlockPos> notTooHigh = pos -> pos.getY() <= NexusToOverworldTeleporter.yPortalOffset - structureSize.getY();

        Predicate<BlockPos> inLiquid = pos -> !world.containsAnyLiquid(new AABB(pos, structureSize.offset(pos)));

        Predicate<BlockPos> noBaseNearby = pos -> world.players().stream().noneMatch((p) -> {
            if (p.getBedLocation() == null || world.getSpawnPoint().equals(p.getBedLocation())) {
                return false;
            }
            return pos.distanceSq(p.getBedLocation()) < Math.pow(BED_DISTANCE, 2);
        });

        Predicate<BlockPos> noPreviousInvasionNearby = pos -> spawnedInvasionPositions.stream()
                .noneMatch(p -> p.distanceSq(pos) < Math.pow(Main.invasionsConfig.getInt("invasion_radius"), 2));

        BinaryOperator<BlockPos> minVariation = (prevPos, newPos) -> {
            int prevVariation = GenUtils.getTerrainVariation(world, prevPos.getX(), prevPos.getZ(), prevPos.getX(), structureSize.getZ());
            int newVariation = GenUtils.getTerrainVariation(world, newPos.getX(), newPos.getZ(), newPos.getX(), structureSize.getZ());
            return prevVariation > newVariation ? newPos : prevPos;
        };

        Optional<BlockPos> towerPos = ModUtils.circlePoints(TOWER_RADIUS, NUM_CIRCLE_POINTS).stream()
                .map(toTowerPos)
                .filter(pos -> pos.getY() != -1)
                .filter(notTooHigh)
                .filter(inLiquid)
                .filter(noPreviousInvasionNearby)
                .filter(noBaseNearby)
                .reduce(minVariation);

        towerPos.ifPresent(blockPos -> WorldGenCustomStructures.invasionTower.generateStructure(world, blockPos, Rotation.NONE));

        return towerPos;
    }

    public static Player getPlayerClosestToOrigin(Level world) {
        return world.players().stream().reduce(world.players().get(0),
                (p1, p2) -> p1.getDistanceSq(BlockPos.ORIGIN) < p2.getDistanceSq(BlockPos.ORIGIN) ? p1 : p2);
    }

    public static void sendInvasionMessage(Level world, String translation) {
        world.players().forEach((p) -> p.sendMessage(
                new TextComponentString("" + ChatFormatting.DARK_PURPLE + new TextComponentTranslation(translation).getFormattedText())));
    }

    public static MultiInvasionWorldSavedData getInvasionData(Level world) {
        MapStorage storage = world.getMapStorage();
        MultiInvasionWorldSavedData instance = (MultiInvasionWorldSavedData) storage.getOrLoadData(MultiInvasionWorldSavedData.class, MultiInvasionWorldSavedData.DATA_NAME);

        if (instance == null) {
            instance = new MultiInvasionWorldSavedData();
            storage.setData(MultiInvasionWorldSavedData.DATA_NAME, instance);
        }
        return instance;
    }

    public static boolean hasMultipleInvasionsConfigured() {
        return Main.invasionsConfig.getConfigList("invasions").size() > 0;
    }
}