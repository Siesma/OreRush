package game.packet.packets;

import game.packet.AbstractPacket;
import game.server.ClientThread;
import game.server.Server;
import game.server.ServerSettings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServerSettingsPacket extends AbstractPacket {


    public ServerSettingsPacket() {
        super("help", new String[] {
                "^NumberOfRobots:$"
        }, "response");
    }

    @Override
    public String encodeWithContent(String... content) {

        return null;
    }

    @Override
    public String encode() {
        return null;
    }

    @Override
    public void decode(Object parent, String message) {
        if (parent instanceof ClientThread) {
            ClientThread obj = (ClientThread) parent;
            for (String s : AbstractPacket.splitMessageBySpacer(message)) {
                try {
                    String name = s.split(":")[0];
                    double val = Integer.parseInt(s.split(":")[1]);
                    ServerSettings settings = obj.getConnectedLobby().getServerSettings();
                    Method setter = settings.getClass().getDeclaredMethod(getNameOfGetter(name, "set", getAllMethodsOfObject(settings.getClass())));
                    setter.invoke(val);
                } catch (Exception e) {
                    logger.error("Some error occurred tried to read the slider variables for the serversettings.", e);
                }
            }

        }
    }

    public Method[] getAllMethodsOfObject (Class<?> clazz) {
        return clazz.getDeclaredMethods();
    }

    private String getNameOfGetter (String similarName, String preFix, Method[] methodsOfObject) {
        for(Method m : methodsOfObject) {
            System.out.println(m.getName());
            if(m.getName().toLowerCase(Locale.ROOT).matches(preFix + similarName.toLowerCase(Locale.ROOT))) {
                return m.getName();
            }
        }
        return "NONE";
    }

}
