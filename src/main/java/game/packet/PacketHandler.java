package game.packet;

import game.client.Client;
import game.helper.FileHelper;
import game.server.ServerConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Scanner;

public class PacketHandler {

    public static final Logger logger = LogManager.getLogger(Client.class);
    private final Object parent;

    public PacketHandler(Object parent) {
        this.parent = parent;
    }

    /**
     * A function that prompts the user to specify which packet is meant to be sent next.
     * All the possible packets are gathered using the files that are in the respective folder.
     * This folder is "packet.packets"
     *
     * @return the encoded packet as a string
     */
    public String createPacketMessage() {
        System.out.println("Please tell which packet you want to send");
        System.out.println("A list of possible packets are:");
        // TODO: Figure out whether we want to continue having this or not because in its current state it is not working.
        for (String s : (Objects.requireNonNull(new File(System.getProperty("user.dir") + "/src/main/java/game/packet/packets").list()))) {
            System.out.println("\t->" + s.split(".java")[0]);
        }
        Scanner sc = new Scanner(System.in);
        String entered = AbstractPacket.promptUserForInput(sc);
        AbstractPacket packet;
        packet = AbstractPacket.getPacketByName(entered);
        if (packet == null) {
            return "";
        }
        return packet.encode();
    }


    /**
     * A function that pushes a given input string and its according values to the server or client.
     * If a packet is attempted to be created, but it is not succeeding it will return nothing.
     *
     * @param out     the outputStream the message should be pushed through
     * @param message the message that should be pushed
     */
    public void pushMessage(OutputStream out, String message) {

        if (message.equals("")) {
            return;
        }
        AbstractPacket packet;
        packet = AbstractPacket.getPacketByName(AbstractPacket.splitMessageBySpacer(message, String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0]);
        if (packet == null) {
            return;
        }
        if (!packet.validate(message)) {
            System.out.println("Tried to send: " + message);
            System.out.println("The given packet contained garbage");
            return;
        }
        try {
            out.write(message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
