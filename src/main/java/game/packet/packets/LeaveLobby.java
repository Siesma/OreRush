package game.packet.packets;

import game.client.InputStreamThread;
import game.client.LobbyInClient;
import game.gui.Player;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerConstants;

public class LeaveLobby extends AbstractPacket {

  public LeaveLobby() {
    super("", new String[]{"^.*$", // lobby name
                    "^.*$"} // player name
            , "");
  }

  @Override
  public String encodeWithContent(String... content) {
    if (content.length == 0) {
      encode();
    }
    String lobbyName = content[0];
    String playerName = content[1];
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
            this.name +
            (char) ServerConstants.DEFAULT_PACKET_SPACER +
            lobbyName +
            (char) ServerConstants.DEFAULT_PACKET_SPACER +
            playerName +
            (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  @Override
  public String encode() {
    return null;
  }

  @Override
  public void decode(Object parent, String message) {
    if (message.startsWith("LeaveLobby" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
      message = message.replace("LeaveLobby" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
    }
    String lobbyName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[0];
    String clientName = message.split(String.valueOf((char) ServerConstants.DEFAULT_PACKET_SPACER))[1];
    if (parent instanceof ClientThread) {
      ClientThread obj = (ClientThread) parent;
      obj.getServer().removeClientFromLobby(obj, lobbyName);
      for (ClientThread clientThread : Server.getClientThreads()) {
        (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new LeaveLobby()).encodeWithContent(lobbyName, clientName));
      }

    }
    if (parent instanceof InputStreamThread) {
      InputStreamThread obj = (InputStreamThread) parent;
      Player playerToDel = null;
      LobbyInClient lobbyWithDel = null;
      for (LobbyInClient lobbyInClient : obj.getClient().getLobbyData()) {
        if (lobbyInClient.getLobbyName().equals(lobbyName)) {
          for (Player player : lobbyInClient.getPlayerData()) {
            if (player.getNickname().equals(clientName)) {
              lobbyWithDel = lobbyInClient;
              playerToDel = player;
            }
          }
        }
      }
      obj.getClient().getLobbyData().get(obj.getClient().getLobbyData().indexOf(lobbyWithDel)).removePlayer(playerToDel);
    }
  }
}
