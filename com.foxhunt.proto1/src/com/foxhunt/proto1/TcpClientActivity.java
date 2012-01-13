package com.foxhunt.proto1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 13.01.12
 * Time: 1:38
 * To change this template use File | Settings | File Templates.
 */
public class TcpClientActivity extends Activity
{
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tcpclient);
		


	}

	public void btnRefresh_Click(View v)
	{
		String host="192.168.1.2";
		int port =  9000;
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		ClientBootstrap bootstrap = new ClientBootstrap(factory);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory()
		{
			@Override public ChannelPipeline getPipeline() throws Exception
			{
				return Channels.pipeline(new TimeClientHandler());
			}
		});

		bootstrap.setOption("tcpNoDelay",true);
		bootstrap.setOption("keepAlive",true);

		bootstrap.connect(new InetSocketAddress(host,port));
	}
	
private class TimeClientHandler extends SimpleChannelHandler {

	@Override public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
	{
		ChannelBuffer buf = (ChannelBuffer) e.getMessage();
		long currentTimeMillis = buf.readInt() * 1000L;
		final TextView txtTcpClientMain = (TextView) TcpClientActivity.this.findViewById(R.id.txtTcpClientMain);
		
		final String res = new Date(currentTimeMillis).toLocaleString();



		TcpClientActivity.this.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				txtTcpClientMain.setText(res);
			}
		}  );
	}

	@Override public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception
	{
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}
}