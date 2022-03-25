package game.packet;

public class PacketType {
    /*
    This Class should represent a packetType and carry all it's values.
     */

  //This identifies the type of the Package to make decoding easier
  public String type;
  //This contains all the data of the Package
  public Object[] content = new Object[100]; //TODO: Figure out a resonable array size

  /*
  This function will turn this package into a Request-Package and fill it's content accodringly
   */
  public void generateRequestPackage (String clientName, long timeOfPacket, String ipOfClient) {
    this.type = "reqst";
    this.content[0] = clientName;
    this.content[1] = timeOfPacket;
    this.content[2] = ipOfClient;
  }

  /*
  This function will turn this package into a Timeout-Package and fill it's content accodringly
   */
  public void generateTimeoutPackage(String ipOfServer) {
    this.type = "timeo";
    this.content[0] = ipOfServer;
  }

  /*
  This function will turn this package into a Success-Package and fill it's content accodringly
   */
  public void generateSuccessPackage(int clientKey) {
    this.type = "succs";
    this.content[0] = clientKey;
  }

  /*
  This function will turn this package into a Awake-Package and fill it's content accodringly
   */
  public void generateAwakePackage(int timeOfPacket) {
    this.type = "awake";
    this.content[0] = timeOfPacket;
  }

  /*
  This function will turn this package into a Close-Package and fill it's content accodringly
   */
  public void generateClosePackage(int timeOfPacket, int clientKey) {
    this.type = "close";
    this.content[0] = timeOfPacket;
    this.content[1] = clientKey;
  }

  /*
  This function will turn this package into a Awake-Package and fill it's content accodringly

  TODO: Define classes for all parameters currently defined as "Object"
   */
  public void generateUpdatePackage(Object visibleMap, Object equipmentCooldown, Object robots, Object[] playerScores) {
    this.type = "updte";
    this.content[0] = visibleMap;
    this.content[1] = equipmentCooldown;
    this.content[2] = robots;
    this.content[3] = playerScores;
  }

  /*
  This function will turn this package into a Move-Package and fill it's content accodringly

  TODO: Define classes for all parameters currently defined as "Object"
   */
  public void generateMovePackage(int clientKey, Object moves) {
    this.type = "pmove";
    this.content[0] = clientKey;
    this.content[1] = moves;
  }

  /*
  This function will turn this package into a Chat-Package and fill it's content accodringly
   */
  public void generateChatPackage(int clientKey, String message) {
    this.type = "pchat";
    this.content[0] = clientKey;
    this.content[1] = message;
  }

  /*
  This function will turn this package into a Settings-Package and fill it's content accodringly

  TODO: Define classes for all parameters currently defined as "Object"
   */
  public void generateSettingsPackage(Object serverSettings) {
    this.type = "settn";
    this.content[0] = serverSettings;
  }
}
