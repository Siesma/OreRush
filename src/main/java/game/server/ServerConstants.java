package game.server;

/**
 * Class containing the constants used in the project.
 * It holds the AScii character for the network protocol.
 */
public class ServerConstants {
    public static int DEFAULT_PORT = 1038;

    public static int DEFAULT_PACKET_STARTING_MESSAGE = 2; // AScii character called "Start of text"
    public static int DEFAULT_PACKET_ENDING_MESSAGE = 3; // AScii character called "Endof text"
    public static int DEFAULT_PACKET_SPACER = 31; // AScii character called "Unit separator"

}
