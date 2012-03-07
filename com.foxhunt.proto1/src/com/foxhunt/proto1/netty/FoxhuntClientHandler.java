package com.foxhunt.proto1.netty;

import com.foxhunt.proto1.FoxhuntTCPClient;
import com.foxhunt.proto1.packets.FoxhuntPacket;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 06.03.12
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */
public class FoxhuntClientHandler extends SimpleChannelUpstreamHandler{
    private FoxhuntTCPClient client;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        client.EnqueueReceivedPacket((FoxhuntPacket)e.getMessage());
    }

    public FoxhuntClientHandler(FoxhuntTCPClient client)
    {
        this.client = client;
    }
}
