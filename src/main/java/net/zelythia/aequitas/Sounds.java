package net.zelythia.aequitas;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class Sounds {
    public static final SoundEvent COLLECTION_BOWL_LOOP = Registry.register(Registry.SOUND_EVENT, "block.collection_bowl.loop", new SoundEvent(new Identifier(Aequitas.MOD_ID, "block.collection_bowl.loop")));


    @Environment(EnvType.CLIENT)
    public static class CollectionBowlSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
        private boolean done;
        private final PlayerEntity player;
        private final double maxDistance;

        public CollectionBowlSoundInstance(PlayerEntity player, BlockPos pos, double maxDistance) {
            super(COLLECTION_BOWL_LOOP.getId(), SoundCategory.BLOCKS);
            this.player = player;
            this.maxDistance = maxDistance;

            this.volume = 1f;
            this.pitch = 1f;
            this.x = pos.getX() + 0.5;
            this.y = pos.getY() + 0.5;
            this.z = pos.getZ() + 0.5;
            this.repeat = true;
            this.repeatDelay = 0;
            this.attenuationType = AttenuationType.LINEAR;
            this.looping = true;
        }

        public boolean isDone() {
            return this.done;
        }

        public final void setDone() {
            this.done = true;
            this.repeat = false;
        }

        @Override
        public void tick() {
            double distance = player.getPos().distanceTo(new Vec3d(x, y, z));
            if (distance > maxDistance) {
                this.volume = (float) (1 - Math.min((distance - maxDistance) / 8, 1)) * 0.2f;
                return;
            }
            this.volume = 0.2f;
        }
    }
}
