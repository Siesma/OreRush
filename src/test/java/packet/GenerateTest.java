package packet;

import game.packet.AbstractPacket;
import game.packet.packets.*;
import game.server.ServerConstants;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class GenerateTest extends TestCase {


    @Test
    public void testGetPacket() {

        Broadcast realBroadcast = new Broadcast();
        Broadcast syntheticBroadcast = (Broadcast) AbstractPacket.getPacketByName("Broadcast");
        Move realMove = new Move();
        Move syntheticMove = (Move) AbstractPacket.getPacketByName("Move");
        InitNickname realInitNickname = new InitNickname();
        InitNickname syntheticInitNickname = (InitNickname) AbstractPacket.getPacketByName("InitNickname");
        Update realUpdate = new Update();
        Update syntheticUpdate = (Update) AbstractPacket.getPacketByName("Update");
        StartGame realStartGame = new StartGame();
        StartGame syntheticStartGame = (StartGame) AbstractPacket.getPacketByName("StartGame");
        Awake realAwake = new Awake();
        Awake syntheticAwake = (Awake) AbstractPacket.getPacketByName("Awake");
        JoinLobby realJoinLobby = new JoinLobby();
        JoinLobby syntheticJoinLobby = (JoinLobby) AbstractPacket.getPacketByName("JoinLobby");
        Timeout realTimeout = new Timeout();
        Timeout syntheticTimeout = (Timeout) AbstractPacket.getPacketByName("Timeout");
        Score realScore = new Score();
        Score syntheticScore = (Score) AbstractPacket.getPacketByName("Score");
        LeaveLobby realLeaveLobby = new LeaveLobby();
        LeaveLobby syntheticLeaveLobby = (LeaveLobby) AbstractPacket.getPacketByName("LeaveLobby");
        CreateLobby realCreateLobby = new CreateLobby();
        CreateLobby syntheticCreateLobby = (CreateLobby) AbstractPacket.getPacketByName("CreateLobby");
        Winner realWinner = new Winner();
        Winner syntheticWinner = (Winner) AbstractPacket.getPacketByName("Winner");
        HighScore realHighScore = new HighScore();
        HighScore syntheticHighScore = (HighScore) AbstractPacket.getPacketByName("HighScore");
        Success realSuccess = new Success();
        Success syntheticSuccess = (Success) AbstractPacket.getPacketByName("Success");
        Chat realChat = new Chat();
        Chat syntheticChat = (Chat) AbstractPacket.getPacketByName("Chat");
        Nickname realNickname = new Nickname();
        Nickname syntheticNickname = (Nickname) AbstractPacket.getPacketByName("Nickname");
        Whisper realWhisper = new Whisper();
        Whisper syntheticWhisper = (Whisper) AbstractPacket.getPacketByName("Whisper");
        ChatLobby realChatLobby = new ChatLobby();
        ChatLobby syntheticChatLobby = (ChatLobby) AbstractPacket.getPacketByName("ChatLobby");
        Close realClose = new Close();
        Close syntheticClose = (Close) AbstractPacket.getPacketByName("Close");
        Connect realConnect = new Connect();
        Connect syntheticConnect = (Connect) AbstractPacket.getPacketByName("Connect");

        Assertions.assertEquals(realBroadcast.getClass(), syntheticBroadcast.getClass());
        Assertions.assertEquals(realMove.getClass(), syntheticMove.getClass());
        Assertions.assertEquals(realInitNickname.getClass(), syntheticInitNickname.getClass());
        Assertions.assertEquals(realUpdate.getClass(), syntheticUpdate.getClass());
        Assertions.assertEquals(realStartGame.getClass(), syntheticStartGame.getClass());
        Assertions.assertEquals(realAwake.getClass(), syntheticAwake.getClass());
        Assertions.assertEquals(realJoinLobby.getClass(), syntheticJoinLobby.getClass());
        Assertions.assertEquals(realTimeout.getClass(), syntheticTimeout.getClass());
        Assertions.assertEquals(realScore.getClass(), syntheticScore.getClass());
        Assertions.assertEquals(realLeaveLobby.getClass(), syntheticLeaveLobby.getClass());
        Assertions.assertEquals(realCreateLobby.getClass(), syntheticCreateLobby.getClass());
        Assertions.assertEquals(realWinner.getClass(), syntheticWinner.getClass());
        Assertions.assertEquals(realHighScore.getClass(), syntheticHighScore.getClass());
        Assertions.assertEquals(realSuccess.getClass(), syntheticSuccess.getClass());
        Assertions.assertEquals(realChat.getClass(), syntheticChat.getClass());
        Assertions.assertEquals(realNickname.getClass(), syntheticNickname.getClass());
        Assertions.assertEquals(realWhisper.getClass(), syntheticWhisper.getClass());
        Assertions.assertEquals(realChatLobby.getClass(), syntheticChatLobby.getClass());
        Assertions.assertEquals(realClose.getClass(), syntheticClose.getClass());
        Assertions.assertEquals(realConnect.getClass(), syntheticConnect.getClass());
    }

    @Test
    public void testReplaceIndicators() {
        String testA = ((char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE) + "Some_Random:Text-That;Utilizes:Known$Special€Characters" + ((char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE);
        String testB = ((char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE) + "some#Random?Text`That/Utilizes>Known|Special^Characters" + ((char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE);
        String testC = ((char) ServerConstants.DEFAULT_PACKET_STARTING_MESSAGE) + "Some=Random~Text%That!Utilizes&Known}Special[Characters" + ((char) ServerConstants.DEFAULT_PACKET_ENDING_MESSAGE);


        Assertions.assertEquals(AbstractPacket.replaceIndicatorChars(testA), "Some_Random:Text-That;Utilizes:Known$Special€Characters");
        Assertions.assertEquals(AbstractPacket.replaceIndicatorChars(testB), "some#Random?Text`That/Utilizes>Known|Special^Characters");
        Assertions.assertEquals(AbstractPacket.replaceIndicatorChars(testC), "Some=Random~Text%That!Utilizes&Known}Special[Characters");

    }


    @Test
    public void testSplitMessage() {
        String testA = "Some:Random:Text:For:Testing:The:Split:Function";
        String testB = "Some" + ((char) ServerConstants.DEFAULT_PACKET_SPACER) + "Random" + ((char) ServerConstants.DEFAULT_PACKET_SPACER) + "Text" + ((char) ServerConstants.DEFAULT_PACKET_SPACER) + "For" + ((char) ServerConstants.DEFAULT_PACKET_SPACER) + "Testing" + ((char) ServerConstants.DEFAULT_PACKET_SPACER) + "The" + ((char) ServerConstants.DEFAULT_PACKET_SPACER) + "Split" + ((char) ServerConstants.DEFAULT_PACKET_SPACER) + "Function";
        Assertions.assertArrayEquals(AbstractPacket.splitMessageBySpacer(testA, ":"), testA.split(":"));
        Assertions.assertArrayEquals(AbstractPacket.splitMessageBySpacer(testB), testB.split("" + ((char) ServerConstants.DEFAULT_PACKET_SPACER)));

    }

    @Test
    public void testRemoveElement() {
        String[] testA = {
                "a",
                "b",
                "c",
                "d",
                "e"
        };
        String[] testB = {
                "1",
                "2",
                "3",
                "4",
                "5"
        };

        testA = AbstractPacket.removeFirstElement(testA);
        Assertions.assertEquals(testA.length, 4);
        Assertions.assertEquals(testA[0], "b");
        testA = AbstractPacket.removeFirstElement(testA);
        Assertions.assertEquals(testA[0], "c");
        Assertions.assertEquals(testA.length, 3);
        testA = AbstractPacket.removeFirstElement(testA);
        Assertions.assertEquals(testA[0], "d");
        Assertions.assertEquals(testA.length, 2);



        testB = AbstractPacket.removeFirstElement(testB);
        Assertions.assertEquals(testB.length, 4);
        Assertions.assertEquals(testB[0], "2");
        testB = AbstractPacket.removeFirstElement(testB);
        Assertions.assertEquals(testB[0], "3");
        Assertions.assertEquals(testB.length, 3);
        testB = AbstractPacket.removeFirstElement(testB);
        Assertions.assertEquals(testB[0], "4");
        Assertions.assertEquals(testB.length, 2);


        // randomized long tests:
        String[] longTest = new String[(int) (Math.random() * 1000)];
        for(int i = 0; i < longTest.length; i++) {
            longTest[i] = generateRandomString((int) (Math.random() * 50));
        }
        for(int i = Math.min(longTest.length - 10, (int) (Math.random() * 500)); i > 0; i--) {
            int curLen = longTest.length;
            String nextElement = longTest[1];
            longTest = AbstractPacket.removeFirstElement(longTest);
            Assertions.assertEquals(longTest[0], nextElement);
            Assertions.assertEquals(longTest.length, curLen - 1);

        }

    }

    public static String generateRandomString(int len) {
        byte[] array = new byte[len];
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

}
