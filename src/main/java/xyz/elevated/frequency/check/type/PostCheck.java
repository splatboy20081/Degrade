package xyz.elevated.frequency.check.type;

import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.PacketWrapper;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

public class PostCheck extends PacketCheck {
    private final Class<? extends PacketWrapper> packet;
    private boolean sent = false;

    public long lastFlying, lastPacket;
    public double buffer = 0.0;

    public PostCheck(PlayerData playerData, Class<? extends PacketWrapper> packet) {
        super(playerData);

        this.packet = packet;
    }

    @Override
    public void process(Object object) {
        if(isPost(object)) fail();
    }

    // Flag only when its both a post and a flag
    public boolean isPost(Object object) {
        if (object.getClass() == WrappedPlayInFlying.class) {
            long now = System.currentTimeMillis();
            long delay = now - lastPacket;

            if (sent) {
                if (delay > 40L && delay < 100L) {
                    buffer += 0.25;

                    if (buffer > 0.5) {
                        return true;
                    }
                } else {
                    buffer = Math.max(buffer - 0.025, 0);
                }

                sent = false;
            }

            lastFlying = now;
        } else if (object.getClass() == packet) {
            long now = System.currentTimeMillis();
            long delay = now - lastFlying;

            if (delay < 10L) {
                lastPacket = now;
                sent = true;
            } else {
                buffer = Math.max(buffer - 0.025, 0.0);
            }
        }

        return false;
    }
}
