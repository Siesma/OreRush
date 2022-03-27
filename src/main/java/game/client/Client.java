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

  private boolean pongReceived = false;


  public void run(String hostAddress, int port, String name) {
    try {
      Socket socket = new Socket(hostAddress, port);
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();

      ContentThread th = new ContentThread(this);
      Thread iT = new Thread(th);
      iT.start();

      PacketType namePacket = PacketGenerator.generateNewPacket("nickn");
      namePacket.content[1] = name;
      PacketHandler.pushMessage(outputStream, namePacket);

      PongThread pT = new PongThread(this);
      Thread pongThread = new Thread(pT);
      pongThread.start();

      //This while loop will generate user-input on the commandline
      while (!isShuttingDown) {

        PacketHandler.pushMessage(outputStream, PacketGenerator.createPacketMessageByUserInput(this));

      }
      System.out.println("terminating ..");
      inputStream.close();
      outputStream.close();
      socket.close();
      System.out.println("EXITING");

      System.exit(0);

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
    System.out.println("fadfadfaf");
    setShuttingDown(true);
  }


  public OutputStream getOutputStream() {
    return outputStream;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public void setPongReceived(boolean pongReceived) {
    this.pongReceived = pongReceived;
  }

  public boolean isPongReceived() {
    return pongReceived;
  }
  public void setShuttingDown(boolean shuttingDown) {
    isShuttingDown = shuttingDown;
  }
}
