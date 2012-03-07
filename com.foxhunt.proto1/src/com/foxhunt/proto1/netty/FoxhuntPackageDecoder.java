package com.foxhunt.proto1.netty;

import com.foxhunt.proto1.packets.FoxhuntPacket;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 20.02.12
 * Time: 23:35
 * To change this template use File | Settings | File Templates.
 */
public class FoxhuntPackageDecoder extends SimpleChannelUpstreamHandler
{
	@Override public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception
	{
		if(!(e instanceof MessageEvent))
		{
			super.handleUpstream(ctx, e);    //To change body of overridden methods use File | Settings | File Templates.
		}
		else
		{
			Object msg = ((MessageEvent) e).getMessage();
			if(msg instanceof ChannelBuffer)
			{
				FoxhuntPacket packet = FoxhuntPacket.Deserialize(((ChannelBuffer) msg).array());
				if(packet!=null)
				{
					  Channels.fireMessageReceived(ctx, packet, ((MessageEvent) e).getRemoteAddress());
				}
				else
				{
					super.handleUpstream(ctx, e);
				}
			}
			else	
			{
				super.handleUpstream(ctx,e);
			}
		}
	}
}
