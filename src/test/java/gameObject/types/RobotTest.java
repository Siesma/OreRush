package gameObject.types;

import game.datastructures.GameObject;
import game.datastructures.Radar;
import game.datastructures.Robot;
import game.datastructures.RobotAction;
import game.helper.FileHelper;
import game.helper.MapType;
import game.helper.TestHelper;
import game.packet.packets.Awake;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

public class RobotTest extends TestCase {

  @Test
  public void testProperties() {
    Robot robotA = new Robot();
    robotA.setOwner("TestCase");
    robotA.setID(0);
    robotA.setPosition(12, 18);

    Assertions.assertEquals(robotA.getOwner(), "TestCase");
    Assertions.assertEquals(robotA.getPosition()[0], 12);
    Assertions.assertEquals(robotA.getPosition()[1], 18);
    Assertions.assertEquals(robotA.getId(), 0);
    Assertions.assertEquals(robotA.getInventory(), null);

    // randomized long tests:
    for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {
      Robot robot = new Robot();
      Set<String> objectHashMap = (new FileHelper()).getObjectMap().get(MapType.GameObjects.getHashName()).keySet();
      ArrayList<String> possibleObjects = new ArrayList<>();
      objectHashMap.stream().forEach(possibleObjects::add);

      GameObject inventoryObject = (GameObject) (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, possibleObjects.get(TestHelper.generateRandomNumber(possibleObjects.size())));

      int robotID = TestHelper.generateRandomNumber(100);
      String nameOwner = TestHelper.generateRandomString(100);
      String notNameOwner = TestHelper.generateRandomString(100);
      // unlikely, but has to be taken into consideration.
      while (notNameOwner.equals(nameOwner)) {
        notNameOwner = TestHelper.generateRandomString(100);
      }

      robot.setID(robotID);
      robot.setOwner(nameOwner);
      inventoryObject.setOwner(notNameOwner);

      robot.loadInventory(inventoryObject);

      Assertions.assertEquals(robot.getOwner(), nameOwner);
      Assertions.assertEquals(robot.getId(), robotID);
      Assertions.assertEquals(robot.getInventory().getClass(), inventoryObject.getClass());
      Assertions.assertEquals(robot.getInventory().getOwner(), inventoryObject.getOwner());
      Assertions.assertNotEquals(robot.getOwner(), notNameOwner);

    }


  }

  @Test
  public void testAction() {
    Robot robotA = new Robot();
    robotA.setPosition(5, 7);
    robotA.setAction(RobotAction.RequestRadar, 5, 1, new Radar());
    Assertions.assertNull(robotA.getInventory());
    Assertions.assertEquals(robotA.getPosition()[0], 5);
    Assertions.assertEquals(robotA.getPosition()[1], 1);
    robotA.setDead(true);
    robotA.setAction(RobotAction.Move, 1, 1, null);
    Assertions.assertNull(robotA.getInventory());

    // randomized long tests:
    for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {
      Robot robot = new Robot();

      int x = TestHelper.generateRandomNumber(5);
      int y = TestHelper.generateRandomNumber(5);
      robot.setPosition(x, y);
      int nx = TestHelper.generateRandomNumber(5);
      int ny = TestHelper.generateRandomNumber(5);

      RobotAction robotAction = RobotAction.values()[TestHelper.generateRandomNumber(RobotAction.values().length)];
      // this is okay because we test somewhere else that this is working.
      GameObject inventoryObject = new Radar();
      robot.setAction(robotAction, nx, ny, inventoryObject);
      if(nx == 0 && (robotAction == RobotAction.RequestRadar || robotAction == RobotAction.RequestTrap)) {
        Assertions.assertEquals(robot.getInventory().getClass(), inventoryObject.getClass());
      } else {
        Assertions.assertNull(robot.getInventory());
      }
    }

  }

  @Test
  public void testPosition() {
    Robot robotA = new Robot();
    robotA.setPosition(0, 7);
    Assertions.assertEquals(robotA.getPosition()[0], 0);
    Assertions.assertEquals(robotA.getPosition()[1], 7);


    // randomized long tests:
    for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {
      Robot robot = new Robot();

      int x = TestHelper.generateRandomNumber(100);
      int y = TestHelper.generateRandomNumber(100);

      robot.setPosition(x, y);

      Assertions.assertEquals(robot.getPosition()[0], x);
      Assertions.assertEquals(robot.getPosition()[1], y);

      int nx = TestHelper.generateRandomNumber(100);
      int ny = TestHelper.generateRandomNumber(100);

      robot.setPosition(nx, ny);

      Assertions.assertEquals(robot.getPosition()[0], nx);
      Assertions.assertEquals(robot.getPosition()[1], ny);

    }

  }

  @Test
  public void testEncoding() {

    Robot robotA = new Robot();

    robotA.setPosition(0, 1);
    robotA.setID(9);
    robotA.setOwner("TestCase");
    Radar inventory = new Radar();
    robotA.loadInventory(inventory);
    Assertions.assertEquals(robotA.encodeToString(), "Robot:9:TestCase:Radar:0:TestCase");

    // randomized long tests:
    for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {

      Robot robot = new Robot();

      int robotID = TestHelper.generateRandomNumber(100);
      String nameOwner = TestHelper.generateRandomString(100);

      Set<String> objectHashMap = (new FileHelper()).getObjectMap().get(MapType.GameObjects.getHashName()).keySet();
      ArrayList<String> possibleObjects = new ArrayList<>();
      objectHashMap.stream().forEach(possibleObjects::add);

      GameObject inventoryObject = (GameObject) (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, possibleObjects.get(TestHelper.generateRandomNumber(possibleObjects.size())));

      robot.setID(robotID);
      robot.setOwner(nameOwner);
      inventoryObject.setID(0);
      robot.loadInventory(inventoryObject);

      Assertions.assertEquals(robot.encodeToString(), "Robot:" + robotID + ":" + nameOwner + ":" + inventoryObject.encodeToString());


    }

  }

}
