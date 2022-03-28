package game.server;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;

import java.io.OutputStream;
import java.util.ArrayList;


public class PingThread implements Runnable {
    ArrayList<ClientThread> clientsWithNoResponse = new ArrayList<>();

    public void run() {
        System.out.println("Ping thread started");
        while (true) {
            ArrayList<ClientThread> list = new ArrayList<>(Server.getClientThreads());

            for (ClientThread clientThread : list) {
                sendPing(clientThread.getOutputStream());
                System.out.println("Ping sent to " + clientThread.getPlayerName());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!clientThread.isPingReceived()) {
                    System.out.println("No response from "
                            + clientThread.getPlayerName()
                            + " after 3 seconds.");
                    System.out.println("The connection has been interrupted");
                    clientsWithNoResponse.add(clientThread);
                    clientThread.setConnectedToServer(false);
                } else {
                    System.out.println("Ping received and confirmed by "
                            + clientThread.getPlayerName()
                            + ".");
                    clientThread.setPingReceived(false); //This resets the check for the next cycle.
                }
            }

            for (ClientThread clientWithNoResponse : clientsWithNoResponse) {
                Server.getClientThreads().remove(clientWithNoResponse);
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // System.out.println(Server.getClientThreads().size() +" client(s) got pinged");
        }

    }

    private void sendPing(OutputStream outputStream) {
        try {
            PacketHandler.pushMessage(outputStream, PacketGenerator.generateNewPacket("awake"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
