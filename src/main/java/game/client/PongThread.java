package game.client;

import java.io.OutputStream;

public class PongThread implements Runnable{

    @Override
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

            }
        }
    }

    private void sendPong(){

    }
    private boolean isPongReceived(){
        boolean answer=true;
        return answer;
    }
}
