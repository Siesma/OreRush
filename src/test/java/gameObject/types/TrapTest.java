package gameObject.types;

import game.datastructures.Trap;
import game.helper.TestHelper;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TrapTest extends TestCase {

  @Test
  public void testEncoding() {

    Trap trapA = new Trap();
    trapA.setID(1);
    trapA.setOwner("TestCase");
    trapA.setPosition(7, 1);

    Assertions.assertEquals(trapA.getOwner(), "TestCase");
    Assertions.assertEquals(trapA.getPlayerID(), 1);
    Assertions.assertEquals(trapA.toString(), "Trap:1:TestCase");
    Assertions.assertArrayEquals(trapA.getPosition(), new int[]{7, 1});

    // randomized long tests:

    for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {
      Trap trap = new Trap();
      int id = TestHelper.generateRandomNumber(100);
      String owner = TestHelper.generateRandomString((int) (Math.random() * 10));
      trap.setID(id);
      trap.setOwner(owner);
      int x = TestHelper.generateRandomNumber(100);
      int y = TestHelper.generateRandomNumber(100);
      trap.setPosition(x, y);

      Assertions.assertEquals(trap.getOwner(), owner);
      Assertions.assertEquals(trap.getPlayerID(), id);
      Assertions.assertEquals(trap.toString(), "Trap:" + id + ":" + owner);
      Assertions.assertArrayEquals(trap.getPosition(), new int[]{x, y});

    }

  }


}
