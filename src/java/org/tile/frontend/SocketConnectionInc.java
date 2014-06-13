/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tile.frontend;

import code.TiledMessage;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Testing
 */
class SocketConnectionInc implements Runnable {

    Socket sock;
    private final ObjectInputStream inStream;
    private final LinkedBlockingQueue<JsonObject> sharedQueueRev;

    public SocketConnectionInc(Socket sock, ObjectInputStream inStream, LinkedBlockingQueue<JsonObject> sharedQueueRev) {
        this.sock = sock;
        this.inStream = inStream;
        this.sharedQueueRev = sharedQueueRev;
    }

    @Override
    public void run() {
        try {
            while (true) {
                recv_message();
                Thread.sleep(100);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private int change_type(byte b)
    {
        if(b>0)
            return b;
        else
            return b+256;
    }
    private void recv_message() {

        try {
            if (sock != null) {
                TiledMessage message = null;
                message = (TiledMessage) inStream.readObject();
                if (message != null) {
                    if (message instanceof TiledMessage) {
                      //  System.out.println("A tile actually recieved with Serial No" + message.getSerialNo());
                        JsonObjectBuilder j = Json.createObjectBuilder();
                        j.add("type", String.valueOf(message.getMessagetype()));
                        if (message.getMessagetype() == 10) {
                            JsonObjectBuilder j1 = Json.createObjectBuilder();
                            JsonObjectBuilder j2 = Json.createObjectBuilder();
                            byte[] intensity = message.getIntensity();
                            
                            j2.add("red", String.valueOf(change_type(intensity[0])));
                            j2.add("green",String.valueOf(change_type(intensity[1])));
                            j2.add("blue", String.valueOf(change_type(intensity[2])));
                            j2.add("amber", String.valueOf(change_type(intensity[3])));
                            j2.add("wwhite", String.valueOf(change_type(intensity[4])));
                            j2.add("cwhite", String.valueOf(change_type(intensity[5])));
                            j1.add("intensity", j2);
                            if (!message.isState()) {
                                j1.add("state", "0");
                            } else {
                                j1.add("state", "1");
                            }
                            j1.add("dimspeed", String.valueOf(message.getDimspeed()));
                            j1.add("doa", "1");
                            j.add("value", j1);
                        } else if (message.getMessagetype() == 7) {
                            if (!message.isState()) {
                                j.add("value", "0");
                            } else {
                                j.add("value", "1");
                            }
                        } else if ((message.getMessagetype() > 0) && (message.getMessagetype() < 7)) {
                            byte intensity[] = message.getIntensity();
                            j.add("value", String.valueOf(intensity[message.getMessagetype() - 1]));
                        } else if (message.getMessagetype() == 8) {
                            j.add("value", String.valueOf(message.getDimspeed()));
                        } else if (message.getMessagetype() == 11) {
                            j.add("value", "0");
                        }
                        j.add("SerialNo", String.valueOf(message.getSerialNo()));
                        JsonObject obj = j.build();
                        sharedQueueRev.add(obj);
                    } else {
                        System.out.println("weird characters read");
                    }
                    // sort(message);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sock = null;
        }
    }
}
