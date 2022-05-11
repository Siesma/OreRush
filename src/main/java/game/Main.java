package game;

import com.sun.javafx.runtime.VersionInfo;
import game.gui.ClientApp;
import game.server.Server;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class of the program
 * to be launched with arguments: client hostAddress:port [username] | server port
 * Depending on the arguments it either launches a server or a client
 */
public class Main {
    public static final Logger logger = LogManager.getLogger(Main.class);
    public static String hostAddress;
    public static String port;
    public static String name;

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.info("Error: Missing arguments.");
            logger.info("To launch the program following command line parameters are expected:");
            logger.info("client <hostAddress>:<port> [<username>] | server <port>");
            System.exit(1);
        } else if (args.length > 3) {
            logger.info("Error: Too many arguments.");
            logger.info("To launch the program following command line parameters are expected:");
            logger.info("client <hostAddress>:<port> [<username>] | server <port>");
            logger.info("The username can't contain spaces.");
            System.exit(1);
        }
        try {
            if (args[0].equals("server")) {
                int port = Integer.parseInt(args[1]);
                Server server = new Server();
                server.run(port);
            } else if (args[0].equals("client")) {
                hostAddress = args[1].split(":")[0];
                port = args[1].split(":")[1];
                if (args.length == 3) {
                    name = args[2];
                } else {
                    name = System.getProperty("user.name");
                }

                Application.launch(ClientApp.class);

            } else {
                logger.info("Error: Wrong first argument.");
                logger.info("The first argument is expected to be \"server\" or \"client\"");
                for (String s : args) {
                    logger.info(s);
                }
                System.exit(1);
            }
        } catch (Exception e) {
            logger.error("Error: Wrong arguments.");
            logger.error("To launch the program following command line parameters are expected:");
            logger.error("client <hostAddress>:<port> [<username>] | server <port>");
            logger.error(e.getMessage());
        }
    }
}
