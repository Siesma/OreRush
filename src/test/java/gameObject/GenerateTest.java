package gameObject;

import game.datastructures.*;
import game.helper.FileHelper;
import game.helper.MapType;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GenerateTest extends TestCase {

  @Test
  public void testGetGameObject() {

    Radar realRadar = new Radar();
    Radar syntheticRadar = (Radar) (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, "Radar");


    Robot realRobot = new Robot();
    Robot syntheticRobot = (Robot) (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, "Robot");


    Ore realOre = new Ore();
    Ore syntheticOre = (Ore) (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, "Ore");


    Trap realTrap = new Trap();
    Trap syntheticTrap = (Trap) (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, "Trap");


    Nothing realNothing = new Nothing();
    Nothing syntheticNothing = (Nothing) (new FileHelper()).createNewInstanceFromName(MapType.GameObjects, "Nothing");


    Assertions.assertEquals(realRadar.getClass(), syntheticRadar.getClass());
    Assertions.assertEquals(realRobot.getClass(), syntheticRobot.getClass());
    Assertions.assertEquals(realOre.getClass(), syntheticOre.getClass());
    Assertions.assertEquals(realTrap.getClass(), syntheticTrap.getClass());
    Assertions.assertEquals(realNothing.getClass(), syntheticNothing.getClass());


  }


}
