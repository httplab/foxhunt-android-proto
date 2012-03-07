package com.foxhunt.proto1;

import com.foxhunt.proto1.netty.FoxhuntClientHandler;
import com.foxhunt.proto1.netty.FoxhuntPackageDecoder;
import com.foxhunt.proto1.netty.FoxhuntPackageEncoder;
import com.foxhunt.proto1.packets.ConnectionRequestPacketU;
import com.foxhunt.proto1.packets.FoxhuntPacket;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 06.03.12
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class FoxhuntTCPClient extends Thread {
    private Queue<FoxhuntPacket> _packetQueue;
    private Queue<FoxhuntPacket> _outQueue;
    private boolean stop=true;
    private String host;
    private int port;
    private String username;
    private String password;
    private String deviceId="";
    private String simId="";

    public synchronized void EnqueuePacket(FoxhuntPacket packet)
    {
        _packetQueue.add(packet);
    }

    public void EnqueueReceivedPacket(FoxhuntPacket packet)
    {
        _outQueue.add(packet);
    }

    public FoxhuntTCPClient(String host, int port, String username, String password){
        this._packetQueue = new LinkedBlockingQueue<FoxhuntPacket>();
        this.host = host;
        this.password = password;
        this.port = port;
        this.username = username;
    }

    @Override
    public void interrupt() {
        stop = true;
    }

    @Override
    public void run() {
        if(stop==false)
        {
            return;
        }

        stop=false;

        ChannelFactory factory =
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool());
        ClientBootstrap bootstrap = new ClientBootstrap (factory);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                LengthFieldBasedFrameDecoder frameDecoder = new LengthFieldBasedFrameDecoder(1024,0,4,0,4);
                LengthFieldPrepender prepender = new LengthFieldPrepender(4,false);

                return Channels.pipeline(prepender, new FoxhuntPackageEncoder(), frameDecoder, new FoxhuntPackageDecoder(), new FoxhuntClientHandler(FoxhuntTCPClient.this));
            }
        });
        bootstrap.setOption("tcpNoDelay" , true);
        bootstrap.setOption("keepAlive", true);
        ChannelFuture connectWait = bootstrap.connect(new InetSocketAddress(host, port));
        connectWait = connectWait.awaitUninterruptibly();
        Channel channel = connectWait.getChannel();

        try
        {
            channel = channel.write(new ConnectionRequestPacketU(username,password,deviceId,simId)).awaitUninterruptibly().getChannel();
        } catch (Exception e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        while(!stop)
        {
            if(!_packetQueue.isEmpty())
            {
                channel = channel.write(_packetQueue.remove()).awaitUninterruptibly().getChannel();
            }
        }
    }
}
