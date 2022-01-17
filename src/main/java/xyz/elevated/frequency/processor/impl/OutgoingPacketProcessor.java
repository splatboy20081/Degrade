package xyz.elevated.frequency.processor.impl;

import net.minecraft.server.v1_8_R3.*;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.processor.type.Processor;
import xyz.elevated.frequency.wrapper.impl.server.WrappedPlayOutEntityVelocity;
import xyz.elevated.frequency.wrapper.impl.server.WrappedPlayOutTeleport;

public final class OutgoingPacketProcessor implements Processor<Packet<PacketListenerPlayOut>> {

    @Override
    public void process(PlayerData playerData, Packet<PacketListenerPlayOut> packet) {
        if (packet instanceof PacketPlayOutEntityVelocity) {
            WrappedPlayOutEntityVelocity wrapper = new WrappedPlayOutEntityVelocity(packet);

            int packetEntityId = wrapper.getEntityId();
            int playerEntityId = playerData.getBukkitPlayer().getEntityId();

            if (packetEntityId == playerEntityId) {
                double velocityX = wrapper.getX();
                double velocityY = wrapper.getY();
                double velocityZ = wrapper.getZ();

                playerData.getVelocityManager().addVelocityEntry(velocityX, velocityY, velocityZ);
            }
        } else if (packet instanceof PacketPlayOutEntityTeleport) {
            WrappedPlayOutTeleport wrapper = new WrappedPlayOutTeleport(packet);

            int entityId = wrapper.getEntityId();
            int playerId = playerData.getBukkitPlayer().getEntityId();

            if (entityId == playerId) {
                playerData.getActionManager().onTeleport();
            }
        } else if (packet instanceof PacketPlayOutPosition) {
            playerData.getActionManager().onTeleport();
        }
    }
}
