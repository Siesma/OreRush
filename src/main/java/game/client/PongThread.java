package game.client;

import game.server.ClientThread;
import game.packet.PacketGenerator;
import game.packet.PacketHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class PongThread implements Runnable{

    private final OutputStream outputStream;

    public PongThread(OutputStream outputStream) {
        this.outputStream = outputStream;
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
            if(!isPongReceived()){
                System.out.print("No response");


            }else{
                System.out.println("Pong is received and confirmed");

            }
        }
    }

    private void sendPong(){
        try {
            System.out.println("Send Pong to Server");
            PacketHandler.pushMessage(outputStream, PacketGenerator.generateNewPacket("awake"));
        }catch(Exception e){
            e.printStackTrace();
        }


    }
    private boolean isPongReceived(){

        return answer;
    }
}
