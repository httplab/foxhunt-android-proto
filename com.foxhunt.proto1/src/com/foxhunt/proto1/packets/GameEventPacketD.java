package com.foxhunt.proto1.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Nu-hin
 * Date: 09.03.12
 * Time: 22:54
 * To change this template use File | Settings | File Templates.
 */
public class GameEventPacketD extends FoxhuntPacket
{
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GameEventPacketD(String message)
    {
        this.message = message;
    }
    
    @Override
    protected void Serialize(DataOutputStream stream) throws IOException {
        stream.writeUTF(this.message);
    }

    public GameEventPacketD(DataInputStream stream)
    {
        try
        {
            this.message = stream.readUTF();
        }
        catch (Exception ex)
        {
            this.message="";
        }
    }


    @Override
    public int getPackageType() {
        return GAME_EVENT_PACKET_D;
    }

    @Override
    public String toString() {
        return super.toString();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
