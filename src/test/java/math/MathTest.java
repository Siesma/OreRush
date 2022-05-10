package math;

import game.helper.MathHelper;
import game.helper.TestHelper;
import game.server.ServerSettings;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MathTest extends TestCase {

    @Test
    public void testNextMove() {
        ServerSettings ss = new ServerSettings();

        int[] xyA = {3, 6};
        int[] destA = {10, 6};
        Assertions.assertArrayEquals(MathHelper.getNextMove(xyA, destA, ss), new int[]{7, 6});


        // randomized long tests:
        for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {
            int[] xy = new int[2];
            xy[0] = TestHelper.generateRandomNumber(100);
            xy[1] = TestHelper.generateRandomNumber(100);
            int[] dest = new int[2];
            dest[0] = TestHelper.generateRandomNumber(100);
            dest[1] = TestHelper.generateRandomNumber(100);
            int[] nextMove = MathHelper.getNextMove(xy, dest, ss);
            Assertions.assertTrue(MathHelper.absoluteCellDistance(xy, nextMove) <= ss.getMaxAllowedMoves().getVal());
        }

    }

    @Test
    public void testDistance() {
        ServerSettings ss = new ServerSettings();

        int[] xyA = {3, 6};
        int[] destA = {10, 6};
        Assertions.assertEquals(MathHelper.absoluteCellDistance(xyA, destA), 7);


        // randomized long tests:
        // although this feels weird having to write down because the function cannot be simplified and therefor exists twice.
        for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {
            int[] xy = new int[2];
            xy[0] = TestHelper.generateRandomNumber(100);
            xy[1] = TestHelper.generateRandomNumber(100);
            int[] dest = new int[2];
            dest[0] = TestHelper.generateRandomNumber(100);
            dest[1] = TestHelper.generateRandomNumber(100);
            Assertions.assertEquals(MathHelper.absoluteCellDistance(xy, dest), Math.abs(xy[0] - dest[0]) + Math.abs(xy[1] - dest[1]));
        }
    }


    @Test
    public void testClamp() {

        int xA = 10;

        int minA = 3;
        int maxA = 6;

        Assertions.assertEquals(MathHelper.clamp(xA, minA, maxA), maxA);

        // randomized long tests:
        for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {
            int x = TestHelper.generateRandomNumber(1000);
            int min = TestHelper.generateRandomNumber(300) + 1;
            int max = min + TestHelper.generateRandomNumber(700);
            Assertions.assertTrue(MathHelper.clamp(x, min, max) / max <= 1);

        }


    }

    @Test
    public void testInBounds() {
        int xA = 5;
        int yA = 1;

        int bMinXA = 0;
        int bMaxXA = 8;
        int bMinYA = 0;
        int bMaxYA = 2;

        Assertions.assertTrue(MathHelper.isInBounds(xA, yA, bMinXA, bMaxXA, bMinYA, bMaxYA));
        Assertions.assertTrue(MathHelper.isInBounds(new int[]{xA, yA}, new int[]{bMinXA, bMinYA}, new int[]{bMaxXA, bMaxYA}));

        // randomized long tests:
        for (int i = TestHelper.generateRandomNumber(1000); i > 0; i--) {

            int x = TestHelper.generateRandomNumber(1000);
            int y = TestHelper.generateRandomNumber(1000);
            int bMinX = TestHelper.generateRandomNumber(300);
            int bMaxX = bMinX + TestHelper.generateRandomNumber(700);
            int bMinY = TestHelper.generateRandomNumber(300);
            int bMaxY = bMinY + TestHelper.generateRandomNumber(700);
            Assertions.assertEquals(MathHelper.isInBounds(x, y, bMinX, bMaxX, bMinY, bMaxY),(x >= bMinX && x < bMaxX && y >= bMinY && y < bMaxY));
            Assertions.assertEquals(MathHelper.isInBounds(new int[] {x, y}, new int[] {bMinX, bMinY}, new int[] {bMaxX, bMaxY}),(x >= bMinX && x < bMaxX && y >= bMinY && y < bMaxY));


        }
    }

}

