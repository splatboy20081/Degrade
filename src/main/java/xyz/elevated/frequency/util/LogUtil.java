package xyz.elevated.frequency.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtil {

  /** @param log - The string you want to print under Frequency. */
  public void log(String log) {
    System.out.println("[Degrade]: " + log);
  }
}
