package game.packet;

import java.util.Scanner;

public class PacketGenerator {
    public static String promptUserForInput()
    {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    /*
    Generates a packet based on input from the user.
    It first lists all the possible packets and then, based on what packet was chosen, prompts the User to input the content of the packet.
     */
    public static String createPacketMessage() {
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
        System.out.println("settings");

        while (true) {
            String entered = promptUserForInput();
            try {
                PacketType newPacket = generatePacket(entered);
                System.out.println("The packet you're sending is");
                int i = 0;
                while (newPacket.content[i] != null){
                    System.out.println(newPacket.content[i]);
                    i++;
                }

                break;
            } catch (Exception e) {
                System.out.println("No packet exists thats named \"" + entered + "\", try again!");
            }
        }
        //System.out.println("Please enter the needed information to fulfill the " + selected.name() + "-Packet.");
        return "Packet generated";
    }

    /*
    This function will generate a Packet based on the user input.
    It will first figure out the type of packet and then call a function to fill it's content.
     */
    private static PacketType generatePacket (String entered) throws Exception {
        PacketType newPacket = new PacketType();
        switch (entered) {
            case "request":
                newPacket.type = "reqst";
                getRequestPacketContent(newPacket);
                return newPacket;
            case "timeout":
                newPacket.type = "timeo";
                return newPacket;
            case "success":
                newPacket.type = "succs";
                return newPacket;
            case "awake":
                newPacket.type = "awake";
                return newPacket;
            case "close":
                newPacket.type = "close";
                return newPacket;
            case "update":
                newPacket.type = "updte";
                return newPacket;
            case "move":
                newPacket.type = "pmove";
                return newPacket;
            case "chat":
                newPacket.type = "pchat";
                getChatPacketContent(newPacket);
                return newPacket;
            case "settings":
                newPacket.type = "settn";
            default:
                throw new Exception();
        }
    }

    /*
    This function is used to generate a Request-Packet
     */
    private static void getRequestPacketContent (PacketType newChatPackage) {
        newChatPackage.content[0] = System.currentTimeMillis();
        newChatPackage.content[1] = 10; //TODO: Make this actually save the IP of the user
    }

    /*
    This function asks the user to type their message and saves the player key and message to newChatPackage
     */
    private static void getChatPacketContent (PacketType newChatPackage) {
        newChatPackage.content[0] = 1; //TODO: Make this use the player key

        System.out.println("Enter your chat message:");
        String entered = promptUserForInput();
        newChatPackage.content[1] = entered;
        System.out.println("Message saved");
    }
}
