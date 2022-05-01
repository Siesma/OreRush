package gameObject;


import game.datastructures.Cell;
import game.datastructures.GameObject;
import game.datastructures.Ore;
import game.datastructures.Radar;
import game.helper.FileHelper;
import game.helper.MapType;
import game.helper.TestHelper;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

public class CellTest extends TestCase {

    @Test
    public void testObjectsOnCell () {
        Cell cellA = new Cell(5, 2);

        Ore oreA = new Ore();
        oreA.setOwner("TestCase");
        oreA.setID(2);
        cellA.place(oreA);

        Assertions.assertNull(cellA.trapOnCell());
        Assertions.assertNull(cellA.robotsOnCell());
        Assertions.assertNull(cellA.radarOnCell());
        Assertions.assertNotNull(cellA.oreOnCell());

        Assertions.assertEquals(cellA.oreOnCell().size(), 1);
        Assertions.assertEquals(cellA.oreOnCell().get(0).getOwner(), "TestCase");

        Assertions.assertEquals(cellA.toString(), "5:2:Ore:2:TestCase");

        // randomized long tests:
        for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {

            int x = TestHelper.generateRandomNumber(100);
            int y = TestHelper.generateRandomNumber(100);
            Cell cell = new Cell(x, y);

            Set<String> objectHashMap = (new FileHelper()).getObjectMap().get(MapType.GameObjects.getHashName()).keySet();
            ArrayList<String> possibleObjects = new ArrayList<>();
            objectHashMap.stream().forEach(possibleObjects::add);
            ArrayList<GameObject> cellObjects = new ArrayList<>();
            for(int j = TestHelper.generateRandomNumber(100); j > 0; j--) {
                GameObject cellObject = (GameObject) (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, possibleObjects.get(TestHelper.generateRandomNumber(possibleObjects.size())));
                cellObjects.add(cellObject);
            }
            StringBuilder encodedPart = new StringBuilder();
            for (GameObject gameObject : cell.getPlacedObjects()) {
                encodedPart.append(gameObject.encodeToString());
            }
            Assertions.assertEquals(cell.toString(), cell.getX() + ":" + cell.getY() + ":" + encodedPart);
        }

    }

    @Test
    public void testPlaceObject () {

        Cell cellA = new Cell(12, 75);

        Radar radarA = new Radar();
        radarA.setOwner("TestCase");

        cellA.place(radarA);

        Assertions.assertNotNull(cellA.radarOnCell());
        Assertions.assertEquals(cellA.getPlacedObjects().get(0).getClass(), Radar.class);

        // randomized long tests:
        for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {

            int x = TestHelper.generateRandomNumber(100);
            int y = TestHelper.generateRandomNumber(100);
            Cell cell = new Cell(x, y);

            int amount = TestHelper.generateRandomNumber(1000) + 1;
            for(int j = amount; j > 0; j--) {
                Ore ore = new Ore();
                ore.setID(TestHelper.generateRandomNumber(1000));
                cell.place(ore);
            }
            Assertions.assertEquals(cell.oreOnCell().size(), amount);
            Assertions.assertEquals(cell.oreOnCell().size(), cell.getPlacedObjects().size());
        }
    }

}
