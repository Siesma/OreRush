package game.packet;

public class PacketType {
    /*
    This Class should represent a packetType and carry all it's values.
     */

    //This identifies the type of the Package to make decoding easier
    public String type;
    //This contains all the data of the Package
    public Object[] content = new Object[100]; //TODO: Figure out a reasonable array size

    /*
    Prints out the full Packet to the console for debugging / validation
     */
    public void printPacketOnCommandLine() {
        System.out.println("This packet contains the following information:");
        System.out.println("Packet type: " + this.type);
        System.out.println("Packet Content: ");
        int i = 0;
        while (this.content[i] != null) {
            System.out.println("> " + this.content[i]);
            i++;
        }
    }
}
