package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.ServerConstants;

import java.util.Scanner;

public class Chat extends AbstractPacket {


  public Chat() {
    super("", new String[]{"^.*$"}, "");
  }

  @Override
  public String encodeWithContent(String... content) {
    if (content.length == 0) {
      encode();
    }
//    if(content.length != 1) {
//
//    }
    String msg = content[0];
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
            this.name +
            (char) ServerConstants.DEFAULT_PACKET_SPACER +
            msg +
            (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  @Override
  public String encode() {
    System.out.println("Chat-message:");
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
            this.name +
            (char) ServerConstants.DEFAULT_PACKET_SPACER +
            AbstractPacket.promptUserForInput(new Scanner(System.in)) +
            (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  @Override
  public void decode(Object parent, String message) {
    if(parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.pushChatMessageToAllClients(message);
    }
  }
}
