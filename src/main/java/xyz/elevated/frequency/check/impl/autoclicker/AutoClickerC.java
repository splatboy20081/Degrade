package xyz.elevated.frequency.check.impl.autoclicker;

import com.google.common.collect.Lists;
import java.util.Deque;
import java.util.List;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.util.MathUtil;
import xyz.elevated.frequency.util.Pair;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInArmAnimation;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "AutoClicker (C)")
public final class AutoClickerC extends PacketCheck {

  private int movements, buffer;
  private final Deque<Integer> samples = Lists.newLinkedList();

  public AutoClickerC(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(Object object) {
    if (object instanceof WrappedPlayInArmAnimation) {
      boolean valid = movements < 4 && !playerData.getActionManager().getDigging().get();

      if (valid) samples.add(movements);

      // Sample size is adjustable. Can flag as low as 12CPS or lower depending on clicker patterns.
      if (samples.size() == 15) {
        Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

        double skewness = MathUtil.getSkewness(samples);
        double kurtosis = MathUtil.getKurtosis(samples);
        double outliers = outlierPair.getX().size() + outlierPair.getY().size();

        // See if skewness and kurtosis is exceeding a specific limit.
        if (skewness < 0.76 && kurtosis < 0.01 && outliers < 2) {
          if (++buffer > 1) {
            fail();
          }
        } else {
          buffer = 0;
        }

        samples.clear();
      }
      movements = 0;
    } else if (object instanceof WrappedPlayInFlying) {
      ++movements;
    }
  }
}
