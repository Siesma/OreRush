package game.packet;

import game.client.Client;

import java.util.Scanner;

/*
This is a helper class to generate Packets.

Either by calling the "createPacketMessage" to promt the user for input to fill out the packet
or by calling the "generatePacket" to generate packets from some information passed by another function.
 */
public class PacketGenerator {
    /*
    This is a helper function that fetches the users next command line input and returns it.
     */
    public static String promptUserForInput() {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    /*
    This function should only be called if the user is trying to generate a packet manually as it will promt the user for input on the command line.

    Generates a packet based on input from the user.
    It first lists all the possible packets and then, based on what packet was chosen, prompts the User to input the content of the packet.

    This should eventually be replaced by a user interface event system.
     */
    public static PacketType createPacketMessageByUserInput(Client client) {
        PacketType newPacket;
        System.out.println("What kind of packet do you want to send?");
        System.out.println("Down follows a list of possible packets.");

        System.out.println("request");
        System.out.println("timeout");
        System.out.println("success");
        System.out.println("awake");
        System.out.println("close");
        System.out.println("update");
        System.out.println("move");
        System.out.println("chat");
        System.out.println("nickname");
        System.out.println("settings");

        while (true) {
            String entered = promptUserForInput();
            try {
                newPacket = generateNewPacket(entered);
                if (newPacket.type.equals("pchat")) {
                    System.out.println("Please type your message here:");
                    newPacket.content[1] = promptUserForInput();
                }
                if (newPacket.type.equals("nickn")) {
                    System.out.println("Please type your new nickname here:");
                    newPacket.content[1] = promptUserForInput();
                }
                if (newPacket.type.equals("close")) {
                    client.shutDownClient(client);   // TODO: find better place than packet generator (wtf)
                }
                //newPacket.printPacketOnCommandLine();

                break;
            } catch (Exception e) {
                System.out.println("No packet exists that is named \"" + entered + "\", try again!");
            }
        }
        //System.out.println("Please enter the needed information to fulfill the " + selected.name() + "-Packet.");
        return newPacket;
    }

    /*
    This is a helper function that should be called when a brand-new packet is to be generated.
     */
    public static PacketType generateNewPacket(String type) throws Exception {
        return generatePacket(type, null);
    }

    /*
    This function will generate a Packet.
    It will first figure out the type of packet and then call a function to fill it's content.

    > If the content is == null it will generate a brand-new packet and fill the packets content
    with the appropriate infromation.

    > If the content is != null, it will fill the packet with the information stored in "content".
    This is the case if a packet is to be generated from a decoded message.
     */
    public static PacketType generatePacket(String type, Object[] content) throws Exception {
        PacketType newPacket = new PacketType();
        switch (type) {
            case "request":
            case "reqst":
                newPacket.type = "reqst";
                getRequestPacketContent(newPacket, content);
                return newPacket;
            case "timeout":
            case "timeo":
                newPacket.type = "timeo";
                getTimeoutPacketContent(newPacket, content);
                return newPacket;
            case "success":
            case "succs":
                newPacket.type = "succs";
                getSuccessPacketContent(newPacket, content);
                return newPacket;
            case "awake":
                newPacket.type = "awake";
                getAwakePacketContent(newPacket, content);
                return newPacket;
            case "close":
                newPacket.type = "close";
                getClosePacketContent(newPacket, content);
                return newPacket;
            case "update":
            case "updte":
                newPacket.type = "updte";
                getUpdatePacketContent(newPacket, content);
                return newPacket;
            case "move":
            case "pmove":
                newPacket.type = "pmove";
                getMovePacketContent(newPacket, content);
                return newPacket;
            case "chat":
            case "pchat":
                newPacket.type = "pchat";
                getChatPacketContent(newPacket, content);
                return newPacket;
            case "nickname":
            case "nickn":
                newPacket.type = "nickn";
                getNicknamePacketContent(newPacket, content);
                return newPacket;
            case "settings":
            case "settn":
                newPacket.type = "settn";
                getSettingsPacketContent(newPacket, content);
                return newPacket;
            default:
                throw new Exception();
        }
    }

    /*
    This function is used to generate a Request-Packet
     */
    private static void getRequestPacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = System.currentTimeMillis();
            newPackage.content[1] = 10; //TODO: Make this actually save the IP of the user
        } else {
            newPackage.content[0] = content[0];
            newPackage.content[1] = content[1];
        }
    }

    /*
    This function is used to generate a Timeout-Packet
     */
    private static void getTimeoutPacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = 20; //TODO: Make this actually save the IP of the server
        } else {
            newPackage.content[0] = content[0];
        }
    }

    /*
    This function is used to generate a Timeout-Packet
     */
    private static void getSuccessPacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = 420; //TODO: Make this actually generate a unique client key
        } else {
            newPackage.content[0] = content[0];
        }
    }

    /*
    This function is used to generate a Awake-Packet
     */
    private static void getAwakePacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = System.currentTimeMillis();
        } else {
            newPackage.content[0] = content[0];
        }
    }

    /*
    This function is used to generate a Close-Packet
     */
    private static void getClosePacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = System.currentTimeMillis();
            newPackage.content[1] = 1; //TODO: Make this use the player key
        } else {
            newPackage.content[0] = content[0];
            newPackage.content[1] = content[1];
        }
    }

    /*
    This function is used to generate a Update-Packet
     */
    private static void getUpdatePacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = "[[This is a map]]"; //TODO: Make this send the map data
            newPackage.content[1] = "[[These are the equipmentCooldowns]]"; //TODO: Make this use the equipment cooldowns
            newPackage.content[2] = "[[These are the robots]]"; //TODO: Make this send the robots
            newPackage.content[3] = "[[These are the highscores]]"; //TODO: Make this send the current score
        } else {
            newPackage.content[0] = content[0];
            newPackage.content[1] = content[1];
            newPackage.content[2] = content[2];
            newPackage.content[3] = content[3];
        }
    }

    /*
    This function is used to generate a Move-Packet
     */
    private static void getMovePacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = 1; //TODO: Make this use the player key
            newPackage.content[1] = "[[This is a move: (1/6)->(2/6)]]"; //TODO: Make this use actual moves
        } else {
            newPackage.content[0] = content[0];
            newPackage.content[1] = content[1];
        }
    }

    /*
    This function is used to generate a Chat-Packet by asking the user to type
    their message and saving the player key and message to newPackage
     */
    private static void getChatPacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = 1; //TODO: Make this use the player key
            newPackage.content[1] = null;
        } else {
            newPackage.content[0] = content[0];
            newPackage.content[1] = content[1];
        }
    }

    /*
    This function asks the user to type a new nickname and saves the player key and nickname to newPackage
     */
    private static void getNicknamePacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = 1; //TODO: Make this use the player key
            newPackage.content[1] = null;
        } else {
            newPackage.content[0] = content[0];
            newPackage.content[1] = content[1];
        }
    }

    /*
    This function is used to generate a Settings-Packet
     */
    private static void getSettingsPacketContent(PacketType newPackage, Object[] content) {
        if (content == null) {
            newPackage.content[0] = "[[This is all the server settings]]"; //TODO: Make this use the server settings
        } else {
            newPackage.content[0] = content[0];
        }
    }
}
