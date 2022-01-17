package xyz.elevated.frequency.check.impl.badpackets;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PostCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInWindowClick;

@CheckData(name = "BadPackets (M)")
public final class BadPacketsM extends PostCheck {

  public BadPacketsM(PlayerData playerData) {
    super(playerData, WrappedPlayInWindowClick.class);
  }

  @Override
  public void process(Object object) {
    boolean post = isPost(object);

    if (post) fail();
  }
}
