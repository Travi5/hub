package me.travi5plays.hub;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.weather.WeatherChangeEvent;


public class WorldProtection implements Listener{
	HubController plugin;
	public WorldProtection(HubController passedPlugin){
		this.plugin = passedPlugin;
	}

	//No Build
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if ((p.getWorld().getName().equalsIgnoreCase(plugin.Hub)) && p != null && (!p.hasPermission("Hub.Builder") && (plugin.AllowAllBuild == false)))
		{
			e.setCancelled(true);
		}
	}
	//No Block Place
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		if ((p.getWorld().getName().equalsIgnoreCase(plugin.Hub)) && p != null && (!p.hasPermission("Hub.Builder") && (plugin.AllowAllBuild == false)))
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemDrop (PlayerDropItemEvent e) {
		if (e.getPlayer() instanceof Player){
			Player p = e.getPlayer();
			if ((p.getWorld().getName().equalsIgnoreCase(plugin.Hub)) && p != null && (plugin.AllowAllBuild == false))
			{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void vehicleDestroy(VehicleDamageEvent e) {
		Vehicle v = e.getVehicle();
		Entity p = e.getAttacker();
		if ((v.getWorld().getName().equalsIgnoreCase(plugin.Hub)) && v != null && (!p.hasPermission("Hub.Builder") && (plugin.AllowAllBuild == false)))
		{
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void itemFrameBreak (HangingBreakEvent e) {
		if (e.getEntity().getWorld().getName().equalsIgnoreCase(plugin.Hub) && (e.getCause().toString().equals("ENTITY") || e.getCause().toString().equals("EXPLOSION"))) {
			e.setCancelled(true);
		}

	}
	
	
	//TODO item frames rotation

	@EventHandler
	public void creatureSpawning(CreatureSpawnEvent e) {
		Entity p = e.getEntity();
		if (p.getWorld().getName().equalsIgnoreCase(plugin.Hub) && e.getSpawnReason() != SpawnReason.CUSTOM)
		{
			e.getEntity().remove();
			//Delete the mob
		}

	}

	//Weather Lock
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if (e.toWeatherState() && e.getWorld().getName().equalsIgnoreCase(plugin.Hub)) { 
			//Cancel the event if DenyWeatherChange is set to true
			e.setCancelled(plugin.DenyWeatherChange);

		}
	}

}