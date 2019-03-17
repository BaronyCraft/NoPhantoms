package de.guntram.bukkit.NoPhantoms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class Settings {
    
    private static class PlayerInfo {
        boolean isEnabled;
        String lastKnownName;
    }

    private static Map<UUID, PlayerInfo> settings;
    private static File settingsFile;
    private static File settingsDir;
    
    public static boolean isEnabledFor(Player player) {
        PlayerInfo info = settings.get(player.getUniqueId());
        boolean dirty=false;
        if (info==null) {
            info=new PlayerInfo();
            info.isEnabled=false;
            info.lastKnownName=player.getName();
            dirty=true;
        } else if (!player.getName().equals(info.lastKnownName)) {
            info.lastKnownName=player.getName();
            dirty=true;
        }
        if (dirty) {
            settings.put(player.getUniqueId(), info);
            save();
        }
        return info.isEnabled;
    }
    
    public static void setEnabledFor(Player player, boolean enabled) {
        PlayerInfo info = settings.get(player.getUniqueId());
        boolean dirty=false;
        if (info==null) {
            info=new PlayerInfo();
        }
        if (!(player.getName().equals(info.lastKnownName))) {
            info.lastKnownName=player.getName();
            dirty=true;
        }
        if (enabled != info.isEnabled) {
            info.isEnabled=enabled;
            dirty=true;
        }
        if (dirty) {
            settings.put(player.getUniqueId(), info);
            save();
        }
    }

    public static void load(Main instance) {
        Type mapType = new TypeToken<HashMap<String, PlayerInfo>>(){}.getType();
        settingsDir=instance.getDataFolder();
        if (!(settingsDir.exists())) {
            settingsDir.mkdirs();
        }
        settingsFile=new File(instance.getDataFolder(), "settings.json");
        try (JsonReader reader=new JsonReader(new FileReader(settingsFile))) {
            Gson gson=new Gson();
            settings=gson.fromJson(reader, mapType);
        } catch (IOException | JsonSyntaxException ex) {
            System.err.println(ex.getClass().getName()+" when reading config; creating a fresh one");
            settings=new HashMap<>();
            save();
        }
    }
    
    public static void save() {
        try (FileWriter writer = new FileWriter(settingsFile)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(settings, writer);
        } catch (IOException ex) {
            System.err.println("Trying to save settings file "+settingsFile.getAbsolutePath()+":");
            ex.printStackTrace(System.err);
        }
    }
}
