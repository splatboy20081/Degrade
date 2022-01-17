package xyz.elevated.frequency.check.impl.killaura;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "KillAura (G)")
public final class KillAuraG extends PacketCheck {

  public KillAuraG(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(Object object) {
    if (object instanceof WrappedPlayInFlying) {
      boolean attacking = playerData.getActionManager().getAttacking().get();
      boolean swinging = playerData.getActionManager().getSwinging().get();

      if (attacking && !swinging) {
        fail();
      }
    }
  }
}
