package me.travi5plays.hub;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Modifiers implements Listener{
	HubController plugin;
	public Modifiers(HubController passedPlugin){
		this.plugin = passedPlugin;
	}
	
	//Pressure Pad
	@EventHandler
	public void onPlayerWalk(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if ((plugin.Hub != null) && (plugin.LauchPadForce > 1) && (e.getAction() == Action.PHYSICAL) && 
				(e.getClickedBlock().getType() == Material.STONE_PLATE) && (p.getWorld().getName().equalsIgnoreCase(plugin.Hub)) )
		{
			p.teleport(p.getLocation().add(new Vector(0, 2, 0))); //raise the player 2 blocks and throw them
			p.setVelocity(new Vector(p.getVelocity().getX(), 1.0, p.getVelocity().getZ()));
			p.setVelocity(p.getLocation().getDirection().multiply(plugin.LauchPadForce));
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1, 2);
			return;
		}
	}
	
	//Add effect to player for fast run
		@EventHandler
		public void onPlayerMove(PlayerMoveEvent e) {
			Player p = e.getPlayer();
			if (p.getWorld().getName().equalsIgnoreCase(plugin.Hub) && plugin.WalkSpeed >= 0 && !p.hasPotionEffect(PotionEffectType.SPEED)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, plugin.WalkSpeed));
			}

			return;
		}
		//Remove Speed Boost
		@EventHandler
		public void  onPlayerTeleport(PlayerTeleportEvent e){
			Player p = e.getPlayer();
			if (p.getWorld().getName().equalsIgnoreCase(plugin.Hub)) {
				p.removePotionEffect(PotionEffectType.SPEED);

			}
		}
	
	
	
	
	
	
	
}