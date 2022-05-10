package game.packet.packets;

import game.client.InputStreamThread;
import game.client.LobbyInClient;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Lobby;
import game.server.Server;
import game.server.ServerConstants;

/**
 * class representing the NICKNAME packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * This Packet is used by a client to inform the server that a game should start in specific lobby
 * which in turn informs the other clients in the lobby and sends them the gamemap
 */
public class StartGame extends AbstractPacket {
  public StartGame() {
    super("", new String[]{"^.*$", //lobby
    }, "");
  }

  /**
   * This function will create a Start Game packet
   *
   * @param content the lobby name where the game shall start
   * @return the formatted StartGame packet
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
      (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * This function is not used
   */
  @Override
  public String encode() {
    return null;
  }

  // TODO: add player turn etc...

  /**
   * Decodes the packet
   *
   * @param parent  server or client
   *                if server receives the packet it starts the game and sends the map to clients in the lobby
   *                if client receives the packet, the game status in the startmenu lobby list is updated
   * @param message contains the lobbyname and clientname
   */
  @Override
  public void decode(Object parent, String message) {
    message = message.replace("StartGame" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");

    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      for (Lobby lobby : obj.getServer().getLobbyArrayList()) {
        if (lobby.getLobbyName().equals(message)) {
          for (ClientThread clientThread : lobby.getListOfClients()) {
            (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(),
              (new Update()).encodeWithContent(lobby.getGameMap().cellStrings()));
            // TODO .getIndividualGameMapForPlayer(clientThread.getPlayerName())));
            (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(),
              (new UpdateTurn()).encodeWithContent(lobby.getListOfClients().get(lobby.turnOfPlayer()).getPlayerName(), String.valueOf(lobby.getTurnCounter())));
          }

        }

      }
      for (ClientThread clientThread : Server.getClientThreads()) {
        (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new StartGame()).encodeWithContent(message));
      }

    }
    if (parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      for (LobbyInClient lobbyInClient : obj.getClient().getLobbyData()) {
        if (lobbyInClient.getLobbyName().equals(message)) {
          lobbyInClient.setStatus("in game");
        }
      }

    }
  }
}
