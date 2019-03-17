package de.guntram.bukkit.NoPhantoms;

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    
    public static Main instance;
    
    @Override 
    public void onEnable() {
        if (instance==null)
            instance=this;
        getCommand("phantoms").setTabCompleter(new PhantomsTabCompleter());
        getServer().getPluginManager().registerEvents(this, this);
        Settings.load(instance);
    }
    
    @Override
    public void onDisable() {
        Settings.save();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String commandName=command.getName();
        Location targetLocation;
        if (commandName.equals("phantoms")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Can only be used by players");
                return true;
            }
            if (args.length != 1) {
                return false;           // bukkit will display usage info
            }
            if (args[0].equals("on")) {
                Settings.setEnabledFor((Player)sender, true);
                return true;
            } else if (args[0].equals("off")) {
                Settings.setEnabledFor((Player)sender, false);
                return true;
            } else if (args[0].equals("query")) {
                sender.sendMessage("Phantom spawn is "+(Settings.isEnabledFor((Player)sender) ? "on" : "off"));
                return true;
            }
            return false;
        }
        return false;
    }
    

    @EventHandler
    public void onPhantomSpawnEvent(PhantomPreSpawnEvent event) {
        Entity entity = event.getSpawningEntity();
        if (entity instanceof Player) {
            if (!Settings.isEnabledFor((Player)entity)) {
                System.out.println("Phantom spawning for player "+entity.getName()+" disabled: suppressing");
                event.setShouldAbortSpawn(true);
            }
        }
    }
}
