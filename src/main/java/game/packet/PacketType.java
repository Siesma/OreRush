//package packet;
//
//public enum PacketType {
//
//  initialize("The init packet consists of one user input part. $\"Name\"", new String[]{
//    ".*", //Name
//    "^([1-9]{2,3}.){3}:([1-9]{3,5})$"  //Resolve IP
//  }, "A default initialization response!"),
//
//  timeout("the timeout packet consists of no user input parts.", new String[]{
//    "^.*$", // the server that is sending the timeout
//    "^(?i)timeout$" // Timeout - case-insensitive
//  }, "The user timed out!"),
//  chat("", new String[]{
//    "^.*$", // Player name + resolve IP
//    "^.*$"
//  }, "Chatting, keep your toxicity to yourself!"),
//  success("the success packet consists of no user input parts.", new String[]{
//    "PLACEHOLDER FOR THE SERVER", // the server that is sending the timeout
//    "^[1-9]{0,255}$" // A randomly generated key which is a random number 0 < x < 255 - a simple XOR on each byte
//  }, "The connection has been established!"),
//  awake("the awake packet consists of no user input parts.", new String[]{
//    "^(?i)awake$" // Awake - case-insensitive
//  }, "Awake?"),
//  close("the close packet consists of no user input parts", new String[]{
//    "(?i)close", // Close - case-insensitive
//    "PLACEHOLDER FOR THE PLAYER" // Player name + resolve IP
//  }, "Closing the connection!"),
//  /*
//  Move-Packet: Is send in the form:
//      PLACEHOLDER FOR THE PLAYER
//      (index_of_robot)->(type_of_action(required_parameters_for_action))
//  If a Robot is not assigned a move, it will be defaulted to wait at its location.
//   */
//  move("the move packet consists of one user input part that is repeated $NUM_ROBOTS times. $\"Robot_Move\"", new String[]{
//    "^.*$", // Player name + resolve IP
//    "^((([1-9]+)->(MOVE\\{[1-9]+,[1-9]+\\}|DIG\\{[1-9]+,[1-9]+\\}|REQUEST\\{[1-3]\\}),)+)?" +
//      "(([1-9]+)->(MOVE\\{[1-9]+,[1-9]+\\}|DIG\\{[1-9]+,[1-9]+\\}|REQUEST\\{[1-3]\\}))$" // Zug für jeden Roboter
//  }, "Making a move!"),
//  /*
//  Update-Packet: Is send in the form:
//      PLACEHOLDER FOR THE PLAYER
//      (index_of_cell)->(OPTIONAL{robot(robot_team_as_number)})(OPTIONAL{ore(type_as_number,amount_as_number)})(OPTIONAL{asset_trap(trap_team_as_number)})(OPTIONAL{asset_radar(radar_team_as_number)})
//      cooldown_for_radar,cooldown_for_trap,cooldown_for_detector
//      robot(index_of_robot,(position_x,position_y))(,(OPTIONAL{asset_carrying(carrying_item)}))(,(OPTIONAL{ore_carrying(type_as_number,amount_as_number)}))
//      score_of_player_as_number
//      (index_of_enemy,score_of_enemy)
//   */
//  update("", new String[]{
//    "^.*$", // Player name + resolve IP
//    "^([1-9]+(->((\\{(?i)robot\\([1-9]+\\)\\})?)((\\{(?i)ore\\([1-9]+,[1-9]+\\)\\})?)((\\{(?i)trap\\([1-9]+\\)\\})?)((\\{(?i)radar\\([1-9]+\\)\\})?))?)$", // Sending coordinate in the format (x+y*width) and occupying Objects
//    "^([1-9]+),([1-9]+),([1-9]+)$",
//    "^(((?i)robot\\(([1-9]+,\\([1-9]+,[1-9]+\\))((,(?i)trap|(?i)radar|(?i)detector)?)((,(?i)ore:\\([1-9]+,[1-9]+\\))?)\\)),)+?((?i)robot\\(([1-9]+,\\([1-9]+,[1-9]+\\))((,(?i)trap|(?i)radar|(?i)detector)?)((,(?i)ore:\\([1-9]+,[1-9]+\\))?)\\))$",
//    "^[1-9]+$",
//    "^((([1-9]+:[1-9]+),)+)?([1-9]+:[1-9]+)$"
//  }, "Updating the user about the board!");
//
//
//  // Regex to find out whether the individual parts of the packet are sent (and received) correctly
//  String[] partMatching;
//  String response;
//  String help;
//  // removing whitespaces to ease out the matching step: replaceAll("\\s+", "")
//  PacketType(String help, String[] partMatching, String response) {
//    this.help = help;
//    this.partMatching = partMatching;
//    this.response = response;
//  }
//  public String getHelp () {
//    return help;
//  }
//
//  public String[] getPartMatching() {
//    return partMatching;
//  }
//
//  public PacketType setPartMatching(String[] partMatching) {
//    this.partMatching = partMatching;
//    return this;
//  }
//
//  public String getResponse() {
//    return response;
//  }
//
//  public PacketType setResponse(String response) {
//    this.response = response;
//    return this;
//  }
//}
