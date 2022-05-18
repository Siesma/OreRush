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
/**
 * this class is used to validate and push a packet.
 */
public class PacketHandler {

    public static final Logger logger = LogManager.getLogger(PacketHandler.class);
    private final Object parent;

    /**
     * Constructor for the packet handler
     * @param parent defines if the packet is sent by the server or client
     */
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
        //TODO: remove this function
        logger.info("Please tell which packet you want to send");
        logger.info("A list of possible packets are:");
        for (String s : (Objects.requireNonNull(new File(System.getProperty("user.dir") + "/src/main/java/game/packet/packets").list()))) {
            logger.info("\t->" + s.split(".java")[0]);
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
            logger.debug("Tried to send: " + message);
            logger.debug("The given packet contained garbage");
            return;
        }
        try {
            out.write(message.getBytes());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
