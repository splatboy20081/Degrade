package xyz.elevated.frequency.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig.EnumPlayerDigType;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInBlockDig;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInBlockPlace;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "BadPackets (H)")
public final class BadPacketsH extends PacketCheck {
    private int count = 0;

    public BadPacketsH(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {
        boolean digging = object instanceof WrappedPlayInBlockDig;
        boolean flying = object instanceof WrappedPlayInFlying;

        if (digging) {
            WrappedPlayInBlockDig wrapper = (WrappedPlayInBlockDig) object;

            handle: {
                if (wrapper.getDigType() != EnumPlayerDigType.RELEASE_USE_ITEM) break handle;

                boolean invalid = ++count > 1;

                if (invalid) fail();
            }
        } else if (flying) {
            count = 0;
        }
    }
}