package xyz.elevated.frequency.packet;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListenerPlayIn;
import net.minecraft.server.v1_8_R3.PacketListenerPlayOut;
import xyz.elevated.frequency.Frequency;
import xyz.elevated.frequency.data.PlayerData;
import xyz.elevated.frequency.processor.impl.IncomingPacketProcessor;
import xyz.elevated.frequency.processor.impl.OutgoingPacketProcessor;

@RequiredArgsConstructor
public final class PacketHandler extends ChannelDuplexHandler {

    private final PlayerData playerData;

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) throws Exception {
        super.write(channelHandlerContext, object, channelPromise);

        try {
            Packet<PacketListenerPlayOut> packet = (Packet<PacketListenerPlayOut>) object;

            Frequency.INSTANCE.getProcessorManager()
                    .getProcessor(OutgoingPacketProcessor.class)
                    .process(playerData, packet);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        super.channelRead(channelHandlerContext, object);

        try {
            Packet<PacketListenerPlayIn> packet = (Packet<PacketListenerPlayIn>) object;

            Frequency.INSTANCE.getProcessorManager()
                    .getProcessor(IncomingPacketProcessor.class)
                    .process(playerData, packet);
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
