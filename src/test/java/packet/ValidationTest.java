package packet;

import game.datastructures.*;
import game.helper.FileHelper;
import game.helper.MapType;
import game.helper.TestHelper;
import game.packet.AbstractPacket;
import game.packet.packets.Move;
import game.packet.packets.Update;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class ValidationTest extends TestCase {

    @Test
    public void testValidationMove() {
        Move move = (Move) AbstractPacket.getPacketByName("move");
        Robot robotA = new Robot();
        robotA.setOwner("TestCase");
        robotA.setID(0);
        Radar radarA = new Radar();
        radarA.setOwner("NotTheTestCase");
        robotA.loadInventory(radarA);
        int[] destA = {34, 5};
        String messagePartA = robotA.getId() + ":Move:" + destA[0] + ":" + destA[1];

        Assertions.assertTrue(move.validate(move.encodeWithContent(messagePartA)));
        Assertions.assertEquals("TestCase", robotA.getOwner());
        Assertions.assertEquals(robotA.getInventory().getClass(), radarA.getClass());
        Assertions.assertEquals(robotA.getOwner(), radarA.getOwner());
        Assertions.assertEquals(robotA.getId(), 0);


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
            int[] dest = new int[]{TestHelper.generateRandomNumber(100), TestHelper.generateRandomNumber(100)};
            robot.loadInventory(inventoryObject);
            RobotAction moveType = RobotAction.values()[TestHelper.generateRandomNumber(RobotAction.values().length)];

            String messagePart = robotA.getId() + ":" + moveType.name() + ":" + dest[0] + ":" + dest[1];
            Assertions.assertTrue(move.validate(move.encodeWithContent(messagePart)));

            Assertions.assertEquals(nameOwner, robot.getOwner());
            Assertions.assertEquals(robot.getInventory().getClass(), inventoryObject.getClass());
            Assertions.assertEquals(robot.getOwner(), inventoryObject.getOwner());
            Assertions.assertNotEquals(robot.getOwner(), notNameOwner);
            Assertions.assertEquals(robot.getId(), robotID);
        }
    }


    @Test
    public void testValidateUpdate () {
        Update update = (Update) AbstractPacket.getPacketByName("update");

        Assertions.assertTrue(update.validate(update.encodeWithContent("10,4_Radar:1:TestCase")));

        // randomized long tests:

        for (int i = TestHelper.generateRandomNumber(10000); i > 0; i--) {
            int[] xy = new int[] {TestHelper.generateRandomNumber(100), TestHelper.generateRandomNumber(100)};
            String owner = "testing";
            int id = TestHelper.generateRandomNumber(100);
            Set<String> objectHashMap = (new FileHelper()).getObjectMap().get(MapType.GameObjects.getHashName()).keySet();
            ArrayList<String> possibleObjects = new ArrayList<>();
            objectHashMap.stream().forEach(possibleObjects::add);
            String cellObject = possibleObjects.get(TestHelper.generateRandomNumber(possibleObjects.size()));
            Assertions.assertTrue(update.validate(update.encodeWithContent(xy[0] + "," + xy[1] + "_" + TestHelper.capitalize(cellObject) + ":" + id + ":" + owner)));

        }

    }

}
