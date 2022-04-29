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

public class InputStreamThread implements Runnable {
    public static final Logger logger = LogManager.getLogger(Server.class);
    private final Client client;
    private final InputStream in;
    private final OutputStream out;

    public InputStreamThread(Client client) {
        this.client = client;
        this.in = client.getInputStream();
        this.out = client.getOutputStream();
    }

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
                System.out.println("client received: " + AbstractPacket.splitMessageBySpacer(message)[0]);
                builder.setLength(0);

                //This part here prints out what the server received. This is here just for bug fixing and manual validation.
                try {
                    AbstractPacket receivedPacket = AbstractPacket.getPacketByMessage(message);
                    if (receivedPacket == null) {
                        logger.info("The received packet contains garbage.");
                        break;
                    }
//                    generateAppropriateReaction(receivedPacket);
                    receivedPacket.decode(this, message);
                } catch (Exception e) {
                    e.printStackTrace();
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
