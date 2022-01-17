package xyz.elevated.frequency.check.impl.aimassist;

import com.google.common.collect.Lists;
import java.util.Deque;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.RotationCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.update.RotationUpdate;
import xyz.elevated.frequency.util.MathUtil;

@CheckData(name = "AimAssist (A)")
public final class AimAssistA extends RotationCheck {

  private final Deque<Float> samples = Lists.newLinkedList();
  private double buffer;

  public AimAssistA(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(RotationUpdate rotationUpdate) {
    // Get the client ticks
    int now = playerData.getTicks().get();

    // Get the deltas from the rotation update
    float deltaYaw = rotationUpdate.getDeltaYaw();
    float deltaPitch = rotationUpdate.getDeltaPitch();

    // Make sure the player isn't using cinematic
    boolean cinematic = playerData.getCinematic().get();
    boolean attacking = now - playerData.getActionManager().getLastAttack() < 2;

    // If the conditions are met, add to the list
    if (deltaYaw > 0.0
        && deltaPitch > 0.0
        && deltaYaw < 30.f
        && deltaPitch < 30.f
        && !cinematic
        && attacking) {
      samples.add(deltaPitch);
    }

    // If the list has reached a sample size of 120
    if (samples.size() == 120) {
      // Get the duplicates through the distinct method in the list
      int distinct = MathUtil.getDistinct(samples);
      int duplicates = samples.size() - distinct;

      // Get the average from all the rotations to make sure they were't just spamming around their
      // aim
      double average = samples.stream().mapToDouble(d -> d).average().orElse(0.0);

      // If the duplicates are extremely low the player didn't have a valid rotation constant
      if (duplicates <= 9 && average < 30.f) {
        if (++buffer > 3) {
          fail();
        }
      } else {
        buffer = Math.max(buffer - 3, 0);
      }

      // Clear the samples
      samples.clear();
    }
  }
}
