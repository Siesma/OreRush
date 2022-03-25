package game.server;

import java.io.OutputStream;
import java.util.ArrayList;


public class PingThread implements Runnable {
    ArrayList<ClientThread> clientsWithNoResponse = new ArrayList<>();

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
                if (!isPingReceived()) {
                    System.out.println("No response from clientThread.");
                    clientsWithNoResponse.add(clientThread);
                    disconnectClient(clientThread);
                } else {
                    // System.out.println("Ping received");
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
        // TODO outputStream.write();
    }

    private boolean isPingReceived() {
        // TODO
        boolean answer = true;
        return answer;
    }

    private void disconnectClient(ClientThread clientThread) {
        clientThread.setConnectedToServer(false);
    }
}
