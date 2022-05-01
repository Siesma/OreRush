package packet;

import game.client.Client;
import game.client.InputStreamThread;
import game.datastructures.GameMap;
import game.datastructures.Ore;
import game.datastructures.Radar;
import game.datastructures.Robot;
import game.helper.TestHelper;
import game.packet.AbstractPacket;
import game.packet.packets.Chat;
import game.packet.packets.Move;
import game.packet.packets.Update;
import game.server.ClientThread;
import game.server.Lobby;
import game.server.ServerConstants;
import game.server.ServerSettings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DecodeTest {
  @Mock
  private Socket socket;

  @Mock
  private OutputStream myOutputStream;

  @Mock
  private ClientThread clientThread;

  @Mock
  private Lobby connectedLobby;

  @Captor
  private ArgumentCaptor<byte[]> valueCapture;


  @Test
  public void testDecodeMakeActionMove() {
    clientThread = mock(ClientThread.class);
    connectedLobby = mock(Lobby.class);
    ServerSettings serverSettings = new ServerSettings();
    GameMap gameMap = new GameMap(serverSettings);


    ArrayList<Robot> listOfOwnRobots = new ArrayList<>();
    Robot robot = new Robot();
    robot.setPosition(2, 4);
    gameMap.placeObjectOnMap(robot, 2, 4);
    robot.setOwner("TestCase");
    listOfOwnRobots.add(robot);

    when(clientThread.getConnectedLobby()).thenReturn(connectedLobby);
    when(connectedLobby.getServerSettings()).thenReturn(serverSettings);

    when(connectedLobby.getGameMap()).thenReturn(gameMap);

    when(clientThread.getRobots()).thenReturn(listOfOwnRobots);
    clientThread.addRobot();
    String moveOfRobot = "0:Move:3:3";
    Move move = new Move();
    move.decode(clientThread, moveOfRobot);

    Assertions.assertEquals(gameMap.getCellArray()[3][3].robotsOnCell().size(), 1);
    Assertions.assertEquals(gameMap.getCellArray()[3][3].robotsOnCell().get(0), listOfOwnRobots.get(0));

  }

  @Test
  public void testDecodeMakeActionRequest() {
    clientThread = mock(ClientThread.class);
    connectedLobby = mock(Lobby.class);
    ServerSettings serverSettings = new ServerSettings();
    GameMap gameMap = new GameMap(serverSettings);


    ArrayList<Robot> listOfOwnRobots = new ArrayList<>();
    Robot robot = new Robot();
    robot.setPosition(0, 3);
    gameMap.placeObjectOnMap(robot, robot.getPosition());
    robot.setOwner("TestCase");
    listOfOwnRobots.add(robot);

    when(clientThread.getConnectedLobby()).thenReturn(connectedLobby);
    when(connectedLobby.getServerSettings()).thenReturn(serverSettings);

    when(connectedLobby.getGameMap()).thenReturn(gameMap);

    when(clientThread.getRobots()).thenReturn(listOfOwnRobots);
    clientThread.addRobot();
    String moveOfRobot = "0:RequestRadar:0:3:Radar";
    Move move = new Move();
    move.decode(clientThread, moveOfRobot);

    Assertions.assertEquals(listOfOwnRobots.get(0).getInventory().getClass(), Radar.class);
  }




  @Test
  public void testDecodeMakeActionDig() {
    clientThread = mock(ClientThread.class);
    connectedLobby = mock(Lobby.class);
    ServerSettings serverSettings = new ServerSettings();
    GameMap gameMap = new GameMap(serverSettings);

    Ore ore = new Ore();
    ore.setID(2);
    ore.setOwner("ServerTestCase");
    gameMap.getCellArray()[2][3].place(ore);
    ArrayList<Robot> listOfOwnRobots = new ArrayList<>();
    Robot robot = new Robot();
    robot.setPosition(2, 3);
    gameMap.placeObjectOnMap(robot, robot.getPosition());
    robot.setOwner("TestCase");
    listOfOwnRobots.add(robot);

    when(clientThread.getConnectedLobby()).thenReturn(connectedLobby);
    when(connectedLobby.getServerSettings()).thenReturn(serverSettings);

    when(connectedLobby.getGameMap()).thenReturn(gameMap);

    when(clientThread.getRobots()).thenReturn(listOfOwnRobots);
    clientThread.addRobot();
    String moveOfRobot = "0:Dig:2:3";
    Move move = new Move();
    move.decode(clientThread, moveOfRobot);

    Assertions.assertEquals(listOfOwnRobots.get(0).getInventory().getClass(), Ore.class);
  }


  @Test
  public void testDecodeMakeActionDigWhileCarryingRadar() {
    clientThread = mock(ClientThread.class);
    connectedLobby = mock(Lobby.class);
    ServerSettings serverSettings = new ServerSettings();
    GameMap gameMap = new GameMap(serverSettings);

    Ore ore = new Ore();
    ore.setID(2);
    ore.setOwner("ServerTestCase");
    gameMap.getCellArray()[2][3].place(ore);
    ArrayList<Robot> listOfOwnRobots = new ArrayList<>();
    Robot robot = new Robot();
    robot.setPosition(2, 3);
    gameMap.placeObjectOnMap(robot, robot.getPosition());
    robot.setOwner("TestCase");
    Radar radar = new Radar();
    radar.setID(1);
    radar.setOwner("SomeTestCaseName");
    robot.loadInventory(radar);
    listOfOwnRobots.add(robot);

    when(clientThread.getConnectedLobby()).thenReturn(connectedLobby);
    when(connectedLobby.getServerSettings()).thenReturn(serverSettings);

    when(connectedLobby.getGameMap()).thenReturn(gameMap);

    when(clientThread.getRobots()).thenReturn(listOfOwnRobots);
    clientThread.addRobot();
    String moveOfRobot = "0:Dig:2:3";
    Move move = new Move();
    move.decode(clientThread, moveOfRobot);

    Assertions.assertEquals(listOfOwnRobots.get(0).getInventory().getClass(), Ore.class);
    Assertions.assertNotNull(gameMap.getCellArray()[2][3].radarOnCell());
  }


  @Test
  public void testDecodeUpdate() {
    clientThread = mock(ClientThread.class);
    connectedLobby = mock(Lobby.class);

    when(connectedLobby.getServerSettings()).thenReturn(new ServerSettings());

    when(clientThread.getCurrentGameMap()).thenReturn(new GameMap(new ServerSettings()));

    GameMap gameMap = new GameMap(connectedLobby.getServerSettings());

    gameMap.spawnOreInMap();

    Update update = new Update();

    String message = update.encodeWithContent(AbstractPacket.replaceIndicatorChars(update.encodeWithContent(gameMap.cellStrings())));

    update.decode(clientThread, message);

    Assertions.assertEquals(clientThread.getCurrentGameMap(), gameMap);

  }

  @Test
  public void testDecodeChat () {
    Client client = mock(Client.class);
    InputStreamThread inputStreamThread = mock(InputStreamThread.class);

    when(client.lastChatMessageProperty()).thenReturn(new SimpleStringProperty());
    when(inputStreamThread.getClient()).thenReturn(client);

    Chat chat = new Chat();
    String chatMessage = "Testing!";

    chat.decode(inputStreamThread, AbstractPacket.replaceIndicatorChars(chat.encodeWithContent(chatMessage)));

    Assertions.assertEquals(client.lastChatMessageProperty().getValue(), chatMessage + "\n");
  }


}