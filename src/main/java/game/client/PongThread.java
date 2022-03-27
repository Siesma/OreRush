package game.client;

import game.server.ClientThread;
import game.packet.PacketGenerator;
import game.packet.PacketHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class PongThread implements Runnable{

    private final Client client;

    public PongThread(Client client) {
        this.client = client;
    }

    public void run() {
        System.out.println("Pong thread started");
        while(true){
            sendPong();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!client.isPongReceived()){
                System.out.println("No response from server.");
                System.out.println("The client will shutdown shortly.");
                client.shutDownClient(); // TODO: try and reconnect to server

            }else{
                System.out.println("Pong is received and confirmed");
                client.setPongReceived(false);

            }
        }
    }

    private void sendPong(){
        try {
            System.out.println("Send Pong to Server");
            PacketHandler.pushMessage(client.getOutputStream(), PacketGenerator.generateNewPacket("awake"));
        }catch(Exception e){
            e.printStackTrace();
        }


    }

}
