package xyz.elevated.frequency.check.impl.autoclicker;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import xyz.elevated.frequency.check.CheckData;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.util.MathUtil;
import xyz.elevated.frequency.util.Pair;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInArmAnimation;
import xyz.elevated.frequency.wrapper.impl.client.WrappedPlayInFlying;

@CheckData(name = "AutoClicker (E)")
public final class AutoClickerE extends PacketCheck {

  private int movements;
  private final Deque<Integer> samples = new LinkedList<>();

  public AutoClickerE(PlayerData playerData) {
    super(playerData);
  }

  @Override
  public void process(Object object) {
    if (object instanceof WrappedPlayInArmAnimation) {
      boolean valid =
          playerData.getCps().get() > 6.5
              && movements < 5
              && !playerData.getActionManager().getDigging().get()
              && !playerData.getActionManager().getPlacing().get();

      if (valid) samples.add(movements);

      if (samples.size() == 20) {
        Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

        int outliers = outlierPair.getX().size() + outlierPair.getY().size();
        int duplicates = MathUtil.getDuplicates(samples);

        // Impossible consistency
        if (outliers < 2 && duplicates > 15) fail();

        samples.clear();
      }

      movements = 0;
    } else if (object instanceof WrappedPlayInFlying) {
      ++movements;
    }
  }
}
