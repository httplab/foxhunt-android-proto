package com.foxhunt.proto1.packets;

import com.foxhunt.proto1.entity.Fix;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 17.02.12
 * Time: 19:05
 * To change this template use File | Settings | File Templates.
 */
public class FixPacketU extends FoxhuntPacket
{
	private Fix fix;

	@Override public int getPackageType()
	{
		return FIX_U;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public FixPacketU(Fix fix)
	{
		this.fix = fix;
	}
	
	public Fix getFix()
	{
		return fix;
	}

	public FixPacketU(DataInputStream stream) throws Exception
	{
		fix = new Fix();
		fix.setLongitude(stream.readDouble());
		fix.setLatitude(stream.readDouble());
		fix.setAccuracy(stream.readDouble());
		fix.setClientTime(stream.readLong());

		int c =stream.read();

		while (c!=-1)
		{
			switch (c)
			{
				case 0x00:
					fix.setAltitude(stream.readDouble());
					break;
				case 0x01:
					fix.setSpeed(stream.readDouble());
					break;
				case 0x02:
					fix.setBearing(stream.readDouble());
					break;
				case 0x03:
					fix.setFixTime(stream.readLong());
					break;
			}
		}
	}

	@Override protected void Serialize(DataOutputStream stream) throws IOException
	{
		stream.writeDouble(fix.getLongitude());
		stream.writeDouble(fix.getLatitude());
		stream.writeDouble(fix.getAccuracy());
		stream.writeLong(fix.getClientTime());
		stream.write(fix.getProviderId());
		if(fix.getAltitude()!=null)
		{
			stream.write(0x00);
			stream.writeDouble(fix.getAltitude().doubleValue());
		}

		if(fix.getSpeed()!=null)
		{
			stream.write(0x01);
			stream.writeDouble(fix.getSpeed().doubleValue());
		}

		if(fix.getBearing()!=null)
		{
			stream.write(0x02);
			stream.writeDouble(fix.getBearing().doubleValue());
		}

		if(fix.getFixTime()!=null)
		{
			stream.write(0x01);
			stream.writeLong(fix.getFixTime().longValue());
		}
	}

	@Override public String toString()
	{
		try
		{
			return String.format("Fix: %1s;", fix.toString());
		}
		catch (Exception ex)
		{
			return ex.getMessage();
		}
	}
}
