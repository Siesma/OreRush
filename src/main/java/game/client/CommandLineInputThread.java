package game.client;

import java.io.OutputStream;

public class CommandLineInputThread implements Runnable {

    private final Client client;
    private final OutputStream outputStream;

    public CommandLineInputThread(Client client) {
        this.client = client;
        this.outputStream = client.getOutputStream();
    }

    @Override
    public void run() {
        while (true) {
//            PacketHandler packetHandler = new PacketHandler(this);
//            packetHandler.pushMessage(outputStream, packetHandler.createPacketMessage());
        }
    }
}
