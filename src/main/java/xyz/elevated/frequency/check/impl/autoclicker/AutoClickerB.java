package xyz.elevated.frequency.check.impl.autoclicker;

import com.google.common.collect.Lists;
import java.util.Deque;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.util.MathUtil;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInArmAnimation;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "AutoClicker (B)")
public final class AutoClickerB extends PacketCheck {

  private final Deque<Integer> samples = Lists.newLinkedList();
  private int movements, streak;
  private double lastKurtosis, lastSkewness, lastDeviation;

  public AutoClickerB(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(Object object) {
    if (object instanceof WrappedPlayInArmAnimation) {
      boolean valid = movements < 4 && !playerData.getActionManager().getDigging().get();

      // If the movements are lower than 4 and the player isn;t digging
      if (valid) samples.add(movements);

      if (samples.size() == 10) {
        // Get the standard deviation skewness and kurtosis from math utils
        double deviation = MathUtil.getStandardDeviation(samples);
        double skewness = MathUtil.getSkewness(samples);
        double kurtosis = MathUtil.getKurtosis(samples);

        // If the statistic values are the same for two sample rotations, flag
        if (deviation == lastDeviation && skewness == lastSkewness && kurtosis == lastKurtosis) {
          if (++streak > 2) {
            fail();
          }
        } else {
          streak = 0;
        }

        // Parse values to the last values and clear the list
        lastDeviation = deviation;
        lastKurtosis = kurtosis;
        lastSkewness = skewness;
        samples.clear();
      }

      // Reset the movements
      movements = 0;
    } else if (object instanceof WrappedPlayInFlying) {
      ++movements;
    }
  }
}
