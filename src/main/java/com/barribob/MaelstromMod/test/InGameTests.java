package com.barribob.MaelstromMod.test;

import com.barribob.MaelstromMod.entity.entities.*;
import com.barribob.MaelstromMod.util.Element;
import com.barribob.MaelstromMod.util.ModUtils;
import com.typesafe.config.Config;
import net.minecraft.command.ICommandSender;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.lang.reflect.InvocationTargetException;

public class InGameTests {
    public static void runAllTests(MinecraftServer server, ICommandSender sender) throws Exception {
        spawnAlgorithm(server.getEntityWorld(), sender.getPosition());
        defaultScout(server.getEntityWorld(), sender.getPosition());
        defaultIllager(server.getEntityWorld(), sender.getPosition());
        defaultChaosKnight(server.getEntityWorld(), sender.getPosition());
        defaultGoldenBoss(server.getEntityWorld(), sender.getPosition());
        defaultMaelstromStatueOfNirvana(server.getEntityWorld(), sender.getPosition());
    }

    public static void runSingleTest(MinecraftServer server, ICommandSender sender, String testName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        InGameTests.class.getMethod(testName, Level.class, BlockPos.class).invoke(null, server.getEntityWorld(), sender.getPosition());
    }

    public static void spawnAlgorithm(Level world, BlockPos pos) throws Exception {
        Config config = TestUtils.testingConfig().getConfig("spawning_algorithm");
        EntityLeveledMob entity = ModUtils.spawnMob(world, pos, 0, config);
        assert entity != null;
        CompoundTag compound = new CompoundTag();
        entity.writeToNBT(compound);

        TestUtils.AssertTrue(entity.getDisplayName().getFormattedText().contains("Maelstrom Scout"), "Mob display names do not match");
        TestUtils.AssertEquals(Element.AZURE, entity.getElement());
        TestUtils.AssertEquals(1000, compound.getInteger("experienceValue"));
        TestUtils.AssertEquals(1f, entity.getHealth());
        TestUtils.AssertEquals(1f, entity.getMaxHealth());
        TestUtils.AssertEquals(12.0, entity.getEntityAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
        TestUtils.AssertEquals(64.0, entity.getEntityAttribute(Attributes.FOLLOW_RANGE).getBaseValue());
        TestUtils.AssertEquals(0.4, entity.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue());
        TestUtils.AssertEquals(0.27, entity.getEntityAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());
    }

    public static void defaultScout(Level world, BlockPos pos) throws Exception {
        EntityLeveledMob scout = new EntityShade(world);
        world.spawnEntity(scout);
        ModUtils.setEntityPosition(scout, new Vec3(pos));
        CompoundTag compound = new CompoundTag();
        scout.writeToNBT(compound);

        TestUtils.AssertEquals(10, compound.getInteger("experienceValue"));
        TestUtils.AssertEquals(25f, scout.getHealth());
        TestUtils.AssertEquals(25f, scout.getMaxHealth());
        TestUtils.AssertAlmostEquals(6.0, scout.getEntityAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(), 3);
        TestUtils.AssertAlmostEquals(0.26, scout.getEntityAttribute(Attributes.MOVEMENT_SPEED).getBaseValue(), 3);
        TestUtils.AssertAlmostEquals(0.3, scout.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue(), 3);
    }

    public static void defaultIllager(Level world, BlockPos pos) throws Exception {
        EntityLeveledMob entity = new EntityMaelstromIllager(world);
        CompoundTag compound = new CompoundTag();
        entity.writeToNBT(compound);

        TestUtils.AssertEquals(1000, compound.getInteger("experienceValue"));
        TestUtils.AssertEquals(300f, entity.getHealth());
        TestUtils.AssertEquals(300f, entity.getMaxHealth());
        TestUtils.AssertAlmostEquals(8.0, entity.getEntityAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(), 3);
    }

    public static void defaultGoldenBoss(Level world, BlockPos pos) throws Exception {
        EntityLeveledMob entity = new EntityGoldenBoss(world);
        CompoundTag compound = new CompoundTag();
        entity.writeToNBT(compound);

        TestUtils.AssertEquals(0, compound.getInteger("experienceValue"));
        TestUtils.AssertEquals(450f, entity.getHealth());
        TestUtils.AssertEquals(450f, entity.getMaxHealth());
        TestUtils.AssertAlmostEquals(9.0, entity.getEntityAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(), 3);
        TestUtils.AssertAlmostEquals(40.0, entity.getEntityAttribute(Attributes.FOLLOW_RANGE).getBaseValue(), 3);
    }

    public static void defaultChaosKnight(Level world, BlockPos pos) throws Exception {
        EntityLeveledMob entity = new EntityChaosKnight(world);
        CompoundTag compound = new CompoundTag();
        entity.writeToNBT(compound);

        TestUtils.AssertEquals(1000, compound.getInteger("experienceValue"));
        TestUtils.AssertEquals(450f, entity.getHealth());
        TestUtils.AssertEquals(450f, entity.getMaxHealth());
        TestUtils.AssertAlmostEquals(9.0, entity.getEntityAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(), 3);
        TestUtils.AssertAlmostEquals(30.0, entity.getEntityAttribute(Attributes.FOLLOW_RANGE).getBaseValue(), 3);
        TestUtils.AssertAlmostEquals(1.0, entity.getEntityAttribute(Attributes.KNOCKBACK_RESISTANCE).getBaseValue(), 3);
    }

    public static void defaultMaelstromStatueOfNirvana(Level world, BlockPos pos) throws Exception {
        EntityLeveledMob entity = new EntityMaelstromStatueOfNirvana(world);
        CompoundTag compound = new CompoundTag();
        entity.writeToNBT(compound);

        TestUtils.AssertEquals(1000, compound.getInteger("experienceValue"));
        TestUtils.AssertEquals(150f, entity.getHealth());
        TestUtils.AssertEquals(150f, entity.getMaxHealth());
        TestUtils.AssertAlmostEquals(10.0, entity.getEntityAttribute(Attributes.ATTACK_DAMAGE).getBaseValue(), 3);
        TestUtils.AssertAlmostEquals(40.0, entity.getEntityAttribute(Attributes.FOLLOW_RANGE).getBaseValue(), 3);
    }
}
