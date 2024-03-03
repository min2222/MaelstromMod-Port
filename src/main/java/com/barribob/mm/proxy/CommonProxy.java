package com.barribob.mm.proxy;

import com.barribob.mm.Main;
import com.barribob.mm.blocks.BlockLeavesBase;
import com.barribob.mm.mana.IMana;
import com.barribob.mm.mana.Mana;
import com.barribob.mm.mana.ManaStorage;
import com.barribob.mm.packets.MessageBBAnimation;
import com.barribob.mm.packets.MessageDirectionForRender;
import com.barribob.mm.packets.MessageEmptySwing;
import com.barribob.mm.packets.MessageExtendedReachAttack;
import com.barribob.mm.packets.MessageLeap;
import com.barribob.mm.packets.MessageLoopAnimationUpdate;
import com.barribob.mm.packets.MessageMana;
import com.barribob.mm.packets.MessageManaUnlock;
import com.barribob.mm.packets.MessageModParticles;
import com.barribob.mm.packets.MessagePlayDarkNexusWindSound;
import com.barribob.mm.packets.MessageStartElytraFlying;
import com.barribob.mm.packets.MessageSyncConfig;

import net.minecraftforge.common.capabilities.CapabilityManager;

public class CommonProxy {

    public void setFancyGraphics(BlockLeavesBase block, boolean isFancy) {
    }

	public static int packetId = 0;
    public void init() {

        Main.NETWORK.registerMessage(packetId++, MessageExtendedReachAttack.class, MessageExtendedReachAttack::toBytes, MessageExtendedReachAttack::new, MessageExtendedReachAttack.Handler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageMana.class, MessageMana::toBytes, MessageMana::new, MessageMana.MessageHandler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageLeap.class, MessageLeap::toBytes, MessageLeap::new, MessageLeap.MessageHandler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageManaUnlock.class, MessageManaUnlock::toBytes, MessageManaUnlock::new, MessageManaUnlock.MessageHandler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageDirectionForRender.class, MessageDirectionForRender::toBytes, MessageDirectionForRender::new, MessageDirectionForRender.Handler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageModParticles.class, MessageModParticles::toBytes, MessageModParticles::new, MessageModParticles.MessageHandler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageSyncConfig.class, MessageSyncConfig::toBytes, MessageSyncConfig::new, MessageSyncConfig.Handler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageBBAnimation.class, MessageBBAnimation::toBytes, MessageBBAnimation::new, MessageBBAnimation.Handler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageLoopAnimationUpdate.class, MessageLoopAnimationUpdate::toBytes, MessageLoopAnimationUpdate::new, MessageLoopAnimationUpdate.Handler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageStartElytraFlying.class, MessageStartElytraFlying::toBytes, MessageStartElytraFlying::new, MessageStartElytraFlying.Handler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessageEmptySwing.class, MessageEmptySwing::toBytes, MessageEmptySwing::new, MessageEmptySwing.Handler::onMessage);
        Main.NETWORK.registerMessage(packetId++, MessagePlayDarkNexusWindSound.class, MessagePlayDarkNexusWindSound::toBytes, MessagePlayDarkNexusWindSound::new, MessagePlayDarkNexusWindSound.Handler::onMessage);

        CapabilityManager.INSTANCE.register(IMana.class, new ManaStorage(), Mana.class);
        // CapabilityManager.INSTANCE.register(IInvasion.class, new InvasionStorage(),
        // Invasion.class);
    }
}
