package xyz.elevated.frequency.check.impl.badpackets;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInHeldItemSlot;

@CheckData(name = "BadPackets (F)")
public final class BadPacketsF extends PacketCheck {

  private int lastSlot = -1;

  public BadPacketsF(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(Object object) {
    if (object instanceof WrappedPlayInHeldItemSlot) {
      WrappedPlayInHeldItemSlot wrapper = (WrappedPlayInHeldItemSlot) object;

      if (wrapper.getSlot() == lastSlot) fail();

      lastSlot = wrapper.getSlot();
    }
  }
}
