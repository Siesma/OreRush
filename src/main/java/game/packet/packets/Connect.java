package game.packet.packets;

import game.client.InputStreamThread;
import game.packet.AbstractPacket;
import game.packet.PacketHandler;
import game.server.ClientThread;
import game.server.Lobby;
import game.server.Server;
import game.server.ServerConstants;
/**
 * class representing the Connect packet.
 * Implementation of the AbstractPacket.
 * Contains a constructor and methods to encode and decode the packet.
 * The Connect packet is the first packet sent by a client, it informs the server of the new client
 * and sends back all information needed by the client such as the other players connected and
 * lobbies that were already created
 */
public class Connect extends AbstractPacket {
  /**
   * Constructor for the Connect packet
   */
  public Connect() {
    super("The init packet consists of one user input part. $\"Name\"", new String[]{
      ".*", //Name
    }, "A default initialization response!");
  }

  /**
   * Creates the Connect packet with the name of the client that is connecting.
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
   * Placeholder in case the packet will have non-normal use cases.
   */
  @Override
  public String encode() {
    return null;
  }

  /**
   * If the packet is received by the server, the connecting client is informed of the existing lobbys and players;
   * and the other players are informed of the newly connected client by sending them a Connect packet.
   * If a client receives a connect packet, the new client is added to the observable client list.
   */
  @Override
  public void decode(Object parent, String message) {
    if (parent instanceof ClientThread) {
      if (message.startsWith("Connect" + (char) ServerConstants.DEFAULT_PACKET_SPACER)) {
        message = message.replace("Connect" + (char) ServerConstants.DEFAULT_PACKET_SPACER, "");
      }
      ClientThread obj = (ClientThread) parent;
      obj.changePlayerName(message);

      if (!message.equals(obj.getPlayerName())) {
        (new PacketHandler(this)).pushMessage(obj.getOutputStream(),(new InitNickname().encodeWithContent(obj.getPlayerName())));
      }


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
        (new PacketHandler(this)).pushMessage(clientThread.getOutputStream(), (new Connect()).encodeWithContent(obj.getPlayerName()));
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
