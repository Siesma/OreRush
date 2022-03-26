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

  public void run(String hostAddress, int port, String name) {
    try {
      Socket sock = new Socket(hostAddress, port);
      InputStream in = sock.getInputStream();
      OutputStream out = sock.getOutputStream();
      ContentThread th = new ContentThread(in);
      th.out = out;
      Thread iT = new Thread(th);
      iT.start();
      BufferedReader conin = new BufferedReader(new InputStreamReader(System.in));
      String line;
      while (true) {
        line = conin.readLine();
        // TODO: Create a function that interptes the incoming "line" according to the packets own functions
//        PacketType interpretedPacket = PacketType.close;
//        if (interpretedPacket == PacketType.close) {
//          break;
//        }
        // TODO: delete, testing as long as close packet not functionnal
        if (line.equals("quit")) {
          break;
        }

        PacketHandler.pushMessage(out, PacketGenerator.createPacketMessageByUserInput());

      }
      System.out.println("terminating ..");
      in.close();
      out.close();
      sock.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
