package game;

import game.client.Client;
import game.server.Server;

import java.net.Inet4Address;

public class Main {

  public static void main(String[] args) {
    if(args.length == 0) {
      System.out.println("The user is expected to have an argument indicating which executable is meant.");
      System.out.println("\"1\" is for Server and \"2\" is for Client");
      System.exit(1);
    }
    try {
      if(Integer.parseInt(args[0]) == 1) {
        Server server = new Server();
        server.run(removeFirstElemet(args));
      } else if (Integer.parseInt(args[0]) == 2){
        Client client = new Client();
        client.run(removeFirstElemet(args));
      } else {
        System.out.println("The given input is expected to be \"1\" or \"2\"");
        System.exit(1);
      }
    } catch (Exception e) {
      System.err.println("The first argument is expected to be an integer.");
      e.printStackTrace();
    }
  }
  public static String[] removeFirstElemet (String[] in) {
    String[] out = new String[in.length - 1];
    for(int i = 1; i < in.length; i++) {
      out[i - 1] = in[i];
    }
    return out;
  }
}
