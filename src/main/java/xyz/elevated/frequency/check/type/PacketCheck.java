package xyz.elevated.frequency.check.type;

import xyz.elevated.frequency.check.Check;
import xyz.elevated.frequency.data.PlayerData;

public class PacketCheck extends Check<Object> {

  public PacketCheck(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(Object object) {
    playerData.getCheckManager().getChecks().stream()
        .filter(PostCheck.class::isInstance)
        .forEach(check -> check.process(object));
  }
}
