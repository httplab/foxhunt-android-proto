package com.foxhunt.proto1.netty;

import com.foxhunt.proto1.packets.FoxhuntPacket;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelDownstreamHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 17.02.12
 * Time: 17:05
 * To change this template use File | Settings | File Templates.
 */
public class FoxhuntPackageEncoder extends SimpleChannelDownstreamHandler
{
	@Override public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		FoxhuntPacket packet = (FoxhuntPacket) e.getMessage();
		byte[] data = packet.Serialize();
		ChannelBuffer buffer = ChannelBuffers.buffer(data.length);
		buffer.writeBytes(data);
		Channels.write(ctx, e.getFuture(), buffer);
	}


}
