package me.travi5plays.hub;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class SignListener implements Listener{
	HubController plugin;
	public SignListener(HubController passedPlugin){
		this.plugin = passedPlugin;
	}

	//Start Sign
	//TODO fix this so its not so long

	public void saveSignLoc (Block bloc, String name) {
		plugin.getConfig().set(name + ".World", bloc.getLocation().getWorld().getName());
		plugin.getConfig().set(name + ".X", bloc.getLocation().getBlockX());
		plugin.getConfig().set(name + ".Y", bloc.getLocation().getBlockY());
		plugin.getConfig().set(name + ".Z", bloc.getLocation().getBlockZ());
		plugin.saveConfig();
		plugin.reloadConfig();
		
	}

	@EventHandler
	public void signPlace(SignChangeEvent e) {
		if(e.getPlayer().hasPermission("Hub.Signs") && e.getPlayer().getWorld().getName().equalsIgnoreCase(plugin.Hub) ) {

			if (e.getLine(1).contains("[CHICKEN START]")){
				e.setLine(1, ChatColor.BLACK + "Chicken Game");
				e.setLine(2, ChatColor.GREEN + "START");
				Block bloc = e.getBlock();
				saveSignLoc(bloc, "StartSign."); 
			}
			
			if(e.getLine(1).contains("[CHICKEN HIGH]")) {
				e.setLine(1, ChatColor.BLACK + "Highest Score:");
				e.setLine(2, ChatColor.GOLD + "ABCD");
				e.setLine(3, ChatColor.BLACK + "0");
				Block bloc = e.getBlock();
				saveSignLoc(bloc, "HighScoreSign."); 
			}
			
			if(e.getLine(1).contains("[CHICKEN ROUND]")) {
				e.setLine(1, ChatColor.BLACK + "Highest/Round:");
				e.setLine(2, ChatColor.GOLD + "ABCD");
				e.setLine(3, ChatColor.BLACK + "0");
				Block bloc = e.getBlock();
				saveSignLoc(bloc, "HighRoundSign.");
			}
			
			if(e.getLine(1).contains("[CHICKEN ARROW]")) {
				e.setLine(1, ChatColor.BLACK + "ARROWS");
				e.setLine(2, ChatColor.GREEN + "x64");
				Block bloc = e.getBlock();
				saveSignLoc(bloc, "ArrowSign.");
			}

			if(e.getLine(1).contains("[CHICKEN BOW]")) {
				e.setLine(1, ChatColor.BLACK + "BOW");
				e.setLine(2, ChatColor.GREEN + "x1");
				Block bloc = e.getBlock();
				saveSignLoc(bloc, "BowSign.");
			}

			if(e.getLine(1).contains("[CHICKEN HITS]")) {
				e.setLine(0, ChatColor.BLACK + "Total Hits!");
				
				e.setLine(1, "name");
				e.setLine(2, "points");
				
				e.setLine(3, ChatColor.BLACK + "Check Yours?");
				//TODO check score
				Block bloc = e.getBlock();
				saveSignLoc(bloc, "ScoreSign.");
			}
		}
	}

	@EventHandler
	public void signInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			Player p = e.getPlayer();

			if (((block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN) || (block.getType() == Material.SIGN)) && (p.getWorld().getName().equalsIgnoreCase(plugin.Hub)))
			{
				Sign sign = (Sign)block.getState();
				//p.sendMessage("Sign Found");

				if(sign.getLine(1).contains("Chicken Game") && sign.getLine(2).contains(ChatColor.GREEN + "START")) {
					if (plugin.chickenGameRun == false){
						plugin.startChickenGame(p);
						//START THE GAME
					}
				}
				if(sign.getLine(1).contains("ARROWS") && sign.getLine(2).contains(ChatColor.GREEN + "x64")) {
					if (plugin.allowWeaponsbuy == true){
						p.getInventory().addItem(new ItemStack(Material.ARROW, 64));
						plugin.addPlayer(p);
					}
					else{
						p.sendMessage("Please start the game");
					}
				}
				if(sign.getLine(1).contains("BOW") && sign.getLine(2).contains(ChatColor.GREEN + "x1")) {
					if (plugin.allowWeaponsbuy == true){
						p.getInventory().addItem(new ItemStack(Material.BOW, 1));
						plugin.addPlayer(p);
					}
					else{
						p.sendMessage("Please start the game");
					}
				}
				
				
				if(sign.getLine(0).contains("Total Hits!")) {
					plugin.updateScoreSign(p);
				}

			}
		}
	}


}
