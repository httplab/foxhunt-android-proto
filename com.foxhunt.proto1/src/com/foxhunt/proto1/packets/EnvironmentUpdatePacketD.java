package com.foxhunt.proto1.packets;

import com.foxhunt.proto1.entity.Fox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 19.02.12
 * Time: 1:56
 * To change this template use File | Settings | File Templates.
 */
public class EnvironmentUpdatePacketD extends FoxhuntPacket
{
	private Fox[] foxes;
	private long serverTime;

	@Override public int getPackageType()
	{
		return ENVIRONMENT_UPDATE_D;
	}

	@Override protected void Serialize(DataOutputStream stream) throws IOException
	{
		stream.writeLong(serverTime);
		stream.writeInt(foxes.length);
		for(int i=0; i<foxes.length; i++)
		{
			stream.write(foxes[i].Serialize());
		}
	}

    public Fox[] getFoxes()
    {
        return foxes;
    }
    
	public EnvironmentUpdatePacketD(DataInputStream stream) throws IOException
	{
		serverTime = stream.readLong();
		int count = stream.readInt();
		foxes = new Fox[count];
		for(int i=0; i<count; i++)
		{
			foxes[i] = Fox.Deserialize(stream);
		}
	}

	@Override public String toString()
	{
		return "Environment Update";
	}
}
