package xyz.elevated.frequency.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInUseEntity;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInWindowClick;

@CheckData(name = "BadPackets (J)")
public final class BadPacketsJ extends PacketCheck {

    public BadPacketsJ(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {
        if (object instanceof WrappedPlayInUseEntity) {
            WrappedPlayInUseEntity wrapper = (WrappedPlayInUseEntity) object;
            handle: {
                if (wrapper.getAction() != PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) break handle;

                boolean placing = playerData.getActionManager().getPlacing().get();

                if (placing) fail();
            }
        }
    }
}
