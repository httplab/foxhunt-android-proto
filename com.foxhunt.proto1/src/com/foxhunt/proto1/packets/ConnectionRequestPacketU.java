package com.foxhunt.proto1.packets;
import java.io.*;
import java.security.MessageDigest;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 30.01.12
 * Time: 22:55
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionRequestPacketU extends FoxhuntPacket
{
	private String login;
	private byte[] passwordHash;
	private String deviceId;
	private String simcardId;

	public String getDeviceId()
	{
		return deviceId;
	}

	public String getLogin()
	{
		return login;
	}

	public byte[] getPasswordHash()
	{
		return passwordHash;
	}

	public String getSimcardId()
	{
		return simcardId;
	}

	@Override protected void Serialize(DataOutputStream stream) throws IOException
	{
		stream.writeUTF(login);
		stream.write(passwordHash);
		if(deviceId!=null)
		{
			stream.writeByte(0x00);
			stream.writeUTF(deviceId);
		}

		if(simcardId!=null)
		{
			stream.writeByte(0x01);
			stream.writeUTF(simcardId);
		}
	}

	public ConnectionRequestPacketU(DataInputStream stream) throws Exception
	{
		int c;
		login = stream.readUTF();
		passwordHash = new byte[16];
		stream.read(passwordHash,0,16);
		c=stream.read();
		while(c!=-1)
		{
			if(c==0)
			{
				deviceId = stream.readUTF();
			}
			else if(c==1)
			{
				simcardId = stream.readUTF();
			}
			c = stream.read();
		}
	}

	public ConnectionRequestPacketU(String login, String password, String deviceId, String simcardId) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		this.login = login;
		this.passwordHash= md.digest(password.getBytes("UTF8"));
		this.deviceId = deviceId;
		this.simcardId = simcardId;
	}

	@Override public int getPackageType()
	{
		return AUTH_REQUEST_U;
	}

	@Override public String toString()
	{
		try
		{
			return String.format("ConnectionRequest: %1s;", login);
		}
		catch (Exception ex)
		{
			return ex.getMessage();
		}
	}
}
