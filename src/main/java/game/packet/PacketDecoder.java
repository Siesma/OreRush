package game.packet;

import game.server.ServerConstants;

/*
This is a helper class to decode the string sent between clients and servers
 */
public class PacketDecoder {

    /*

    and stores it in the "content" array which is returned.
     */

    /**
     * This function will chop up the encoded string into its parts.
     * It will then, based on the packet type, pass the encoded information into the data types they represent
     * @param type of the packet
     * @param encodedMessage contains arguments of the packet which needs to be split in an array
     * @return content array containing arguments relevant to the packet type
     * @throws Exception
     */
    protected static Object[] decodePacketContent(String type, String encodedMessage) throws Exception {
        Object[] content = new Object[100];
        String[] encodedMessageArray = splitMessageBySpacer(encodedMessage);
        switch (type) {
            case "reqst":
                decodeRequestPacketContent(content, encodedMessageArray);
                return content;
            case "timeo":
                decodeTimeoutPacketContent(content, encodedMessageArray);
                return content;
            case "succs":
                decodeSuccessPacketContent(content, encodedMessageArray);
                return content;
            case "awake":
                decodeAwakePacketContent(content, encodedMessageArray);
                return content;
            case "close":
                decodeClosePacketContent(content, encodedMessageArray);
                return content;
            case "updte":
                decodeUpdatePacketContent(content, encodedMessageArray);
                return content;
            case "pmove":
                decodeMovePacketContent(content, encodedMessageArray);
                return content;
            case "pchat":
                decodeChatPacketContent(content, encodedMessageArray);
                return content;
            case "nickn":
                decodeNicknamePacketContent(content, encodedMessageArray);
                return content;
            case "settn":
                decodeSettingsPacketContent(content, encodedMessageArray);
                return content;
            default:
                throw new Exception();
        }
    }


    // All the below function take the encoded message array and pass it based on what packet was sent.

    private static void decodeRequestPacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = Long.parseLong(encodedMessageArray[0]);
        content[1] = Integer.parseInt(encodedMessageArray[1]);
    }

    private static void decodeTimeoutPacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = encodedMessageArray[0];
    }

    private static void decodeSuccessPacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = Integer.parseInt(encodedMessageArray[0]);
    }

    private static void decodeAwakePacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = Long.parseLong(encodedMessageArray[0]);
    }

    private static void decodeClosePacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = Long.parseLong(encodedMessageArray[0]);
        content[1] = Integer.parseInt(encodedMessageArray[1]);
    }

    private static void decodeUpdatePacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = encodedMessageArray[0]; //TODO: Currently this is a placeholder string. Once a map Class is created, call a function that parses the string into a map
        content[1] = encodedMessageArray[1]; //TODO: Currently this is a placeholder string. Once a cooldown Class is created, call a function that parses the string into a cooldown
        content[2] = encodedMessageArray[2]; //TODO: Currently this is a placeholder string. Once a robot Class is created, call a function that parses the string into a robot
        content[3] = encodedMessageArray[3]; //TODO: Currently this is a placeholder string. Once a highscore Class is created, call a function that parses the string into a highscore
    }

    private static void decodeMovePacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = Integer.parseInt(encodedMessageArray[0]);
        content[1] = encodedMessageArray[1]; //TODO: Currently this is a placeholder string. Once a moves Class is created, call a function that parses the string into a moves
    }

    private static void decodeChatPacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = Integer.parseInt(encodedMessageArray[0]);
        content[1] = encodedMessageArray[1];
    }

    private static void decodeNicknamePacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = Integer.parseInt(encodedMessageArray[0]);
        content[1] = encodedMessageArray[1];
    }

    private static void decodeSettingsPacketContent(Object[] content, String[] encodedMessageArray) {
        content[0] = encodedMessageArray[0]; //TODO: Currently this is a placeholder string. Once a settings Class is created, call a function that parses the string into a settings
    }

    private static String[] splitMessageBySpacer(String message) {
        return message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER));
    }
}
