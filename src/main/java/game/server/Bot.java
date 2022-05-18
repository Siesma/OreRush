package game.server;

import game.datastructures.Cell;
import game.datastructures.GameMap;
import game.datastructures.Radar;
import game.datastructures.Robot;
import game.helper.MathHelper;
import game.helper.Vector;
import game.packet.PacketHandler;
import game.packet.packets.Nickname;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Bot extends ClientThread {

  private String name;
  private Lobby lobby;
  private ServerSettings serverSettings;
  private ArrayList<Robot> robots = new ArrayList<>();
  private GameMap gameMap;

  public Bot(Server server, Socket socket, String name, Lobby lobby) throws IOException {
    super(server, socket);
    this.name = name;
    this.lobby = lobby;
    this.serverSettings = lobby.getServerSettings();
    updateMap();
  }


  public ArrayList<String> getMoves() {
    ArrayList<String> out = new ArrayList<>();
    for (Robot r : this.getRobots()) {
      String move = getMove(r);
      out.add(move);
    }
    return out;
  }

  public void updateMap() {
    this.gameMap = lobby.getGameMap().getIndividualGameMapForPlayer(name);
  }

  public int getAmountOfNewRadarCoverageForCell(Cell cell) {
    return 0;
  }

  public int[] getBestTrapPosition () {
    return null;
  }


  public Cell getBestRadarPosition() {
    Cell best = gameMap.getCellArray()[0][0];
    int bestAmount = 0;
    for (int x = 0; x < gameMap.getGameMapSize()[0]; x++) {
      for (int y = 0; y < gameMap.getGameMapSize()[0]; y++) {

        int yo = (gameMap.getGameMapSize()[0] / 2) + (int) (Math.pow(-1, y) * ((y + 1) / 2));
        Cell xy = gameMap.getCellArray()[x][y];
        int curNewRevealAmount = getAmountOfNewRadarCoverageForCell(xy);

        int curAmount = getAmountOfNewRadarCoverageForCell(xy);
        if (bestAmount < curAmount && xy.radarOnCell() == null && xy.trapOnCell() == null) {
          best = xy;
          bestAmount = curAmount;
        }
      }
    }
    return best;
  }

  public String getMove(Robot r) {
    return r.getId() + "Wait:" + r.getPosition()[0] + ":" + r.getPosition()[1] + "";
  }

  public double radarDesireForRobot(ArrayList<Robot> robots, Robot robotInQuestion) {
    double desire = 1;
    for (Robot r : robots) {
      if (r.equals(robotInQuestion)) {
        continue;
      }
      if (r.getInventory() instanceof Radar) {
        desire = 0;
        break;
      }
      desire *= Math.sqrt(((double) r.getPosition()[0] - robotInQuestion.getPosition()[0]) / (double) serverSettings.getMaxAllowedMoves());
    }
    return desire;
  }

  public Cell closestOreCell (Robot r, int allowedSteps) {
    int curmoveindex = 0;
    Vector cur = new Vector(-1, -1);
    for (int i = 0; i < (serverSettings.getMaxAllowedMoves() * serverSettings.getMaxAllowedMoves()) * allowedSteps; i++) {
      for (int k = 0; k < 2; k++) {
        for (int j = 0; j < i; j++) {
          cur.add(directions.values()[curmoveindex].add);
          int xi = Math.max(0, Math.min(cur.getX(), gameMap.getGameMapSize()[0] - 1));
          int yi = Math.max(0, Math.min(cur.getY(), gameMap.getGameMapSize()[1] - 1));
          if (gameMap.getCellArray()[xi][yi].oreOnCell().size() > 0) {
            return gameMap.getCellArray()[xi][yi];
          }
        }
        curmoveindex++;
        curmoveindex = curmoveindex % directions.values().length;
      }
    }
    return null;
  }


  enum directions {
    left("L", new Vector(-1, 0)), up("U", new Vector(0, 1)), right("R", new Vector(1, 0)), down("D", new Vector(0, -1));
    String n;
    Vector add;

    directions(String n, Vector add) {
      this.n = n;
      this.add = add;
    }
  }

  public String move__move (Robot r, int x, int y) {
    return r.getId() + ":Move:" + x + ":" + y;
  }
  public String move__dig (Robot r, int x, int y) {
    return r.getId() + ":Dig:" + x + ":" + y;
  }

  Robot closestRobotToSpawn(ArrayList<Robot> robots) {
    Robot closest = robots.get(0);
    for (Robot r : robots) {
      if (r.isDead()) {
        continue;
      }
      if (r.getPosition()[0] < closest.getPosition()[0]) {
        closest = r;
      }
    }
    return closest;
  }

  String getRandomMove(Robot r, int min_threshold) {
    if(r.getPosition()[0] >= gameMap.getGameMapSize()[0] - 1) {

    }
    return "";
  }

}
