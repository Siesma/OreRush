package game.packet;

import game.client.Client;
import game.server.Server;
import game.server.ServerConstants;

public class PacketHandler {

  public static String generateOutputFromInput(String input) {
    PacketType packetType = detectPacketType(input);

    //TODO: Replace default response with important information

    return "OutputGenerated";
  }

  private static PacketType detectPacketType(String input) {
    PacketType packetType = new PacketType();
    switch (input.substring(0, 5)) {
      case "reqst":
        packetType.type = "request";
        break;
      case "timeo":
        packetType.type = "timeout";
        break;
      case "succs":
        packetType.type = "success";
        break;
      case "awake":
        packetType.type = "awake";
        break;
      case "close":
        packetType.type = "close";
        break;
      case "updte":
        packetType.type = "update";
        break;
      case "pmove":
        packetType.type = "move";
        break;
      case "pchat":
        packetType.type = "chat";
        break;
      case "settn":
        packetType.type = "settings";
      default:
        return null;
    }
    return packetType;
  }

  private static PacketType decode(String message) throws Exception {
    PacketType type = PacketGenerator.generatePacket(message.substring(0, 5), PacketDecoder.decodePacketContent(message.substring(0, 5), message.substring(6)));
    return type;
  }

  private static String encode(Client client, Server server, PacketType message) throws Exception {
    StringBuilder out = new StringBuilder();
    out.append(message.type);
    out.append((char) ServerConstants.DEFAULT_PACKET_SPACER);
    for (Object o : message.content) {
      if (o == null) {
        continue;
      }
      out.append(o.toString());
      out.append((char) ServerConstants.DEFAULT_PACKET_SPACER);
    }
    return out.toString();
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
