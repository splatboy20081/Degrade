package xyz.elevated.frequency.check.impl.badpackets;

import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PostCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInBlockDig;

@CheckData(name = "BadPackets (A)")
public final class BadPacketsA extends PostCheck {

  public BadPacketsA(PlayerData playerData) {
    super(playerData, WrappedPlayInBlockDig.class);
  }

  @Override
  public void process(Object object) {
    boolean post = isPost(object);

    if (post) fail();
  }
}
