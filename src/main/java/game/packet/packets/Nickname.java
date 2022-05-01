package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerConstants;
import javafx.application.Platform;

import java.io.InputStream;
import java.util.Scanner;

/**
 * class representing the NICKNAME packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * This Packet is used by a client to inform the server of the client changing nickname
 * which in turn informs the other clients
 */

public class Nickname extends AbstractPacket {
  public Nickname() {
    super("", new String[]{"^.*$", //old name
            "^.*$" // new name
    }, "");
  }

  /**
   * This function will create a Nickname packet
   * @param content the old name and the new name
   * @return the formatted Nickname packet
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
   * This function will create a Nickname with CLI input
   */
  @Override
  public String encode() {
    System.out.println("What do you want your new name to be?");
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
            this.name +
            (char) ServerConstants.DEFAULT_PACKET_SPACER +
            "old name" +
            (char) ServerConstants.DEFAULT_PACKET_SPACER +
            promptUserForInput(new Scanner(System.in)) +
            (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Decodes the packet
   * @param parent server or client
   *               if server receives the packet the updates the client nickname and
   *               informs the other clients by sending a Nickname packet
   *               if client receives the packet, the GUI is updated
   * @param message contains the lobbyname and clientname
   */
  @Override
  public void decode(Object parent, String message) {

    message = message.replace("Nickname" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
    String oldName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
    message = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];

    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.changePlayerName(message);
      for (ClientThread client : Server.getClientThreads()) {
        (new PacketHandler(this)).pushMessage(client.getOutputStream(), (new Nickname()).encodeWithContent(oldName, obj.getPlayerName()));
      }

    }
    if (parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      if (obj.getClient().getNickname().equals(oldName)) {
        String finalMessage = message;
        Platform.runLater(() ->obj.getClient().nicknameProperty().setValue(finalMessage));
      }
      obj.getClient().changeNicknameOfOtherClient(oldName, message);
      obj.getClient().lastChatMessageProperty().setValue("Server: " + oldName + " has changed their name to "
              + message + ".\n");
    }
  }
}
