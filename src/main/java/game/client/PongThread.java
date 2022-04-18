package game.client;


import game.packet.PacketHandler;
import game.packet.packets.Awake;
import game.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;


public class PongThread implements Runnable {

    private final Client client;
    public static final Logger logger = LogManager.getLogger(Server.class);

    public PongThread(Client client) {
        this.client = client;
    }
    public void run() {
        System.out.println("Pong thread started");
        while (true) {
            sendPong();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!client.isPongReceived()) {
                logger.error("No response from the server, The client will shutdown shortly.");
                client.shutDownClient(); // TODO: try and reconnect to server
            } else {
                client.setPongReceived(false);
            }
            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends an awake packet to the server to ensure connection.
     */
    private void sendPong() {
        try {
            (new PacketHandler(this)).pushMessage(client.getOutputStream(), (new Awake().encode()));
        } catch (Exception e) {
            logger.fatal("Client-server connection lost");
        }
    }
}
