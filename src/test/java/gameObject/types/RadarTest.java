package gameObject.types;

import game.datastructures.Radar;
import game.helper.TestHelper;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RadarTest extends TestCase {

  @Test
  public void testEncoding() {

    Radar radarA = new Radar();
    radarA.setID(1);
    radarA.setOwner("TestCase");
    radarA.setPosition(1, 3);

    Assertions.assertEquals(radarA.getOwner(), "TestCase");
    Assertions.assertEquals(radarA.getPlayerID(), 1);
    Assertions.assertEquals(radarA.encodeToString(), "Radar:1:TestCase");
    Assertions.assertArrayEquals(radarA.getPosition(), new int[]{1, 3});

    // randomized long tests:

    for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {
      Radar radar = new Radar();
      int id = TestHelper.generateRandomNumber(100);
      String owner = TestHelper.generateRandomString((int) (Math.random() * 10));
      radar.setID(id);
      radar.setOwner(owner);
      int x = TestHelper.generateRandomNumber(100);
      int y = TestHelper.generateRandomNumber(100);
      radar.setPosition(x, y);

      Assertions.assertEquals(radar.getOwner(), owner);
      Assertions.assertEquals(radar.getPlayerID(), id);
      Assertions.assertEquals(radar.encodeToString(), "Radar:" + id + ":" + owner);
      Assertions.assertArrayEquals(radar.getPosition(), new int[]{x, y});

    }

  }

}
