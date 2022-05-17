package game.server;

/**
 * Class containing the constants used in the project.
 * It holds the AScii character for the network protocol.
 */
public class ServerConstants {
    /**
     * default port
     */
    public static int DEFAULT_PORT = 1038;

    /**
     * default starting message for packet receiving
     */
    public static int DEFAULT_PACKET_STARTING_MESSAGE = 2; // AScii character called "Start of text"
    /**
     * default ending message for packet receiving
     */
    public static int DEFAULT_PACKET_ENDING_MESSAGE = 3; // AScii character called "Endof text"
    /**
     * default packet spacer for packet receiving
     */
    public static int DEFAULT_PACKET_SPACER = 31; // AScii character called "Unit separator"
    /**
     * default packet text spacer for packet receiving
     */
    public static int DEFAULT_TEXT_SPACER = 32; // AScii character used to separate text

}
