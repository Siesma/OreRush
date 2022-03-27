package game.client;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;
import game.packet.PacketType;

import java.io.*;
import java.net.Socket;

public class Client {

  private boolean isShuttingDown = false;

  public void run(String hostAddress, int port, String name) {
    try {
      Socket sock = new Socket(hostAddress, port);
      InputStream in = sock.getInputStream();
      OutputStream out = sock.getOutputStream();
      ContentThread th = new ContentThread(in);
      th.out = out;
      Thread iT = new Thread(th);
      iT.start();



      PacketType namePacket = PacketGenerator.generateNewPacket("nickn");
      namePacket.content[1] = name;
      PacketHandler.pushMessage(out, namePacket);

      //This while loop will generate user-input on the commandline
      do {
        PacketHandler.pushMessage(out, PacketGenerator.createPacketMessageByUserInput(this));
      } while (!isShuttingDown);
      System.out.println("terminating ..");
      in.close();
      out.close();
      sock.close();

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
}
