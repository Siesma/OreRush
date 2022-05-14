package game.client;

import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.packet.packets.Success;
import game.server.Server;
import game.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The InputStreamThread is responsible for recording and processing the packets received on the Inputstream connected to the server.
 * It records the messages and forwards them to the packet handler which will process them further.
 */
public class InputStreamThread implements Runnable {
    /**
     * Log4j logger that allows great, clear and useful logging of information and errors
     * instead of the ugly commandline prints
     */
    public static final Logger logger = LogManager.getLogger(Server.class);
    /**
     * The Client listening on this input steam.
     */
    private final Client client;
    /**
     * Inputstream of this client used to receive packages of the server
     */
    private final InputStream in;
    /**
     * Outputstream of the client used to send packets to the server
     */
    private final OutputStream out;

    /**
     * Constructor of the InputStreamThread class. Used in the constructor of the Client class.
     * @param client the The Client listening on this input steam.
     */
    public InputStreamThread(Client client) {
        this.client = client;
        this.in = client.getInputStream();
        this.out = client.getOutputStream();
    }

    /**
     * Starts the thread and listens to any input.
     */
    public void run() {
        boolean startingToRecordMessage = false;
        StringBuilder builder = new StringBuilder();
        while (true) {
            int cur;
            try {
                cur = in.read();
            } catch (IOException e) {
                cur = -1;
            }
            if (cur == -1) {
                return;
            }

            // This part is executed once the end of the message is reached.
            if (cur == ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE) {
                startingToRecordMessage = false;
                String message = builder.toString();
                logger.info("client received: " + AbstractPacket.splitMessageBySpacer(message)[0]);
                builder.setLength(0);

                //This part here prints out what the server received. This is here just for bug fixing and manual validation.
                try {
                    AbstractPacket receivedPacket = AbstractPacket.getPacketByMessage(message);
                    if (receivedPacket == null) {
                        logger.info("The received packet contains garbage.");
                        break;
                    }
//                    generateAppropriateReaction(receivedPacket);
                    try {
                    receivedPacket.decode(this, message);
                    } catch (Exception e) {
                        logger.fatal("While decoding the message there was a critical error!", e);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

            }

            // This will read the whole message into the builder.
            if (startingToRecordMessage) {
                builder.append((char) cur);
            }


            // This is executed when the server detects the start of a message.
            if (cur == ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE) {
                startingToRecordMessage = true;
            }
        }
    }

    /**
     * Getter for the Client
     * @return the Client associated with this InputStreamThread
     */
    public Client getClient() {
        return client;
    }

    /**
     * Confirms the ping from the server to not stop the connection.
     */
    public void confirmPingFromServer() {
        try {
            (new PacketHandler(this)).pushMessage(out, (new Success()).encode());
        } catch (Exception e) {
            logger.error("Client-server connection lost", e.getMessage());
        }
    }
}
