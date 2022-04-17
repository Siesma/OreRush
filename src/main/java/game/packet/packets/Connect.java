package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Lobby;
import game.server.Server;
import game.server.ServerConstants;

public class Connect extends AbstractPacket {

  public Connect() {
    super("The init packet consists of one user input part. $\"Name\"", new String[]{
      ".*", //Name
    }, "A default initialization response!");
  }

  /**
   * Placeholder in case the packet will have non-normal use cases.
   */
  @Override
  public String encodeWithContent(String... content) {
    return (char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE +
            this.name +
            (char) ServerConstants.DEFAULT_PACKET_SPACER +
            content[0] +
            (char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE;
  }

  /**
   * Encodes the unfinished Join packet.
   * Does not work properly and may not be used, so far at least.
   */
  @Override
  public String encode() {
    return null;
  }

  /**
   * Placeholder for if the join packet has to be decoded.
   */
  @Override
  public void decode(Object parent, String message) {
    if (parent instanceof ClientThread) {
      if (message.startsWith("Connect" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
        message = message.replace("Connect" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
      }
      ClientThread obj = (ClientThread) parent;
      obj.changePlayerName(message);

      //TODO (seb) send package with other clients to update connectedClient list
      //TODO already existing lobbys

      // informs player of already existing players
      for (ClientThread clientThread:Server.getClientThreads()) {
        if (clientThread != obj) {
          (new PacketHandler(this)).pushMessage(obj.getOutputStream(), (new Connect()).encodeWithContent(clientThread.getPlayerName()));
        }
      }

      // informs of already created lobbys and clients in those lobbys
      for (Lobby lobby:obj.getServer().getLobbyArrayList()) {
        (new PacketHandler(this)).pushMessage(obj.getOutputStream(), (new CreateLobby()).encodeWithContent(lobby.getLobbyName()));
        for (ClientThread clientThread: lobby.getListOfClients()){
          (new PacketHandler(this)).pushMessage(obj.getOutputStream(), (new JoinLobby()).encodeWithContent(lobby.getLobbyName(), clientThread.getPlayerName()));
        }
      }


      // inform clients of new player
      for(ClientThread clientThread : Server.getClientThreads()) {
        (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Connect()).encodeWithContent(message));
      }
    }
    if (parent instanceof InputStreamThread) {
      if (message.startsWith("Connect" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
        message = message.replace("Connect" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
      }
      InputStreamThread obj = (InputStreamThread) parent;
      obj.getClient().setLastChatMessage("Server: " + message + " has joined the server.\n");
      obj.getClient().addClient(message);
    }
  }
}
