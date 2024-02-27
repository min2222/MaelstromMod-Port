package com.barribob.MaelstromMod.sounds;

import com.barribob.MaelstromMod.init.ModDimensions;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DarkNexusWindSound extends MovingSound {
    private final LocalPlayer player;
    private int time;

    public DarkNexusWindSound(LocalPlayer player) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundSource.PLAYERS);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1F;
    }

    @Override
    public void update() {
        ++this.time;

        if (!this.player.isDead && (this.time <= 20 || this.player.dimension == ModDimensions.DARK_NEXUS.getId())) {
            this.xPosF = (float) this.player.posX;
            this.yPosF = (float) this.player.posY;
            this.zPosF = (float) this.player.posZ;
            float velocity = Mth.sqrt(this.player.motionX * this.player.motionX + this.player.motionZ * this.player.motionZ + this.player.motionY * this.player.motionY);
            float f1 = velocity / 2.0F;

            this.volume = 0.1f + Mth.clamp(f1 * f1, 0.0F, 1.0F);

            if (this.time < 20) {
                this.volume = 0.0F;
            } else if (this.time < 40) {
                this.volume = (float) (this.volume * ((this.time - 20) / 20.0D));
            }

            if (this.volume > 0.8F) {
                this.pitch = 1.0F + (this.volume - 0.8F);
            } else {
                this.pitch = 1.0F;
            }
        } else {
            this.donePlaying = true;
        }
    }
}