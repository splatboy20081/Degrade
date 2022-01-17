package xyz.elevated.frequency.check.impl.badpackets;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInSteerVehicle;

@CheckData(name = "BadPackets (K)")
public final class BadPacketsK extends PacketCheck {

  public BadPacketsK(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(Object object) {
    if (object instanceof WrappedPlayInSteerVehicle) {
      boolean exempt = isExempt(ExemptType.VEHICLE);

      if (exempt) {
        fail();
      }
    }
  }
}
