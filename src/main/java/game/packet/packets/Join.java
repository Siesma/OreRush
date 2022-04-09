package game.packet.packets;

import game.packet.AbstractPacket;

public class Join extends AbstractPacket {

  public Join() {
    super("The init packet consists of one user input part. $\"Name\"", new String[]{
      ".*", //Name
      "^([1-9]{2,3}.){3}:([1-9]{3,5})$"  //Resolve IP
    }, "A default initialization response!");
  }



  @Override
  public String encodeWithContent(String... content) {
    return null;
  }

  @Override
  public String encode() {
    return "";
  }

  @Override
  public void decode(Object parent, String message) {

  }
}
