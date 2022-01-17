package xyz.elevated.frequency.data.impl;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import xyz.elevated.frequency.check.type.PositionCheck;
import xyz.elevated.frequency.data.BoundingBox;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.exempt.ExemptManager;
import xyz.elevated.frequency.exempt.type.ExemptType;
import xyz.elevated.frequency.observable.Observable;
import xyz.elevated.frequency.update.PositionUpdate;

@RequiredArgsConstructor
@Getter
public final class PositionManager {
  @Getter(AccessLevel.NONE)
  private final PlayerData playerData;

  @Getter(AccessLevel.NONE)
  private double lastPosX, lastPosY, lastPosZ;

  private final Observable<Boolean> touchingAir = new Observable<>(false);
  private final Observable<Boolean> touchingLiquid = new Observable<>(false);
  private final Observable<Boolean> touchingHalfBlock = new Observable<>(false);
  private final Observable<Boolean> touchingClimbable = new Observable<>(false);
  private final Observable<Boolean> touchingIllegalBlock = new Observable<>(false);
  private final Observable<Object[]> nearbyEntities = new Observable<>(null);

  public synchronized void handle(
      World world, double posX, double posY, double posZ, boolean onGround) {
    BoundingBox boundingBox = new BoundingBox(posX, posY, posZ, world);

    Player bukkitPlayer = playerData.getBukkitPlayer();
    Object[] entities = bukkitPlayer.getNearbyEntities(3, 3, 3).toArray();

    // Convert the data to bukkit locations and parse them
    Location location = new Location(world, posX, posY, posZ);
    Location lastLocation = new Location(world, lastPosX, lastPosY, lastPosZ);

    PositionUpdate positionUpdate = new PositionUpdate(lastLocation, location, onGround);
    ExemptManager exemptManager = playerData.getExemptManager();

    playerData.getPositionUpdate().set(positionUpdate);

    // Make sure the player isn't inside the void or getting teleported
    if (exemptManager.isExempt(ExemptType.TELEPORTING, ExemptType.VOID)) {
      return;
    }

    // Make sure the player is actually moving
    if (location.distanceSquared(lastLocation) == 0.0) {
      return;
    }

    // Make sure the player isn't flying, and he isn't in a vehicle
    if (bukkitPlayer.isInsideVehicle() || bukkitPlayer.getAllowFlight()) {
      return;
    }

    // Make sure the player is not near vehicles
    if (Arrays.stream(entities).anyMatch(entity -> entity instanceof Vehicle)) return;

    // Compensate for nearby entities
    nearbyEntities.set(entities);

    // Parse the bounding boxes properly
    playerData.getBoundingBox().set(boundingBox);
    playerData.getBoundingBoxes().add(boundingBox);

    // Handle collisions
    handleCollisions(boundingBox);

    // Parse the position update to the checks
    playerData.getCheckManager().getChecks().stream()
        .filter(PositionCheck.class::isInstance)
        .forEach(check -> check.process(positionUpdate));

    // Pass the data to the last variables.
    lastPosX = posX;
    lastPosY = posY;
    lastPosZ = posZ;
  }

  private synchronized void handleCollisions(BoundingBox boundingBox) {
    boundingBox.expand(0.5, 0.07, 0.5).move(0.0, -0.55, 0.0);

    boolean touchingAir = boundingBox.checkBlocks(material -> material == Material.AIR);
    boolean touchingLiquid =
        boundingBox.checkBlocks(
            material ->
                material == Material.WATER
                    || material == Material.LAVA
                    || material == Material.STATIONARY_WATER
                    || material == Material.STATIONARY_LAVA);
    boolean touchingHalfBlock =
        boundingBox.checkBlocks(
            material -> material.getData() == Stairs.class || material.getData() == Step.class);
    boolean touchingClimbable =
        boundingBox.checkBlocks(
            material -> material == Material.LADDER || material == Material.LAVA);
    boolean touchingIllegalBlock =
        boundingBox.checkBlocks(
            material -> material == Material.WATER_LILY || material == Material.BREWING_STAND);

    this.touchingAir.set(touchingAir && !touchingIllegalBlock);
    this.touchingLiquid.set(touchingLiquid);
    this.touchingHalfBlock.set(touchingHalfBlock);
    this.touchingClimbable.set(touchingClimbable);
    this.touchingIllegalBlock.set(touchingIllegalBlock);
  }
}
