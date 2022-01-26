package xyz.elevated.frequency.check.impl.badpackets;

import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInEntityAction;

@CheckData(name = "BadPacketsR")
public final class BadPacketsR extends PacketCheck {

    public BadPacketsR(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {

        if(object instanceof WrappedPlayInEntityAction) {

            final WrappedPlayInEntityAction action = (WrappedPlayInEntityAction) object;
            if (!(action.getAction() == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING) && playerData.getSprinting().get()) fail();

        }

    }
}
