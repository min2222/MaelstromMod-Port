package com.barribob.mm.invasion;

import com.barribob.mm.Main;
import com.barribob.mm.util.ModUtils;
import com.barribob.mm.util.Reference;
import com.typesafe.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MultiInvasionWorldSavedData extends SavedData {
    public static final String DATA_NAME = Reference.MOD_ID + "_MultiInvasionData";
    private int ticks;
    private int invasionIndex;
    public static final int INVASION_RESET_TIME = ModUtils.secondsToTicks(10);
    private final Set<BlockPos> spawnedInvasionPositions = new HashSet<>();

    @SuppressWarnings("unused")
    public MultiInvasionWorldSavedData(String s) {
        super(s);
    }

    public MultiInvasionWorldSavedData() {
        super(DATA_NAME);
    }

    public void addSpawnedInvasionPosition(BlockPos pos) {
        if (spawnedInvasionPositions.add(pos)) {
            this.markDirty();
        }
    }

    public Set<BlockPos> getSpawnedInvasionPositions() {
        return spawnedInvasionPositions;
    }

    public Config getCurrentInvasion() {
        List<? extends Config> invasions = Main.invasionsConfig.getConfigList("invasions");

        if (invasions.size() > invasionIndex) {
            return invasions.get(invasionIndex);
        }

        return null;
    }

    public void tick(Level world) {
        Config invasion = getCurrentInvasion();

        if (invasion == null) {
            return;
        }

        this.markDirty();

        int invasionTime = ModUtils.minutesToTicks(invasion.getInt("invasion_time"));
        int warningTime = ModUtils.minutesToTicks(invasion.getInt("warning_time"));

        if (ticks == warningTime) {
            InvasionUtils.sendInvasionMessage(world, Reference.MOD_ID + ".invasion_1");
        }

        if (ticks == invasionTime) {
            if (world.playerEntities.size() > 0) {
                Player player = InvasionUtils.getPlayerClosestToOrigin(world);

                Optional<BlockPos> spawnedPos = InvasionUtils.trySpawnInvasionTower(player.getPosition(), player.world, spawnedInvasionPositions);

                if (spawnedPos.isPresent()) {
                    spawnedInvasionPositions.add(spawnedPos.get());
                    InvasionUtils.sendInvasionMessage(world, Reference.MOD_ID + ".invasion_2");
                    invasionIndex++;
                    ticks = 0;
                    return;
                }
            }

            ticks = Math.max(0, ticks - INVASION_RESET_TIME);
        }

        ticks++;
    }

    @Override
    public void readFromNBT(CompoundTag nbt) {
        this.ticks = nbt.getInteger("ticks");
        this.invasionIndex = nbt.getInteger("integerIndex");

        spawnedInvasionPositions.clear();
        NBTTagList nbtList = nbt.getTagList("spawnedInvasionPositions", new CompoundTag().getId());
        for (NBTBase nbtBase : nbtList) {
            CompoundTag posNbt = (CompoundTag) nbtBase;
            int[] pos = posNbt.getIntArray("pos");
            spawnedInvasionPositions.add(new BlockPos(pos[0], pos[1], pos[2]));
        }
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag compound) {
        compound.setInteger("ticks", ticks);
        compound.setInteger("integerIndex", invasionIndex);

        NBTTagList nbtList = new NBTTagList();
        for (BlockPos pos : spawnedInvasionPositions) {
            CompoundTag posCompound = new CompoundTag();
            posCompound.setIntArray("pos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
            nbtList.appendTag(posCompound);
        }

        compound.setTag("spawnedInvasionPositions", nbtList);

        return compound;
    }
}