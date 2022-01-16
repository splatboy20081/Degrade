package xyz.elevated.frequency.check.impl.badpackets;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInKeepAlive;

@CheckData(name = "BadPackets (P)")
public class BadPacketsP extends PacketCheck {

    private int lastId = -1;

    public BadPacketsP(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {
        if (object instanceof WrappedPlayInKeepAlive) {
            WrappedPlayInKeepAlive wrapper = (WrappedPlayInKeepAlive) object;

            if (wrapper.getId() == lastId) {
                fail();
            }

            lastId = wrapper.getId();
        }
    }
}
