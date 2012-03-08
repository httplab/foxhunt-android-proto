package com.foxhunt.proto1;

import com.foxhunt.proto1.netty.FoxhuntPackageDecoder;
import com.foxhunt.proto1.netty.FoxhuntPackageEncoder;
import com.foxhunt.proto1.packets.AuthResultPacketD;
import com.foxhunt.proto1.packets.ConnectionRequestPacketU;
import com.foxhunt.proto1.packets.FoxhuntPacket;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 06.03.12
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class FoxhuntClient extends SimpleChannelUpstreamHandler
{
    public interface IncomingPacketListener extends EventListener
    {
        void OnIncomingPacket(FoxhuntPacket packet);
    }

    public interface ClientStateChangeListener extends EventListener
    {
        void OnStateChanged(FoxhuntClient client, ClientState oldState, ClientState newState, String message);
    }

    public enum ClientState
    {
        Online,
        Offline,
        Connecting,
    }

    private String username;
    private int port;
    private String password;
    private String host;
    private ClientState clientState = ClientState.Offline;
    private final List<IncomingPacketListener> incomingPacketListeners = new ArrayList<IncomingPacketListener>();
    private final List<ClientStateChangeListener> stateChangeListeners = new ArrayList<ClientStateChangeListener>();
    private ClientBootstrap bootstrap;
    private Channel channel;
    private ChannelFuture lastWriteFuture;

    public FoxhuntClient(String username, String password, String host, int port)
    {
        this.username = username;
        this.host = host;
        this.password = password;
        this.port = port;

        ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        bootstrap = new ClientBootstrap (factory);
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                LengthFieldBasedFrameDecoder frameDecoder = new LengthFieldBasedFrameDecoder(1024,0,4,0,4);
                LengthFieldPrepender prepender = new LengthFieldPrepender(4,false);
                return Channels.pipeline(prepender, new FoxhuntPackageEncoder(), frameDecoder, new FoxhuntPackageDecoder(),FoxhuntClient.this);
            }
        });
        bootstrap.setOption("tcpNoDelay" , true);
        bootstrap.setOption("keepAlive", true);
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public ClientState getClientState() {
        return clientState;
    }

    public void setClientState(ClientState clientState) {
        setClientState(clientState, null);
    }

    public void setClientState(ClientState clientState, String message) {
        ClientState oldState = getClientState();
        this.clientState = clientState;
        for(ClientStateChangeListener listener : stateChangeListeners)
        {
            listener.OnStateChanged(this,oldState,clientState, message);
        }
    }

    public void registerIncomingPacketListener(IncomingPacketListener listener)
    {
        incomingPacketListeners.add(listener);
    }

    public void unregisterIncomingPacketListener(IncomingPacketListener listener)
    {
        incomingPacketListeners.remove(listener);
    }

    public void registerStateChangedListener(ClientStateChangeListener listener)
    {
        stateChangeListeners.add(listener);
    }

    public void unregisterStateChangedListener(ClientStateChangeListener listener)
    {
        stateChangeListeners.remove(listener);
    }

    public void SendPacket(FoxhuntPacket packet)
    {
        lastWriteFuture = channel.write(packet);
    }

    public void Connect() throws Exception
    {
        if(this.getClientState()!=ClientState.Offline)
        {
            return;
        }

        ChannelFuture lastWriteFuture=null;
        setClientState(ClientState.Connecting);
        InetSocketAddress addr = new InetSocketAddress(getHost(), getPort());
        ChannelFuture connectWait = bootstrap.connect(addr);
        connectWait.awaitUninterruptibly();

        if(!connectWait.isSuccess())
        {
            connectWait.getCause();
            bootstrap.releaseExternalResources();
            setClientState(ClientState.Offline, connectWait.getCause().getMessage());
            return;
        }

        channel = connectWait.getChannel();

        lastWriteFuture = channel.write(new ConnectionRequestPacketU(getUsername(), getPassword(),"DUMMY","DUMMY"));
    }

    public void Disconnect()
    {
        if(getClientState()!=ClientState.Online)
        {
            return;
        }

        if (lastWriteFuture != null) {
            lastWriteFuture.awaitUninterruptibly();
        }

        channel.close().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
        setClientState(ClientState.Offline);
    }


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if(e.getMessage() instanceof FoxhuntPacket)
        {
            switch (getClientState())
            {
                case Connecting:
                    if(e.getMessage() instanceof AuthResultPacketD)
                    {
                        AuthResultPacketD authResultPacket = (AuthResultPacketD) e.getMessage();
                        if(authResultPacket.getResult())
                        {
                            setClientState(ClientState.Online, authResultPacket.getMessage());
                        }
                        else
                        {
                            setClientState(ClientState.Offline, authResultPacket.getMessage());
                            e.getChannel().getCloseFuture().awaitUninterruptibly();
                        }
                    }
                    break;
                case Online:
                    for(IncomingPacketListener listener: incomingPacketListeners)
                    {
                        listener.OnIncomingPacket((FoxhuntPacket)e.getMessage());
                    }
                    break;                    
            }
        }
    }
}
