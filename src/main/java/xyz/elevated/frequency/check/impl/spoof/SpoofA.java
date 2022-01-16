package xyz.elevated.frequency.check.impl.spoof;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "Spoof (A)")
public class SpoofA extends PacketCheck {

    public SpoofA(PlayerData playerData) {
        super(playerData);
    }

    @Override
    public void process(Object object) {

        if (object instanceof WrappedPlayInFlying) {

            boolean exempt = isExempt(ExemptType.TELEPORTING, ExemptType.CHUNK, ExemptType.VEHICLE);

            if (exempt) fail();

            if (((WrappedPlayInFlying) object).onGround() && ((WrappedPlayInFlying) object).getY() % 0.015625 != 0)
                fail();

            if(((WrappedPlayInFlying) object).onGround() && ((WrappedPlayInFlying) object).getY() % 0.015625 != 0.5d)
                fail();

        }
    }
}
