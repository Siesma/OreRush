package game.helper;

import game.packet.AbstractPacket;
import game.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileHelper {
  public static final Logger logger = LogManager.getLogger(Server.class);

  public FileHelper() {

  }
  public Object createInstanceOfClass(String relativePath) {
    try {
      Class<?> classes = (Class<?>) Class.forName(relativePath);
      return classes.newInstance();
    } catch (Exception e) {
//      logger.error("Some error occurred! Reverting to null");
      return null;
    }

  }

}
