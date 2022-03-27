package game.client;


import game.packet.PacketGenerator;
import game.packet.PacketHandler;


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
                System.out.println("No response from the server.");
                System.out.println("The client will shutdown shortly.");
                client.shutDownClient(client); // TODO: try and reconnect to server
            }else{
                System.out.println("Pong is received and confirmed by the server");
                client.setPongReceived(false);
            }
        }
    }

    private void sendPong(){
        try {
            System.out.println("Sent pong to the server");
            PacketHandler.pushMessage(client.getOutputStream(), PacketGenerator.generateNewPacket("awake"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
