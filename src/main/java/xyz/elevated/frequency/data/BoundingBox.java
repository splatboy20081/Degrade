package xyz.elevated.frequency.data;

import java.util.ArrayList;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import xyz.elevated.frequency.util.NmsUtil;

@Getter
public final class BoundingBox {

  private double minX, minY, minZ;
  private double maxX, maxY, maxZ;

  private final long timestamp = System.currentTimeMillis();
  private final World world;

  public BoundingBox(Location position) {
    this(position.getX(), position.getY(), position.getZ(), position.getWorld());
  }

  public BoundingBox(double x, double y, double z, World world) {
    this(x, x, y, y, z, z, world);
  }

  public BoundingBox(
      double minX, double maxX, double minY, double maxY, double minZ, double maxZ, World world) {
    if (minX < maxX) {
      this.minX = minX;
      this.maxX = maxX;
    } else {
      this.minX = maxX;
      this.maxX = minX;
    }
    if (minY < maxY) {
      this.minY = minY;
      this.maxY = maxY;
    } else {
      this.minY = maxY;
      this.maxY = minY;
    }
    if (minZ < maxZ) {
      this.minZ = minZ;
      this.maxZ = maxZ;
    } else {
      this.minZ = maxZ;
      this.maxZ = minZ;
    }

    this.world = world;
  }

  public double distance(Location location) {
    return Math.sqrt(
        Math.min(Math.pow(location.getX() - minX, 2), Math.pow(location.getX() - maxX, 2))
            + Math.min(Math.pow(location.getZ() - minZ, 2), Math.pow(location.getZ() - maxZ, 2)));
  }

  public double distance(double x, double z) {
    double dx = Math.min(Math.pow(x - minX, 2), Math.pow(x - maxX, 2));
    double dz = Math.min(Math.pow(z - minZ, 2), Math.pow(z - maxZ, 2));

    return Math.sqrt(dx + dz);
  }

  public double distance(BoundingBox box) {
    double dx = Math.min(Math.pow(box.minX - minX, 2), Math.pow(box.maxX - maxX, 2));
    double dz = Math.min(Math.pow(box.minZ - minZ, 2), Math.pow(box.maxZ - maxZ, 2));

    return Math.sqrt(dx + dz);
  }

  public Vector getDirection() {
    double centerX = (minX + maxX) / 2.0;
    double centerY = (minY + maxY) / 2.0;
    double centerZ = (minZ + maxZ) / 2.0;

    return new Location(world, centerX, centerY, centerZ).getDirection();
  }

  public BoundingBox add(BoundingBox box) {
    minX += box.minX;
    minY += box.minY;
    minZ += box.minZ;

    maxX += box.maxX;
    maxY += box.maxY;
    maxZ += box.maxZ;

    return this;
  }

  public BoundingBox move(double x, double y, double z) {
    minX += x;
    minY += y;
    minZ += z;

    maxX += x;
    maxY += y;
    maxZ += z;

    return this;
  }

  public BoundingBox expand(double x, double y, double z) {
    minX -= x;
    minY -= y;
    minZ -= z;

    maxX += x;
    maxY += y;
    maxZ += z;

    return this;
  }

  public BoundingBox expandMax(double x, double y, double z) {
    maxX += x;
    maxY += y;
    maxZ += z;

    return this;
  }

  public boolean checkBlocks(Predicate<Material> predicate) {
    int first = (int) Math.floor(minX);
    int second = (int) Math.ceil(maxX);
    int third = (int) Math.floor(minY);
    int forth = (int) Math.ceil(maxY);
    int fifth = (int) Math.floor(minZ);
    int sixth = (int) Math.ceil(maxZ);

    ArrayList<Block> list = new ArrayList<>();

    list.add(world.getBlockAt(first, third, fifth));

    for (int i = first; i < second; ++i) {
      for (int j = third; j < forth; ++j) {
        for (int k = fifth; k < sixth; ++k) {
          list.add(world.getBlockAt(i, j, k));
        }
      }
    }

    return list.stream().allMatch(block -> predicate.test(block.getType()));
  }

  public double getCenterX() {
    return (minX + maxX) / 2.0;
  }

  public double getCenterY() {
    return (minY + maxY) / 2.0;
  }

  public double getCenterZ() {
    return (minZ + maxZ) / 2.0;
  }

  public long getTimestamp() {
    return timestamp;
  }
}

@Getter
@Setter
final class BlockPosition {

  private int x, y, z;

  public BlockPosition(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Block getBlock(World world) {
    return NmsUtil.getBlock(new Location(world, x, y, z));
  }
}
