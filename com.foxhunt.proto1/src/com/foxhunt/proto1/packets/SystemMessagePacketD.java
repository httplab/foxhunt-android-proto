package com.foxhunt.proto1.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 19.02.12
 * Time: 2:25
 * To change this template use File | Settings | File Templates.
 */
public class SystemMessagePacketD extends FoxhuntPacket
{
	private String message;

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override public int getPackageType()
	{
		return SYSTEM_MESSAGE_D;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override protected void Serialize(DataOutputStream stream) throws IOException
	{
		stream.writeUTF(message);
	}

	public SystemMessagePacketD(DataInputStream stream) throws IOException
	{
		message = stream.readUTF();
	}
	
	public SystemMessagePacketD(String message)
	{
		this.message = message;
	}

	@Override public String toString()
	{
		return "System message: "  + message;
	}
}
