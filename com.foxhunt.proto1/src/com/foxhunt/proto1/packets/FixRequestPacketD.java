package com.foxhunt.proto1.packets;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 17.02.12
 * Time: 18:50
 * To change this template use File | Settings | File Templates.
 */
public class FixRequestPacketD extends FoxhuntPacket
{
	@Override public int getPackageType()
	{
		return FIX_REQUEST_D;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public String toString()
	{
		return "Fix request";
	}

	@Override protected void Serialize(DataOutputStream stream) throws IOException
	{

	}
}
