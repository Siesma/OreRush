package game.client;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;
import game.packet.PacketType;

import java.io.*;
import java.net.Socket;

public class Client {

  private boolean isShuttingDown = false;
  private InputStream inputStream;
  private static OutputStream outputStream;


  public void run(String hostAddress, int port, String name) {
    try {
      Socket socket = new Socket(hostAddress, port);
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();
      ContentThread th = new ContentThread(inputStream);
      th.out = outputStream;
      Thread iT = new Thread(th);
      iT.start();

      PacketType namePacket = PacketGenerator.generateNewPacket("nickn");
      namePacket.content[1] = name;
      PacketHandler.pushMessage(outputStream, namePacket);

      PongThread pT = new PongThread(this);
      Thread pongThread = new Thread(pT);
      pongThread.start();

      //This while loop will generate user-input on the commandline
      do {
        PacketHandler.pushMessage(outputStream, PacketGenerator.createPacketMessageByUserInput(this));
      } while (!isShuttingDown);
      System.out.println("terminating ..");
      inputStream.close();
      outputStream.close();
      socket.close();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
  This switches causes the thread to break out of the While(true) loop and shut itself down.
   */
  public void shutDownClient() {
    isShuttingDown = true;
  }


  public OutputStream getOutputStream() {
    return outputStream;
  }

}
