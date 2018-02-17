package me.travi5plays.hub;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.Overridden;
import org.bukkit.entity.Player;




public class Commands implements CommandExecutor {
	HubController plugin;
	public Commands(HubController passedPlugin){
		this.plugin = passedPlugin;
	}

	@Overridden
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Player p = (Player)sender;

		if (args.length == 0) {
			if (plugin.getConfig().getBoolean("TeleportTimer") == true) {

				p.sendMessage(ChatColor.GOLD  + "[Teleporting in 10 seconds]");
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
					public void run() 
					{
						plugin.TeleportToHub(p);

					}
				}, 200L);
			}
			else {
				plugin.TeleportToHub(p);
			}
		}

		if (args.length == 1){
			if (p.hasPermission("Hub.setup")){
				if(args[0].equalsIgnoreCase("Set"))
				{
					//set hub name
					plugin.getConfig().set("Hub-World", p.getWorld().getName());
					plugin.getConfig().set("Hub-Coordinates." + ".X", p.getLocation().getBlockX());
					plugin.getConfig().set("Hub-Coordinates." + ".Y", p.getLocation().getBlockY());
					plugin.getConfig().set("Hub-Coordinates." + ".Z", p.getLocation().getBlockZ());
					plugin.getConfig().set("Hub-Coordinates." + ".Pitch", p.getLocation().getPitch());
					plugin.getConfig().set("Hub-Coordinates." + ".Yaw", p.getLocation().getYaw());
					plugin.saveConfig();
					p.sendMessage(ChatColor.GOLD + "The Hub has been set");
					plugin.reloadConfig();
					plugin.Hub = plugin.getConfig().getString("Hub-World");
					//set Hub Cords

				}
				if(args[0].equalsIgnoreCase("Reload")){
					plugin.reloadConfig();
					plugin.reloadAll();
					p.sendMessage(ChatColor.GOLD + "Config Reloaded");

				}
				if(args[0].equalsIgnoreCase("check")){
					p.sendMessage(plugin.checkHighestScore());
				}
				if(args[0].equalsIgnoreCase("update")){
					plugin.updateScoreSign(null);
				}
				if(args[0].equalsIgnoreCase("2")){
					plugin.PlayersScores.put(p.getUniqueId(), 2);
					p.sendMessage("Set to 2");
				}
				if(args[0].equalsIgnoreCase("3")){
					plugin.PlayersScores.put(p.getUniqueId(), 3);
					p.sendMessage("Set to 3");
				}
				if(args[0].equalsIgnoreCase("4")){
					plugin.PlayersScores.put(p.getUniqueId(), 4);
					p.sendMessage("Set to 4");
				}
				if(args[0].equalsIgnoreCase("1")){
					plugin.PlayersScores.put(p.getUniqueId(), 1);
					p.sendMessage("Set to 1");
				}
				if(args[0].equalsIgnoreCase("clear")){
					plugin.PlayersScores.clear();
					p.sendMessage("Cleared");
				}
				if(args[0].equalsIgnoreCase("load")){
					plugin.loadHashMap();
					p.sendMessage("loaded");
				}
				if(args[0].equalsIgnoreCase("save")){
					//plugin.saveHashMap();
					p.sendMessage("saved");
				}
				

				if(args[0].equalsIgnoreCase("allbuild") || cmd.getName().equalsIgnoreCase("build") ){
					if (plugin.AllowAllBuild == false ) {
						plugin.AllowAllBuild = true;
						p.sendMessage("Allow all build ALLOWED");

					}
					else {
						plugin.AllowAllBuild = false;
						p.sendMessage("Allow all build DENIED");
					}
				}

				if(args[0].equalsIgnoreCase("Help")){
					p.sendMessage("Set, Reload, allbuild");

				}
			}
		}
		return true;
	}
}