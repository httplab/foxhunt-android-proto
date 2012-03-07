package com.foxhunt.proto1.entity;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 18.02.12
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class Fox
{
	public final static int RED_FOX = 0;
	public final static int BLUE_FOX = 1;
	public final static int GRAY_FOX = 2;

	private int id;
	private double latitude;
	private double longitude;
	private String name;
	private int type;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public byte[] Serialize() throws IOException
	{
		ByteArrayOutputStream str = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(str);
		dos.writeInt(id);
		dos.writeDouble(latitude);
		dos.writeDouble(longitude);
		dos.writeUTF(name);
		dos.writeInt(type);
		dos.close();
		return str.toByteArray();
	}
	
	public static Fox Deserialize(DataInputStream stream) throws IOException
	{
		Fox res = new Fox();
		
		res.id = stream.readInt();
		res.latitude = stream.readDouble();
		res.longitude = stream.readDouble();
		res.name = stream.readUTF();
		res.type = stream.readInt();

		return res;
	}
}
