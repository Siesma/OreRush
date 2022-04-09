package game.client;

import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.packet.packets.Success;
import game.server.ServerConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamThread implements Runnable {
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
                builder.setLength(0);

                //This part here prints out what the server received. This is here just for bug fixing and manual validation.
                try {
                    AbstractPacket receivedPacket = AbstractPacket.getPacketByMessage(message);
                    if (receivedPacket == null) {
                        System.out.println("The received packet contains garbage.");
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

    public Client getClient () {
        return client;
    }

    private void confirmPingFromServer() {
        try {
            (new PacketHandler(this)).pushMessage(out, (new Success()).encode());
        } catch (Exception e) {
            System.out.println("Client-server connection lost");
        }
    }
}
