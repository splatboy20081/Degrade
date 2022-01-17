package xyz.elevated.frequency.data.impl;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import xyz.elevated.frequency.data.PlayerData;

@RequiredArgsConstructor
public final class ConnectionManager {
  private final PlayerData playerData;

  public void onTransaction(short actionNumber, long now) {
    Optional<Long> entry = getTransactionTime(actionNumber);

    entry.ifPresent(time -> playerData.getTransactionPing().set(now - time));
  }

  public void onKeepAlive(int identification, long now) {
    Optional<Long> entry = getKeepAliveTime(identification);

    entry.ifPresent(time -> playerData.getKeepAlivePing().set(now - time));
  }

  public Optional<Long> getTransactionTime(short actionNumber) {
    Map<Short, Long> entries = playerData.getTransactionUpdates();

    if (entries.containsKey(actionNumber)) return Optional.of(entries.get(actionNumber));

    return Optional.empty();
  }

  public Optional<Long> getKeepAliveTime(int identification) {
    Map<Integer, Long> entries = playerData.getKeepAliveUpdates();

    if (entries.containsKey(identification)) return Optional.of(entries.get(identification));

    return Optional.empty();
  }
}
