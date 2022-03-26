package game.packet;

import game.client.Client;
import game.server.Server;
import game.server.ServerConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

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

  public static PacketType decode(String message) throws Exception{
    return PacketGenerator.generatePacket(message.substring(0, 5), PacketDecoder.decodePacketContent(message.substring(0, 5), message.substring(6)));
  }

  private static String encode(PacketType message){
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

  public static void pushMessage(OutputStream out, PacketType packet) {
    try{
      String encodedMessage = encode(packet);

      out.write(ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE);
      out.write(encodedMessage.getBytes());
      out.write(ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE);
      //System.out.println("This is the encoded message: " + encodedMessage);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
