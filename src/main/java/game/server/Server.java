package game.server;


import game.packet.PacketType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  public void run (int port) {

    System.out.println("Now listening on port " + port);

    try {
      ServerSocket serverSocket = new ServerSocket(port);
      Socket socket = serverSocket.accept();
      handleSocket(socket);
      socket.close();
      serverSocket.close();
    } catch (
      IOException e) {
      e.printStackTrace();
      System.out.println("EXITING");
      System.exit(1);
    }
  }


  private static void handleSocket(Socket socket) throws IOException {
    InputStream inputStream = socket.getInputStream();
    OutputStream outputStream = socket.getOutputStream();

    StringBuilder builder = new StringBuilder();
    boolean startingToRecordMessage = false;
    while (true) {

      int cur;
      try {
        cur = inputStream.read();
      } catch (IOException e) {
        System.out.println("Unexpected error!");
        cur = -1;
      }
      if (cur == -1) {
        return;
      }

      if (cur == ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE) {
        startingToRecordMessage = false;
        String message = builder.toString();
        pushMessage(message);
        builder.setLength(0);
        outputStream.write(generateOutputFromInput(message).getBytes());
      }
      if (startingToRecordMessage) {
        builder.append((char) cur);
      }
      if (cur == ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE) {
        startingToRecordMessage = true;
      }
    }
  }

  private static String generateOutputFromInput(String input) {
     PacketType packetType = interpretMessage(input);
        /*
        TODO: Replace default response with important information
         */
    return packetType.getResponse();
  }

  private static PacketType interpretMessage(String message) {
        /*
        TODO: Interpret message given the matching of packets
         */
    return PacketType.awake;
  }

  private static void pushMessage(String message) {
        /*
        TODO: Push the message to the user and the relevant parts of the program
         */
    System.out.println(message);
  }

}
