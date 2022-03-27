package game.client;

import game.server.ClientThread;
import game.packet.PacketGenerator;
import game.packet.PacketHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class PongThread implements Runnable{


    public void run() {
        System.out.println("Pong thread started");
        while(true){
            sendPong(/* ClientThread.getOutputStream()*/);

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

    private void sendPong(/*OutputStream outputStreamServer*/){
        try {
            System.out.println("Send Pong to Server");
          //  PacketHandler.pushMessage(outputStreamServer, PacketGenerator.generateNewPacket("awake"));
        }catch(Exception e){
            e.printStackTrace();
        }


    }
    private boolean isPongReceived(){
        boolean answer=true;
        return answer;
    }
}
