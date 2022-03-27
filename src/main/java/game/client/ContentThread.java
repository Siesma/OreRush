package game.client;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;
import game.packet.PacketType;
import game.server.ServerConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ContentThread implements Runnable {
  InputStream in;
  OutputStream out;

  public ContentThread(InputStream in) {
    this.in = in;
  }

  public void run() {
    int len;
    byte[] b = new byte[100];
    boolean startingToRecordMessage = false;
    StringBuilder builder = new StringBuilder();
    while (true) {
      int cur;
      try {
        cur = in.read();
      } catch (IOException e) {
        cur = -1;
      }
      if (cur == -1) {
        return;
      }

      /*
      This part is executed once the end of the message is reached.
       */
      if (cur == ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE) {
        startingToRecordMessage = false;
        String message = builder.toString();
        //PacketHandler.pushMessage(message);
        builder.setLength(0);

        //This part here prints out what the server received. This is here just for bug fixing and manual validation.
        try {
          PacketType receivedPacket = PacketHandler.decode(message);
          if (receivedPacket == null) {
            System.out.println("The recieved packet contains garbage.");
            break;
          }
          //receivedPacket.printPacketOnCommandLine();
          generateAppropriateReaction(receivedPacket);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      /*
      This will read the whole message into the builder.
       */
      if (startingToRecordMessage) {
        builder.append((char) cur);
      }

      /*
      This is executed when the server detects the start of a message.
       */
      if (cur == ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE) {
        startingToRecordMessage = true;
      }
    }
  }

  /*
  This causes a reaction based on a received Packet.
   */
  private void generateAppropriateReaction(PacketType packet) {
    switch (packet.type) {
      case "reqst":
        break;
      case "timeo":
        break;
      case "succs":
        break;
      case "awake":
        confirmPingFromServer();
        break;
      case "updte":
        break;
      case "pchat":
        printChatMessageToCommandLine(packet);
        break;
      case "settn":
        break;
      default:
    }
  }

  private void printChatMessageToCommandLine(PacketType packet) {
    System.out.println(packet.content[1]);
  }

  private void confirmPingFromServer() {
    try {
      PacketHandler.pushMessage(out, PacketGenerator.generateNewPacket("succs"));
    } catch (Exception e) {

    }
  }
}
