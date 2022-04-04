package game.client;

import game.packet.PacketGenerator;
import game.packet.PacketHandler;

import java.io.OutputStream;

public class CommandLineInputThread implements Runnable{

    private final Client client;
    private final OutputStream outputStream;

    public CommandLineInputThread(Client client) {
        this.client = client;
        this.outputStream = client.getOutputStream();
    }

    @Override
    public void run() {
        while (true) {
            PacketHandler.pushMessage(outputStream, PacketGenerator.createPacketMessageByUserInput(client));
        }
    }
}
