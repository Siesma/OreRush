package game;

import game.client.Client;
import game.server.Server;


public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: Missing arguments.");
            System.out.println("To launch the program following command line parameters are expected:");
            System.out.println("client <hostAddress>:<port> [<username>] | server <port>");
            System.exit(1);
        } else if (args.length > 3) {
            System.out.println("Error: Too many arguments.");
            System.out.println("To launch the program following command line parameters are expected:");
            System.out.println("client <hostAddress>:<port> [<username>] | server <port>");
            System.out.println("The username can't contain spaces.");
            System.exit(1);
        }
        try {
            if (args[0].equals("server")) {
                int port = Integer.parseInt(args[1]);
                Server server = new Server();
                server.run(port);
            } else if (args[0].equals("client")) {
                String hostAddress = args[1].split(":")[0];
                int port = Integer.parseInt(args[1].split(":")[1]);
                String name;
                if (args.length == 3) {
                    name = args[2];
                } else {
                    name = System.getProperty("user.name");
                }
                Client client = new Client();
                client.run(hostAddress, port, name);
            } else {
                System.out.println("Error: Wrong first argument.");
                System.out.println("The first argument is expected to be \"sever\" or \"client\"");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Error: Wrong arguments.");
            System.out.println("To launch the program following command line parameters are expected:");
            System.out.println("client <hostAddress>:<port> [<username>] | server <port>");
        }
    }
}
