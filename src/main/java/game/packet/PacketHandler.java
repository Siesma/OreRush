package game.packet;

public class PacketHandler {

    public static String generateOutputFromInput(String input) {
        PacketType packetType = detectPacketType(input);

        //TODO: Replace default response with important information

        return "OutputGenerated";
    }

    private static PacketType detectPacketType(String input) {
        PacketType packetType = new PacketType();

        //TODO: Replace default response with important information

        return packetType;
    }

    private static String decode(String message) {
        String decodedMessage = "";
        /*
        TODO: Interpret message given the matching of packets
         */
        return decodedMessage;
    }

    private static String encode(String message) {
        String encodedMessage = "";
        /*
        TODO: Push the message to the user and the relevant parts of the program
         */
        return (encodedMessage);
    }

    private static boolean isValidate(String message) {
        /*
        TODO: Check if the message is a valid command based on the protocol
         */
        System.out.println(message);
        return false; //Todo make this actually check
    }

    public static void pushMessage(String encodedMessage) {
        /*
        TODO: Send the already encoded message
         */
        System.out.println(encodedMessage);
    }
}
