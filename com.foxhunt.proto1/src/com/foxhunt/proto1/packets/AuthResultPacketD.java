package com.foxhunt.proto1.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 17.02.12
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class AuthResultPacketD extends FoxhuntPacket
{
	private boolean result;
	private String message;

	public String getMessage()
	{
		return message;
	}

	public boolean isResult()
	{
		return result;
	}

	public AuthResultPacketD(boolean result, String message)
	{
		this.result = result;
		this.message = message;
	}
	
	@Override protected void Serialize(DataOutputStream stream) throws IOException
	{
		stream.write(result ? 1 : 0);
		stream.writeUTF(message);
	}

	public AuthResultPacketD(DataInputStream stream)  throws  Exception
	{
		int c = stream.readByte();
		result = !(c==0);
		message =  stream.readUTF();
	}

	@Override public int getPackageType()
	{
		return AUTH_RESULT_D;
	}

	@Override public String toString()
	{
		try
		{
			return String.format("AuthResult: %1s : %2s", result?"OK" : "FAIL", message);
		}
		catch (Exception ex)
		{
			return ex.getMessage();
		}
	}
}
