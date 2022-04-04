package game.client;


import game.packet.PacketGenerator;
import game.packet.PacketHandler;


public class PongThread implements Runnable {

    private final Client client;

    public PongThread(Client client) {
        this.client = client;
    }

    public void run() {
        System.out.println("Pong thread started");
        while (true) {
            sendPong();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!client.isPongReceived()) {
                System.out.println("No response from the server.");
                System.out.println("The client will shutdown shortly.");
                client.shutDownClient(); // TODO: try and reconnect to server
            } else {
                client.setPongReceived(false);
            }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends an awake packet to the server to ensure connection.
     */
    private void sendPong() {
        try {
            PacketHandler.pushMessage(client.getOutputStream(), PacketGenerator.generateNewPacket("awake"));
        } catch (Exception e) {
            System.out.println("Client-server connection lost");
        }
    }
}
