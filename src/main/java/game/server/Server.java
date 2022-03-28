package game.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {


    private static final ArrayList<ClientThread> clientThreads = new ArrayList<>();


    public void run(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Now listening on port " + port);

        PingThread pT = new PingThread();
        Thread pingThread = new Thread(pT);
        pingThread.start();

        while (true) {
            try {
                System.out.println(clientThreads.size() + " clients are connected to the server.");
                System.out.println("Waiting for client connection... ");

                Socket socket = serverSocket.accept();
                ClientThread cT = new ClientThread(socket);
                clientThreads.add(cT);
                Thread clientThread = new Thread(cT);
                clientThread.start();
                System.out.println("New client connected.");


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("EXITING");
                System.exit(1);
            }
        }
    }

    public static ArrayList<ClientThread> getClientThreads() {
        return clientThreads;
    }
}
