package xyz.elevated.frequency.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInEntityAction;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "BadPackets (G)")
public final class BadPacketsG extends PacketCheck {

    private int count;
    private PacketPlayInEntityAction.EnumPlayerAction lastAction;

    public BadPacketsG(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {
        if (object instanceof WrappedPlayInEntityAction) {
            WrappedPlayInEntityAction wrapper = (WrappedPlayInEntityAction) object;

            boolean invalid = ++count > 1 && wrapper.getAction() == lastAction;

            if (invalid) fail();

            lastAction = wrapper.getAction();
        } else if (object instanceof WrappedPlayInFlying) {
            count = 0;
        }
    }
}
