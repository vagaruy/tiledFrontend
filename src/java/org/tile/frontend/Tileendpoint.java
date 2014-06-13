/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tile.frontend;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Testing
 */
@ServerEndpoint(value = "/tileendpoint", encoders = {MessageEncoder.class}, decoders = {MessageDecoder.class})
public class Tileendpoint {

    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    private static int port = 6969;
    private static String host;
    private static SocketConnection socket=null;
    private static Thread t=null;
    private static LinkedBlockingQueue<JsonObject> sharedQueue=null;
    private static LinkedBlockingQueue<JsonObject> sharedQueueRev=null;
     private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    public Tileendpoint() throws UnknownHostException {
        
             
    }
   

    @OnMessage
    public void broadcastFigure(TileMessage tile, Session session) {
        System.out.println(tile.getJson());
       sharedQueue.add(tile.getJson());
       
      
       
       while(!sharedQueueRev.isEmpty())
       {
           
       JsonObject j=sharedQueueRev.remove();
       if(j instanceof JsonObject){
           for (Session peer : peers) {
               try {
               //     System.out.println("What the fuck asshole3");
                    peer.getBasicRemote().sendObject(j);
                } catch (Exception ex) {
                  //  ex.printStackTrace();
                    
                }
       }
           }
       }      
        for (Session peer : peers) {
            if (!peer.equals(session)) {
                try {
                    peer.getBasicRemote().sendObject(tile);
                } catch (Exception ex) {
                  ex.printStackTrace();
                    
                }
            }
        }
     
    }
    @OnOpen
    public void onOpen(Session peer) {
        try {
            peers.add(peer);
        } catch (Exception ex) {
          //  ex.printStackTrace();
        }
        
        if(t==null)
        {
            try {
            host = InetAddress.getLocalHost().getHostName();
            sharedQueue=new LinkedBlockingQueue();
            sharedQueueRev=new LinkedBlockingQueue();
           socket=new SocketConnection(host,port,sharedQueue,sharedQueueRev);
           t=new Thread(socket);
           t.start();
            } catch (UnknownHostException ex) {
                Logger.getLogger(Tileendpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
 }

    @OnClose
    public void onClose(Session peer) {
        peers.remove(peer);
    }

    @OnError
    public void OnError(Session s, Throwable t) {
    }

    
}
