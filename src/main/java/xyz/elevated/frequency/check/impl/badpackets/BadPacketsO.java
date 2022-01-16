package xyz.elevated.frequency.check.impl.badpackets;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInSteerVehicle;

@CheckData(name = "BadPackets (O)")
public class BadPacketsO extends PacketCheck {

    public BadPacketsO(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {
        if (object instanceof WrappedPlayInSteerVehicle) {
            WrappedPlayInSteerVehicle wrapper = (WrappedPlayInSteerVehicle) object;

            float forward = Math.abs(wrapper.getForward());
            float side = Math.abs(wrapper.getSide());

            // The max forward/side value is .98 or -.98
            boolean invalid = side > .98F || forward > .98F;

            if (invalid) {
                fail();
            }
        }
    }
}
