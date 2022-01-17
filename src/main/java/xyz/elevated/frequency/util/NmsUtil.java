package xyz.elevated.frequency.util;

import io.netty.channel.ChannelPipeline;
import lombok.experimental.UtilityClass;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import xyz.elevated.frequency.data.PlayerData;

@UtilityClass
public class NmsUtil {

  public EntityPlayer getEntityPlayer(Player player) {
    return ((CraftPlayer) player).getHandle();
  }

  public EntityPlayer getEntityPlayer(PlayerData playerData) {
    Player player = playerData.getBukkitPlayer();

    return getEntityPlayer(player);
  }

  public ChannelPipeline getEntityPlayerPipeline(EntityPlayer entityPlayer) {
    return entityPlayer.playerConnection.networkManager.channel.pipeline();
  }

  public ChannelPipeline getPlayerPipeline(Player player) {
    return getEntityPlayerPipeline(getEntityPlayer(player));
  }

  public PlayerConnection getPlayerConnection(PlayerData playerData) {
    Player player = playerData.getBukkitPlayer();

    return getPlayerConnection(player);
  }

  public PlayerConnection getPlayerConnection(Player player) {
    EntityPlayer entityPlayer = getEntityPlayer(player);

    return entityPlayer.playerConnection;
  }

  public Block getBlock(Location location) {
    return isChunkLoaded(location) ? location.getBlock() : null;
  }

  public boolean isChunkLoaded(Location loc) {
    net.minecraft.server.v1_8_R3.World world = ((CraftWorld) loc.getWorld()).getHandle();

    return !world.isClientSide
        && world.isLoaded(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ()));
  }

  public Entity getEntity(org.bukkit.entity.Entity entity) {
    return ((CraftEntity) entity).getHandle();
  }

  public WorldServer getWorld(World world) {
    return ((CraftWorld) world).getHandle();
  }

  public Vector getMotion(Player player) {
    EntityPlayer entityPlayer = getEntityPlayer(player);

    double motionX = entityPlayer.motX;
    double motionY = entityPlayer.motY;
    double motionZ = entityPlayer.motZ;

    return new Vector(motionX, motionY, motionZ);
  }

  public Vector getMotion(PlayerData playerData) {
    Player player = playerData.getBukkitPlayer();

    return getMotion(player);
  }
}
