package game.server;

import game.packet.PacketHandler;
import game.packet.packets.Awake;

import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PingThread implements Runnable {
  ArrayList<ClientThread> clientsWithNoResponse = new ArrayList<>();
  public static final Logger logger = LogManager.getLogger(Server.class);

  public void run() {
    logger.info("Ping thread started");
    while (true) {
      // creates a copy of the connected clients to avoid ConcurrentModificationErrors (different solution?)
      ArrayList<ClientThread> list = new ArrayList<>(Server.getClientThreads());

      for (ClientThread clientThread : list) {
        sendPing(clientThread.getOutputStream());
        // gives time to the client to respond
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (!clientThread.isPingReceived()) {
          logger.warn("No response from "
                  + clientThread.getPlayerName()
                  + " after 3 seconds.");
          logger.warn("The connection has been interrupted");
          clientsWithNoResponse.add(clientThread);

        } else {
          clientThread.setPingReceived(false); //This resets the check for the next cycle.
        }
      }

      // delete clients with no response
      for (ClientThread clientWithNoResponse : clientsWithNoResponse) {
        Server.getClientThreads().remove(clientWithNoResponse);
      }
      try {
        Thread.sleep(120000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Sends an awake-Packet to the client
   *
   * @param outputStream of the client thread(server) that is connected to the inputStream of the target client
   */
  private void sendPing(OutputStream outputStream) {
    try {
      (new PacketHandler(this)).pushMessage(outputStream, (new Awake().encode()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
