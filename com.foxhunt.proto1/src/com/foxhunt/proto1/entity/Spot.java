package com.foxhunt.proto1.entity;

import com.google.android.maps.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 09.03.12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public class Spot
{
    private int id;
    private double latitude;
    private double longitude;
    private String name;
    private int type;
    private double radius;

    public Spot()
    {

    }

    public Spot(int id, double latitude, double longitude, int type, String name, double radius)
    {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.name = name;
        this.radius = radius;
    }



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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
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
        dos.writeDouble(radius);
        dos.close();
        return str.toByteArray();
    }

    public static Spot Deserialize(DataInputStream stream) throws IOException
    {
        Spot res = new Spot();

        res.id = stream.readInt();
        res.latitude = stream.readDouble();
        res.longitude = stream.readDouble();
        res.name = stream.readUTF();
        res.type = stream.readInt();
        res.radius = stream.readDouble();

        return res;
    }

    public GeoPoint getGeoPoint()
    {
        return new GeoPoint((int) (latitude*1e6),  (int) (longitude * 1e6));
    }

}


