/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tile.frontend;

import code.TiledMessage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Testing
 */
class SocketConnection implements Runnable {
    
    private final LinkedBlockingQueue<JsonObject> sharedQueue;
    private final LinkedBlockingQueue<JsonObject> sharedQueueRev;
    private final String host;
    private final int port;
    Socket sock = null;
    ObjectOutputStream outStream = null;
    ObjectInputStream inStream = null;
    TiledMessage tile;
    
    SocketConnection(String host, int port, LinkedBlockingQueue<JsonObject> sharedQueue, LinkedBlockingQueue<JsonObject> sharedQueueRev) {
        this.host = host;
        this.port = port;
        this.sharedQueue = sharedQueue;
        this.sharedQueueRev = sharedQueueRev;
    }
    
    @Override
    public void run() {
        System.out.println("Thread created");
        create_connection();
        SocketConnectionInc inc=new SocketConnectionInc(sock,inStream,sharedQueueRev);
        SocketConnectionOut out=new SocketConnectionOut(sock,outStream,sharedQueue);
        Thread incThread=new Thread(inc);
        Thread outThread=new Thread(out);
        incThread.start();
        outThread.start();
        while (true) {
           if(sock==null)
           {
               create_connection();
               
           }
           else{
               
           
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("Sleep has been interrupted");
            }
        }
    }}
    
    private void create_connection() {
        try {
            sock = new Socket(host, port);
            inStream = new ObjectInputStream(sock.getInputStream());
            outStream = new ObjectOutputStream(sock.getOutputStream());
           // outStream.flush();
           

            //  System.out.println(sock.getInetAddress());

        } catch (Exception ex) {
             ex.printStackTrace();
            sock = null;
            
        }
    }
    
    
    
    
}
