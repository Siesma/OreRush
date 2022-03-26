package game.client;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;
import game.packet.PacketType;
import game.server.ServerConstants;

import java.io.*;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

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

      //This while loop will generate user-input on the commandline
      while (true) {
        PacketHandler.pushMessage(out, PacketGenerator.createPacketMessageByUserInput(this));
        if (isShuttingDown) {
          break;
        }
      }
      System.out.println("terminating ..");
      in.close();
      out.close();
      sock.close();

    } catch (IOException e) {
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
