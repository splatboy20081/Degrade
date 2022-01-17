package xyz.elevated.frequency.processor.type;

import xyz.elevated.frequency.data.PlayerData;

public interface Processor<T> {
  void process(PlayerData playerData, T t);
}
