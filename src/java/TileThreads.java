import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
public class TileThreads implements Runnable {

    boolean scanOrPing = false;
    private final ArrayBlockingQueue<TiledMessage> queueRev;
    private final ArrayBlockingQueue<TiledMessage> queueFwd;
    SocketConnection sock = null;

    
    private final byte SerialNo;
    private final InetAddress ipAddress;
    private int failcount = 0;

    TileThreads( ArrayBlockingQueue<TiledMessage> queueRev,
            ArrayBlockingQueue<TiledMessage> queueFwd,
             byte SerialNo, InetAddress ipAddress) {
        this.queueFwd = queueFwd;
        this.queueRev = queueRev;
        
        this.SerialNo = SerialNo;
        this.ipAddress = ipAddress;
        
        System.out.println("Spawned a new thread with Serial No" + SerialNo
                + " Ip " + ipAddress.toString());
    }

  

    private void destroy() {

        TiledMessage tile = new TiledMessage(SerialNo);
        tile.messagetype = 11; // death message being added to the queue..
        queueRev.add(tile);
        System.out.println("Deleting some  index Interrupting myself yo.");
        Thread.currentThread().interrupt();
        }

    @Override
    public void run() {
        int count = 0;
        while (true) {
            /*if(sock==null)
             {
             create_connection();
             try {
             Thread.sleep(500);
             } catch (InterruptedException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             }
             }*/

            if (count == 5) {
                count = 0;
                scan_doa();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                get_status();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            if (failcount == 3) {
                destroy();
                return;
            }

            sendrcv_message();
            count++;

        }
    }

    private void get_status() {
        // TODO Auto-generated method stub
        create_connection();
        byte info[] = new byte[]{Protocol.START, 0x04, Protocol.API_ID_1, SerialNo, Protocol.INFO, 0x00};
        info[5] = Checksum.generateCheckSum(info, 6);
        try {
            if ((sock != null) && (sock.isConnected() == 1)) {
                //System.out.println("Am I even here yet..");
                byte message[] = sock.snd_recv(info);
                if (message != null) {
                    //System.out.println("Message length is "+message.length);
                    //System.out.println("Serial no in message is"+message[5]);
                    MessageDecoder msgd = new MessageDecoder(message);
                    if (msgd.message_decode() == 1) {
                        System.out.println("SerialNO is " + msgd.getTile().getSerialNo());
                    }
                    queueRev.add(msgd.getTile());
                }
            } else {
                //create_connection();
                System.out.println("Why does ping succed then");
            }
        } catch (IllegalStateException e) {
            queueRev.clear();
            System.out.println("Queue full no listener so deleitng the contents");
            //e.printStackTrace();

        } catch (Exception e) {
            System.out.println("Sending recieving status problem too now whatf");
            e.printStackTrace();

        }

    }

    public void create_connection() {

        //	System.out.println("My Ip is" + ipAddress.toString());
        // TODO Auto-generated method stub
        try {
            sock = new SocketConnection(ipAddress, 8080);
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("Tile Thread to arduino connection problem");
            sock = null;
        }
    }

    public void scan_doa() {
        create_connection();
        byte[] message = new byte[]{0x7E, 0x04, 0x00, 0x00, 0x00, 0x7A};

        try {
            if ((sock != null) && (sock.isConnected() == 1)) {
                sock.snd_recv(message);
                //System.out.println(sock.snd_recv(message));
                System.out.println("Talking success");
                if (failcount != 0) {
                    failcount--;
                }

            } else {
                System.out.println("PING Connection fail yo");
                failcount++;

            }
            sock = null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("TIle scanning error");
        }

    }

    /*
     * try { while(sock.isConnected()==1) {
     * 
     * try { TiledMessage message=queue.take(); deploy_message(message,sock); }
     * catch (InterruptedException e) {
     * System.out.println("Taking message from queue didn't work.");
     * e.printStackTrace(); } } } catch (IOException e) { // TODO Auto-generated
     * catch block e.printStackTrace(); }
     */
    private void sendrcv_message() {
        create_connection();
        if (!queueFwd.isEmpty()) {
            System.out.println("A message addressed to me yo///");
            TiledMessage tile = null;
            try {
                tile = queueFwd.take();
                System.out.println("message taken has serial no" + tile.getSerialNo() + "message type" + tile.getMessagetype());
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            

            MessageGenerator msg = new MessageGenerator(tile);

            byte message[] = msg.getMessage();

            // TODO Auto-generated method stub
            try {
                if ((sock != null) && (sock.isConnected() == 1)) {
                    byte[] msgrcv = sock.snd_recv(message);

                    System.out.println("Recieved message is " + msgrcv);
                    if (msgrcv != null) {

                        MessageDecoder msgd = new MessageDecoder(msgrcv);
                        if (msgd.message_decode() == 1) {
                            TiledMessage tile1 = msgd.getTile();
                            //only send back the status messages...just ignore the rest for time being lol
                            if (tile1.getMessagetype() == 10) {
                                queueRev.add(tile1);
                            }
                        }
                        System.out.println("Send success..recieved a legit message back");
                    }
                } else {
                    System.out.println("sending  Connection fail yo");

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("TIle sending error");
                sock = null;
            }

        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        sock = null;

    }

}
