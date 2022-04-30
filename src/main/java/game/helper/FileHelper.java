package game.helper;

import game.datastructures.*;
import game.packet.packets.*;
import game.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Locale;

public class FileHelper {
    public static final Logger logger = LogManager.getLogger(Server.class);

    private final HashMap<String, HashMap<String, Object>> objectMap;

    public FileHelper() {
        objectMap = new HashMap<>();
        initialize();
    }

    /**
     * Empties and then fills the (now) empty "objectMap" with a new instance of each Object that can maybe be create in runtime.
     */
    public void initialize() {
        objectMap.clear();
        /*
         * Initializes the packetmap.
         */
        HashMap<String, Object> packetHashMap = new HashMap<>();
        packetHashMap.put("awake", new Awake());
        packetHashMap.put("connect", new Connect());
        packetHashMap.put("leavelobby", new LeaveLobby());
        packetHashMap.put("chatlobby", new ChatLobby());
        packetHashMap.put("initnickname", new InitNickname());
        packetHashMap.put("success", new Success());
        packetHashMap.put("joinlobby", new JoinLobby());
        packetHashMap.put("update", new Update());
        packetHashMap.put("broadcast", new Broadcast());
        packetHashMap.put("whisper", new Whisper());
        packetHashMap.put("move", new Move());
        packetHashMap.put("nickname", new Nickname());
        packetHashMap.put("createlobby", new CreateLobby());
        packetHashMap.put("score", new Score());
        packetHashMap.put("close", new Close());
        packetHashMap.put("timeout", new Timeout());
        packetHashMap.put("chat", new Chat());
        packetHashMap.put("startgame", new StartGame());
        packetHashMap.put("winner", new Winner());
        packetHashMap.put("highscore", new HighScore());
        objectMap.put(MapType.Packets.name().toLowerCase(Locale.ROOT), packetHashMap);
        /*
         * Initializes the gameObjectMap.
         */
        HashMap<String, Object> gameObjectMap = new HashMap<>();
        gameObjectMap.put("nothing", new Nothing());
        gameObjectMap.put("ore", new Ore());
        gameObjectMap.put("radar", new Radar());
        gameObjectMap.put("trap", new Trap());
        gameObjectMap.put("robot", new Robot());
        objectMap.put(MapType.GameObjects.name().toLowerCase(Locale.ROOT), gameObjectMap);
    }

    /**
     * This function returns a new object by name.
     * This will requires the typeOfMap and the key to be in lowercase, although the function already
     * falls back to the lowercase value.
     * There are currently two sets of objects than can be accessed:
     * Those are the individual packets and the individual gameobjects.
     *
     * @param typeOfMap is a reference to which HashMap the key is supposed to be inside of.
     * @param key       is the key which should be returned from the relative HashMap
     * @return a new instance of the given Object
     */
    public Object createNewInstanceFromName(MapType typeOfMap, String key) {
        initialize();
        Object obj = objectMap.get(typeOfMap.getHashName()).get(key.toLowerCase(Locale.ROOT));
    if(obj == null) {
      logger.debug("The object  \"" + key + "\" is missing from the \"" + typeOfMap + "\" MapType.");
    }
        return obj;
    }

    public HashMap<String, HashMap<String, Object>> getObjectMap() {
        return objectMap;
    }
}
