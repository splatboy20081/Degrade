package xyz.elevated.frequency.data.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.observable.Observable;
import xyz.elevated.frequency.util.EvictingList;
import xyz.elevated.frequency.util.MathUtil;

@Getter
@RequiredArgsConstructor
public final class ActionManager {
  private final PlayerData playerData;
  private final EvictingList<Integer> clicks = new EvictingList<>(10);

  /*
  We're using observables so we don't reset variables all the time which hogs performance
   */
  private final Observable<Boolean> placing = new Observable<>(false);
  private final Observable<Boolean> attacking = new Observable<>(false);
  private final Observable<Boolean> swinging = new Observable<>(false);
  private final Observable<Boolean> digging = new Observable<>(false);
  private final Observable<Boolean> delayed = new Observable<>(false);
  private final Observable<Boolean> teleported = new Observable<>(false);
  private final Observable<Boolean> steer = new Observable<>(false);
  private final Observable<Boolean> packetDigging = new Observable<>(false);

  private int lastAttack,
      lastDig,
      lastFlying,
      lastDelayedFlying,
      lastTeleport,
      movements,
      lastPlace;

  public void onArmAnimation() {
    swinging.set(true);

    click:
    {
      if (digging.get() || movements > 5) break click;

      clicks.add(movements);
    }

    if (clicks.size() > 5) {
      double cps = MathUtil.getCps(clicks);
      double rate = cps * movements;

      playerData.getCps().set(cps);
      playerData.getRate().set(rate);
    }

    movements = 0;
  }

  public void onAttack() {
    attacking.set(true);

    lastAttack = playerData.getTicks().get();
  }

  public void onPlace() {
    placing.set(true);

    lastPlace = playerData.getTicks().get();
  }

  public void onDig() {
    packetDigging.set(true);

    lastDig = playerData.getTicks().get();
  }

  public void onFlying() {
    int now = playerData.getTicks().get();
    int attack = now - lastAttack;

    boolean delayed = now - lastFlying > 2;
    boolean digging = now - lastDig < 15 || packetDigging.get();
    boolean lagging = now - lastDelayedFlying < 2;
    boolean teleporting = now - lastTeleport < 2;
    boolean recent = attack < 200;

    placing.set(false);
    attacking.set(false);
    swinging.set(false);
    attacking.set(false);
    steer.set(false);
    packetDigging.set(false);

    this.digging.set(digging);
    this.delayed.set(lagging);
    teleported.set(teleporting);

    lastDelayedFlying = delayed ? now : lastDelayedFlying;
    lastFlying = now;

    playerData.getTarget().set(recent ? playerData.getTarget().get() : null);
    playerData.getTicks().set(now + 1);

    movements++;
  }

  public void onSteerVehicle() {
    steer.set(true);
  }

  public void onTeleport() {
    lastTeleport = playerData.getTicks().get();
  }

  public void onBukkitDig() {
    lastDig = playerData.getTicks().get();
  }
}
