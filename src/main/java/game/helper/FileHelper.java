package game.helper;

import game.packet.AbstractPacket;

public class FileHelper {

  public FileHelper() {

  }
  public Object createInstanceOfClass(String relativePath) {
    try {
      Class<?> classes = (Class<?>) Class.forName(relativePath);
      return classes.newInstance();
    } catch (Exception e) {
//      System.out.println("Some error occurred! Reverting to null");
      return null;
    }

  }

}
