/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.bukkit.NoPhantoms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

/**
 *
 * @author gbl
 */
public class PhantomsTabCompleter implements TabCompleter {
    
    static final List<String> commands=Arrays.asList("on", "off", "query");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions=new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], commands, completions);
        if (args.length == 1) {
            for (String group: commands) {
                if (StringUtil.startsWithIgnoreCase(group, args[0]) && sender.hasPermission("gotoworldgroup.goto."+group)) {
                    completions.add(group);
                }
            }
        }
        return completions;
    }
}
