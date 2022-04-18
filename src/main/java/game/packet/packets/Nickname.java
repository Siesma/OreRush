package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerConstants;

import java.io.InputStream;
import java.util.Scanner;

//TODO fix nickanme in Client gui changes should be reflected in client list and in lobbylist

public class Nickname extends AbstractPacket {
  public Nickname() {
    super("", new String[]{"^.*$", //old name
            "^.*$" // new name
    }, "");
  }

  /**
   * This function will create a Nickname where the input is already predetermined.
   */
  @Override
  public String encodeWithContent(String... content) {
    if (content.length == 0) {
      encode();
    }
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      content[0] +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      content[1] +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * This function will create a Nickname where the input is not predetermined.
   */
  @Override
  public String encode() {
    //System.out.println("What do you want your new name to be?");
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
      this.name +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      "old name" +
      (char) ServerConstants.DEFAULT_PACKET_SPACER +
      promptUserForInput(new Scanner(System.in)) +
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Informs the server and the client about the incoming nickname change.
   * Will remove redundant packet-declaration stuff.
   */
  @Override
  public void decode(Object parent, String message) {

    message = message.replace("Nickname" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
    String oldName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
    message = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];

    if (parent instanceof ClientThread ) {
      ClientThread obj = (ClientThread) parent;
      obj.changePlayerName(message);
      for (ClientThread client: Server.getClientThreads()) {
        (new PacketHandler(this)).pushMessage(client.getOutputStream(), (new Nickname()).encodeWithContent(oldName,message));
      }

    }
    if (parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      obj.getClient().changeNicknameOfOtherClient(oldName,message);

      obj.getClient().setLastChatMessage("Server: " + oldName + " has changed their name to "
              + message + ".\n");
    }
  }
}
