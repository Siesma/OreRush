package game.server;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;

import java.io.OutputStream;
import java.util.ArrayList;


public class PingThread implements Runnable {
    ArrayList<ClientThread> clientsWithNoResponse = new ArrayList<>();
    protected boolean isPingReceived = false;

    public void run() {
        System.out.println("Ping thread started");
        while (true) {
            for(ClientThread clientThread :Server.getClientThreads()) {
                // System.out.println("Ping sent");
                sendPing(clientThread.getOutputStream());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isPingReceived) {
                    System.out.println("No response from clientThread.");
                    clientsWithNoResponse.add(clientThread);
                    disconnectClient(clientThread);
                } else {
                    System.out.println("The Ping was received and confirmed");
                    isPingReceived = false; //This resets the check for the next cycle.
                }
            }

            for (ClientThread clientWithNoResponse:clientsWithNoResponse) {
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
            System.out.println("Send Ping to Client");
            PacketHandler.pushMessage(outputStream, PacketGenerator.generateNewPacket("awake"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void disconnectClient(ClientThread clientThread) {
        clientThread.setConnectedToServer(false);
        Server.getClientThreads().remove(clientThread);
        System.out.println("Disconnected Client from Server");
    }
}
