package com.barribob.MaelstromMod.proxy;

import com.barribob.MaelstromMod.Main;
import com.barribob.MaelstromMod.blocks.BlockLeavesBase;
import com.barribob.MaelstromMod.mana.IMana;
import com.barribob.MaelstromMod.mana.Mana;
import com.barribob.MaelstromMod.mana.ManaStorage;
import com.barribob.MaelstromMod.packets.MessageExtendedReachAttack;
import com.barribob.MaelstromMod.packets.MessageLeap;
import com.barribob.MaelstromMod.packets.MessageMana;
import com.barribob.MaelstromMod.util.Reference;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy
{
    public void registerItemRenderer(Item item, int meta, String id)
    {
    }

    public void setFancyGraphics(BlockLeavesBase block, boolean isFancy)
    {
    }

    public void setCustomState(Block block, IStateMapper mapper)
    {
    }

    public void init()
    {
	// Register a network to communicate to the server for client stuff (e.g. client
	// raycast rendering for extended melee reach)
	Main.network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.NETWORK_CHANNEL_NAME);

	int packetId = 0;

	Main.network.registerMessage(MessageExtendedReachAttack.Handler.class, MessageExtendedReachAttack.class, packetId++, Side.SERVER);
	Main.network.registerMessage(MessageMana.MessageHandler.class, MessageMana.class, packetId++, Side.CLIENT);
	Main.network.registerMessage(MessageLeap.MessageHandler.class, MessageLeap.class, packetId++, Side.CLIENT);

	CapabilityManager.INSTANCE.register(IMana.class, new ManaStorage(), Mana.class);
    }
}
