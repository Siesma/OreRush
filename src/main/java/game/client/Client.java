package game.client;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;
import game.packet.PacketType;

import java.io.*;
import java.net.Socket;

public class Client {

  private Socket socket;
  private InputStream inputStream;
  private static OutputStream outputStream;

  private boolean pongReceived = false;


  public void run(String hostAddress, int port, String name) {
    try {
      socket = new Socket(hostAddress, port);
    } catch (Exception e) {
      System.out.println("The connection with the server failed.");
      System.out.println("Please ensure the server is running with same port and try again.");
      System.exit(0);
    }
    try {
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
      while (true) {
        PacketHandler.pushMessage(outputStream, PacketGenerator.createPacketMessageByUserInput(this));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
  This switches causes the thread to break out of the While(true) loop and shut itself down.
   */
  public void shutDownClient(Client client) {
    System.out.println("terminating ..");
    try {
      inputStream.close();
      outputStream.close();
      client.socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("EXITING");
    System.exit(0);
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
}
