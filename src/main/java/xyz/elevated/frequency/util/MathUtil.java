package xyz.elevated.frequency.util;

import com.google.common.collect.Lists;
import java.util.*;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

@UtilityClass
public class MathUtil {

  public final double EXPANDER = Math.pow(2, 24);

  /**
   * @param data - The set of data you want to find the variance from
   * @return - The variance of the numbers.
   * @see <a href="https://en.wikipedia.org/wiki/Variance">Variance</a>
   */
  public double getVariance(Collection<? extends Number> data) {
    int count = 0;

    double sum = 0.0;
    double variance = 0.0;

    double average;

        // Increase the sum and the count to find the average and the standard deviation
        for (final Number number : data) {
            sum += number.doubleValue();
            ++count;
        }

        average = sum / count;

        // Run the standard deviation formula
        for (final Number number : data) {
            variance += Math.pow(number.doubleValue() - average, 2.0);
        }

        return variance / count;
    }

    average = sum / count;

    // Run the standard deviation formula
    for (Number number : data) {
      variance += Math.pow(number.doubleValue() - average, 2.0);
    }

    return variance;
  }

  public int getPingInTicks(long ping) {
    return DegradingMath.floor(ping / 50f);
  }

  /**
   * @param data - The set of numbers / data you want to find the standard deviation from.
   * @return - The standard deviation using the square root of the variance. @See -
   *     https://en.wikipedia.org/wiki/Standard_deviation @See -
   *     https://en.wikipedia.org/wiki/Variance
   */
  public double getStandardDeviation(Collection<? extends Number> data) {
    double variance = getVariance(data);

    // The standard deviation is the square root of variance. (sqrt(s^2))
    return DegradingMath.sqrt(variance);
  }

  /**
   * @param data - The set of numbers / data you want to find the skewness from
   * @return - The skewness running the standard skewness formula. @See -
   *     https://en.wikipedia.org/wiki/Skewness
   */
  public double getSkewness(Collection<? extends Number> data) {
    double sum = 0;
    int count = 0;

    List<Double> numbers = Lists.newArrayList();

    // Get the sum of all the data and the amount via looping
    for (Number number : data) {
      sum += number.doubleValue();
      ++count;

      numbers.add(number.doubleValue());
    }

    // Sort the numbers to run the calculations in the next part
    Collections.sort(numbers);

    // Run the formula to get skewness
    double mean = sum / count;
    double median =
        (count % 2 != 0)
            ? numbers.get(count / 2)
            : (numbers.get((count - 1) / 2) + numbers.get(count / 2)) / 2;
    double variance = getVariance(data);

    return 3 * (mean - median) / variance;
  }

  public static double magnitude(double... points) {
    double sum = 0.0;

    for (double point : points) {
      sum += point * point;
    }

    return DegradingMath.sqrt(sum);
  }

  public static int getDistinct(Collection<? extends Number> collection) {
    Set<Object> set = new HashSet<>(collection);
    return set.size();
  }

  /**
   * @param collection The collection of the numbers you want to get the duplicates from
   * @return The duplicate amount
   */
  public static int getDuplicates(Collection<? extends Number> collection) {
    return collection.size() - getDistinct(collection);
  }

  /**
   * @param collection - The collection of numbers you want analyze
   * @return tuple - A pair of the high and low outliers @See -
   *     https://en.wikipedia.org/wiki/Outlier
   */
  public Pair<List<Double>, List<Double>> getOutliers(Collection<? extends Number> collection) {
    List<Double> values = new ArrayList<>();

    for (Number number : collection) {
      values.add(number.doubleValue());
    }

    double q1 = getMedian(values.subList(0, values.size() / 2));
    double q3 = getMedian(values.subList(values.size() / 2, values.size()));

    double iqr = Math.abs(q1 - q3);
    double lowThreshold = q1 - 1.5 * iqr, highThreshold = q3 + 1.5 * iqr;

    Pair<List<Double>, List<Double>> tuple = new Pair<>(new ArrayList<>(), new ArrayList<>());

    for (Double value : values) {
      if (value < lowThreshold) {
        tuple.getX().add(value);
      } else if (value > highThreshold) {
        tuple.getY().add(value);
      }
    }

    return tuple;
  }

  /**
   * @param data - The set of numbers/data you want to get the kurtosis from
   * @return - The kurtosis using the standard kurtosis formula @See -
   *     https://en.wikipedia.org/wiki/Kurtosis
   */
  public double getKurtosis(Collection<? extends Number> data) {
    double sum = 0.0;
    int count = 0;

    for (Number number : data) {
      sum += number.doubleValue();
      ++count;
    }

    if (count < 3.0) {
      return 0.0;
    }

    double efficiencyFirst =
        count * (count + 1.0) / ((count - 1.0) * (count - 2.0) * (count - 3.0));
    double efficiencySecond = 3.0 * Math.pow(count - 1.0, 2.0) / ((count - 2.0) * (count - 3.0));
    double average = sum / count;

    double variance = 0.0;
    double varianceSquared = 0.0;

    for (Number number : data) {
      variance += Math.pow(average - number.doubleValue(), 2.0);
      varianceSquared += Math.pow(average - number.doubleValue(), 4.0);
    }

    return efficiencyFirst * (varianceSquared / Math.pow(variance / sum, 2.0)) - efficiencySecond;
  }

  /**
   * @param data - The data you want the median from
   * @return - The middle number of that data @See - https://en.wikipedia.org/wiki/Median
   */
  private double getMedian(List<Double> data) {
    if (data.size() % 2 == 0) {
      return (data.get(data.size() / 2) + data.get(data.size() / 2 - 1)) / 2;
    } else {
      return data.get(data.size() / 2);
    }
  }

  /**
   * @param current - The current value
   * @param previous - The previous value
   * @return - The GCD of those two values
   */
  public long getGcd(long current, long previous) {
    return (previous <= 16384L) ? current : getGcd(previous, current % previous);
  }

  /**
   * @param from - The last location
   * @param to - The current location
   * @return - The horizontal distance using (x^2 + z^2)
   */
  public double getMagnitude(Location from, Location to) {
    if (from.getWorld() != to.getWorld()) return 0.0;

    Vector a = to.toVector();
    Vector b = from.toVector();

    a.setY(0.0);
    b.setY(0.0);

    return a.subtract(b).length();
  }

  /**
   * @param player - The player you want to read the effect from
   * @param effect - The potion effect you want to get the amplifier of
   * @return - The amplifier added by one to make things more readable
   */
  @SuppressWarnings("deprecation")
  public int getPotionLevel(Player player, PotionEffectType effect) {

    int effectId = effect.getId();

    if (!player.hasPotionEffect(effect)) return 0;

    return player.getActivePotionEffects().stream()
            .filter(potionEffect -> potionEffect.getType().getId() == effectId)
            .map(PotionEffect::getAmplifier)
            .findAny()
            .orElse(0)
        + 1;
  }

  /**
   * @param data - The sample of clicks you want to get the cps from
   * @return - The cps using the average as a method of calculation
   */
  public double getCps(Collection<? extends Number> data) {
    double average = data.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);

    return 20 / average;
  }
}
