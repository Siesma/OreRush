package gameObject.types;

import game.datastructures.Ore;
import game.datastructures.OreType;
import game.datastructures.Radar;
import game.helper.TestHelper;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OreTest extends TestCase {


  @Test
  public void testEncoding() {

    Ore oreA = new Ore();
    oreA.setID(1);
    oreA.setOwner("TestCase");
    oreA.setPosition(1, 3);

    Assertions.assertEquals(oreA.getOwner(), "TestCase");
    Assertions.assertEquals(oreA.getOreType().ordinal(), 1);
    Assertions.assertEquals(oreA.encodeToString(), "Ore:1:TestCase");
    Assertions.assertArrayEquals(oreA.getPosition(), new int[]{1, 3});

    // randomized long tests:

    for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {
      Ore ore = new Ore();
      int id = TestHelper.generateRandomNumber(100);
      String owner = TestHelper.generateRandomString((int) (Math.random() * 10));
      ore.setID(id);
      ore.setOwner(owner);
      int x = TestHelper.generateRandomNumber(100);
      int y = TestHelper.generateRandomNumber(100);
      ore.setPosition(x, y);

      Assertions.assertEquals(ore.getOwner(), owner);
      Assertions.assertEquals(ore.getOreType().ordinal(), id % OreType.values().length);
      Assertions.assertEquals(ore.encodeToString(), "Ore:" + (id % OreType.values().length) + ":" + owner);
      Assertions.assertArrayEquals(ore.getPosition(), new int[]{x, y});

    }

  }


}
