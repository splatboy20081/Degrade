package xyz.elevated.frequency.check.type;

import xyz.elevated.frequency.check.Check;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.update.RotationUpdate;

public class RotationCheck extends Check<RotationUpdate> {

  public RotationCheck(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(RotationUpdate rotationUpdate) {}
}
