package com.foxhunt.proto1.packets;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 17.02.12
 * Time: 19:03
 * To change this template use File | Settings | File Templates.
 */
public class EnvironmentUpdateRequestPacketU extends FoxhuntPacket
{
	@Override public int getPackageType()
	{
		return 5;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override protected void Serialize(DataOutputStream stream) throws IOException
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override public String toString()
	{
		return "Environment update request";
	}
}
