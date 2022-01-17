package xyz.elevated.frequency.check.impl.badpackets;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PostCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInArmAnimation;

@CheckData(name = "BadPackets (B)")
public final class BadPacketsB extends PostCheck {

  public BadPacketsB(PlayerData playerData) {
    super(playerData, WrappedPlayInArmAnimation.class);
  }

  @Override
  public void process(Object object) {
    boolean post = isPost(object);

    if (post) fail();
  }
}
