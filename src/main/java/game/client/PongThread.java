package game.client;


import game.packet.PacketHandler;
import game.packet.packets.Awake;
import game.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;

/**
 * The Pong thread is responsible for sending pong packets to the server and
 * shutdown the client if the connection is lost
 */
public class PongThread implements Runnable {

    /**
     * The Client associated with this pong thread
     */
    private final Client client;

    /**
     * Log4j logger that allows great, clear and useful logging of information and errors
     * instead of the ugly commandline prints
     */
    public static final Logger logger = LogManager.getLogger(PongThread.class);

    /**
     * Constructor used in Client.
     * @param client the client, to be associated with the Pong Thread
     */
    public PongThread(Client client) {
        this.client = client;
    }

    /**
     * Starts the thread and begins sending pongs the server.
     */
    public void run() {
        logger.info("Pong thread started");
        while (true) {
            sendPong();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            if (!client.isPongReceived()) {
                logger.error("No response from the server, The client will shutdown shortly.");
                client.shutDownClient(); // TODO: try and reconnect to server
            } else {
                client.setPongReceived(false);
            }
            try {
                Thread.sleep(12000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
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
            logger.fatal("Client-server connection lost", e.getMessage());
        }
    }
}
