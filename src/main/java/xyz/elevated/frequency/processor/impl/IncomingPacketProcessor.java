package xyz.elevated.frequency.processor.impl;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import xyz.elevated.frequency.check.type.PacketCheck;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.data.impl.PositionManager;
import xyz.elevated.frequency.data.impl.RotationManager;
import xyz.elevated.frequency.processor.type.Processor;
import xyz.elevated.frequency.util.NmsUtil;
import xyz.elevated.frequency.wrapper.impl.client.*;

public final class IncomingPacketProcessor implements Processor<Packet<PacketListenerPlayIn>> {

  @Override
  public void process(PlayerData playerData, Packet<PacketListenerPlayIn> packet) {
    if (packet instanceof PacketPlayInFlying) {
      WrappedPlayInFlying wrapper = new WrappedPlayInFlying(packet);

      double posX = wrapper.getX();
      double posY = wrapper.getY();
      double posZ = wrapper.getZ();

      float yaw = wrapper.getYaw();
      float pitch = wrapper.getPitch();

      boolean hasPos = wrapper.hasPos();
      boolean hasLook = wrapper.hasLook();
      boolean onGround = wrapper.onGround();

      if (hasPos) {
        PositionManager positionManager = playerData.getPositionManager();
        World world = playerData.getBukkitPlayer().getWorld();

        positionManager.handle(world, posX, posY, posZ, onGround);
      }

      if (hasLook) {
        RotationManager rotationManager = playerData.getRotationManager();

        rotationManager.handle(yaw, pitch);
      }

      playerData.getVelocityManager().apply();
      playerData.getActionManager().onFlying();
      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInUseEntity) {
      WrappedPlayInUseEntity wrapper = new WrappedPlayInUseEntity(packet);

      if (wrapper.getAction() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
        playerData.getActionManager().onAttack();

        Entity entity =
            wrapper.getTarget(NmsUtil.getWorld(playerData.getBukkitPlayer().getWorld()));
        playerData.getTarget().set(entity);
      }

      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInBlockDig) {
      WrappedPlayInBlockDig wrapper = new WrappedPlayInBlockDig(packet);

      switch (wrapper.getDigType()) {
        default:
        case START_DESTROY_BLOCK:
        case ABORT_DESTROY_BLOCK:
        case STOP_DESTROY_BLOCK:
          {
            playerData.getActionManager().onDig();
            break;
          }
      }

      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInHeldItemSlot) {
      WrappedPlayInHeldItemSlot wrapper = new WrappedPlayInHeldItemSlot(packet);

      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInEntityAction) {
      WrappedPlayInEntityAction wrapper = new WrappedPlayInEntityAction(packet);

      switch (wrapper.getAction()) {
        case START_SPRINTING:
          {
            playerData.getSprinting().set(true);
            break;
          }

        case STOP_SPRINTING:
          {
            playerData.getSprinting().set(false);
            break;
          }
        default:
          {
            // no default
          }
      }

      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInCustomPayload) {
      WrappedPlayInCustomPayload wrapper = new WrappedPlayInCustomPayload(packet);

      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInArmAnimation) {
      WrappedPlayInArmAnimation wrapper = new WrappedPlayInArmAnimation(packet);

      playerData.getActionManager().onArmAnimation();
      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInKeepAlive) {
      WrappedPlayInKeepAlive wrapper = new WrappedPlayInKeepAlive(packet);

      playerData.getConnectionManager().onKeepAlive(wrapper.getId(), System.currentTimeMillis());
      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInClientCommand) {
      WrappedPlayInClientCommand wrapper = new WrappedPlayInClientCommand(packet);

      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInBlockPlace) {
      WrappedPlayInBlockPlace wrapper = new WrappedPlayInBlockPlace(packet);

      playerData.getActionManager().onPlace();
      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInSteerVehicle) {
      WrappedPlayInSteerVehicle wrapper = new WrappedPlayInSteerVehicle(packet);

      playerData.getActionManager().onSteerVehicle();
      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    } else if (packet instanceof PacketPlayInTransaction) {
      WrappedPlayInTransaction wrapper = new WrappedPlayInTransaction(packet);

      long now = System.currentTimeMillis();

      playerData.getConnectionManager().onTransaction(wrapper.getHash(), now);
      playerData.getCheckManager().getChecks().stream()
          .filter(PacketCheck.class::isInstance)
          .forEach(check -> check.process(wrapper));
    }
  }
}
