package game.packet;

public class PacketHandler {
    private static String generateOutputFromInput(String input) {
        PacketType packetType = interpretMessage(input);
        /*
        TODO: Replace default response with important information
         */
        return packetType.getResponse();
    }

    private static void detectPacketType(String input) {
        PacketType packetType = interpretMessage(input);
        /*
        TODO: Replace default response with important information
         */
        return packetType.getResponse();
    }

    private static String decode(String message) {
        /*
        TODO: Interpret message given the matching of packets
         */
        return PacketType.awake;
    }

    private static String encode(String message) {
        /*
        TODO: Push the message to the user and the relevant parts of the program
         */
        System.out.println(message);
    }

    private static boolean isValidate(String message) {
        /*
        TODO: Check if the message is a valid command based on the protocol
         */
        System.out.println(message);
    }
}
