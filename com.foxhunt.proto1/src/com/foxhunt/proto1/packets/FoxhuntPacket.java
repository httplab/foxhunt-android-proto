package com.foxhunt.proto1.packets;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 17.01.12
 * Time: 23:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class FoxhuntPacket
{
	public static final int AUTH_REQUEST_U = 0x00000000;
	public static final int AUTH_RESULT_D = 0x00000001;
	public static final int FIX_U = 0x00000002;
	public static final int FIX_REQUEST_D = 0x00000003;
	public static final int ENVIRONMENT_UPDATE_D = 0x00000004;
	public static final int ENVIRONMENT_UPDATE_REQUEST_U = 0x00000005;
	public static final int USER_INPUT_U = 0x00000006;
	public static final int SYSTEM_MESSAGE_D = 0x00000007;
    public static final int GAME_EVENT_PACKET_D = 0x00000008;
	
	public byte[] Serialize() throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(stream);
		Serialize(dos);
		dos.close();
		byte[] dataBody = stream.toByteArray();
		stream = new ByteArrayOutputStream();
		dos = new DataOutputStream(stream);
		dos.writeInt(getPackageType());
		dos.write(dataBody);
		dos.close();
		return stream.toByteArray();
	}

	protected abstract void Serialize(DataOutputStream stream) throws IOException;

	public abstract int getPackageType();

	@Override public String toString()
	{
		try
		{
			return String.format("%1$d", getPackageType());
		}
		catch (Exception ex)
		{
			return ex.getMessage();
		}
	}

	public static FoxhuntPacket Deserialize(byte[] data)  throws Exception
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		int packageType = dis.readInt();
		FoxhuntPacket res = null;
		switch (packageType)
		{
			case AUTH_REQUEST_U:
				res = new ConnectionRequestPacketU(dis);
				break;
			case AUTH_RESULT_D:
				res = new AuthResultPacketD(dis);
				break;
			case FIX_U:
				res = new FixPacketU(dis);
				break;
			case FIX_REQUEST_D:
				res = new FixRequestPacketD();
				break;
			case ENVIRONMENT_UPDATE_REQUEST_U:
				res = new EnvironmentUpdateRequestPacketU();
				break;
			case ENVIRONMENT_UPDATE_D:
				res = new EnvironmentUpdatePacketD(dis);
				break;
			case SYSTEM_MESSAGE_D:
				res = new SystemMessagePacketD(dis);
				break;
            case GAME_EVENT_PACKET_D:
                res = new GameEventPacketD(dis);
                break;
			default:
				res = null;
		}

		dis.close();
		return res;
	}
}
