/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tile.frontend;

import code.TiledMessage;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import javax.json.JsonObject;

/**
 *
 * @author Testing
 */
class SocketConnectionOut implements Runnable {

    private Socket sock;
    private final ObjectOutputStream outStream;
    private final LinkedBlockingQueue<JsonObject> sharedQueue;

    public SocketConnectionOut(Socket sock, ObjectOutputStream outStream, LinkedBlockingQueue<JsonObject> sharedQueue) {
        this.sock = sock;
        this.outStream = outStream;
        this.sharedQueue = sharedQueue;

    }

    @Override
    public void run() {
        System.out.println("my birth");
        try {
            while (true) {
                send_message();
                Thread.sleep(100);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void send_message() {
      try {
            if (sock != null) {
                

                    JsonObject j = sharedQueue.take();
                    byte type = 0, value = 0, SerialNo = 0;

                    type = (byte) Integer.parseInt(j.getString("type"));

                    value = (byte) Integer.parseInt(j.getString("value"));
                    SerialNo = (byte) Integer.parseInt(j.getString("SerialNo"));
                    // System.out.println("Getting somewhere now");

                    TiledMessage tile = new TiledMessage(SerialNo);
                    if (type == 0) {

                        tile = null;
                    } else if ((type > 0) && (type < 7)) { //setting individual colors
                        byte[] intensity = new byte[6];
                        // System.out.println("Recieving something daily");
                        tile.setMessagetype((byte) type);
                        intensity[type - 1] = value;
                        tile.setIntensity(intensity);

                    } else if (type == 7) {
                        tile.setMessagetype(type);
                        if (value > 0) {
                            tile.setState(true);
                        }
                    } else if (type == 8) {
                        tile.setMessagetype(type);
                        tile.setDimspeed(value);
                    }
                    else if(type==12){
                        tile.setMessagetype(type);
                        tile.setState(true);
                    }
                    // outStream.reset();
                    if (tile != null) {
                        System.out.println(tile);
                        outStream.writeObject(tile);
                        outStream.flush();
                    }
                
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            sock = null;
        }

    }
}
