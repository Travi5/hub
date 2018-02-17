package me.travi5plays.hub;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


public class CombatListener implements Listener{
	HubController plugin;
	public CombatListener(HubController passedPlugin){
		this.plugin = passedPlugin;
	}


	//Each Death of ChickenGame adds one point to player who killed
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent  e){	
		if(e.getCause() == DamageCause.PROJECTILE) {
			Projectile projectile = (Projectile) e.getDamager();

			//Only looking for players projectile's
			if (projectile.getShooter() instanceof Player) {

				Player shooter = (Player) projectile.getShooter();
				if (shooter.getWorld().getName().equalsIgnoreCase(plugin.Hub)) {
					//Shooter is in the HUB world


					if (projectile instanceof Arrow
							&& e.getEntity().getCustomName() != null
							&& e.getEntity().getCustomName().equalsIgnoreCase("ChickenGame")
							&& shooter != null) 
					{

						if (plugin.chickenGameRun == true) {
							shooter.getWorld().playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 1);

							//ADD players hit point
							if (plugin.PlayersScores.get(shooter.getUniqueId()) == null) {
								plugin.PlayersScores.put(shooter.getUniqueId(), 1);
								//DELETE
								shooter.sendMessage("You have been added");
							}
							else { //add point
								plugin.PlayersScores.put(shooter.getUniqueId(), plugin.PlayersScores.get(shooter.getUniqueId()) + 1);
							}

							//Scores to add to file
							if (plugin.playerMap.get(shooter.getName()) != null)
							{
								//Chicken Game hit registered
								//shooter.getWorld().playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 1);
								plugin.playerMap.put(shooter.getName(),plugin.playerMap.get(shooter.getName()) + 1);
								shooter.sendMessage(ChatColor.GOLD + "HIT! Points: " + plugin.playerMap.get(shooter.getName()));
							}
							else {
								plugin.playerMap.put(shooter.getName(),1);
								shooter.sendMessage(ChatColor.GOLD + "You have entered the round! Points: " + plugin.playerMap.get(shooter.getName()));

							}
						}
						else //Game hasn't started
						{
							shooter.sendMessage("Please start the game");
							e.getEntity().getWorld().playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 1);
							//this shouldn't really happen as players shouldn't have arrows
						}
					}

					if (projectile instanceof Snowball 
							&& e.getEntity().getCustomName() != null
							&& e.getEntity().getCustomName().equalsIgnoreCase("ChickenGame")
							&& shooter != null)  {
						shooter.sendMessage(ChatColor.GREEN + "[ChickenGame] BRRRR!! Thats cold!");
					}

					if (projectile instanceof Snowball
							&& shooter instanceof Player
							&& e.getEntity() instanceof Player
							&& !e.getEntity().hasMetadata("NPC")) {
						Player victim = (Player) e.getEntity();
						plugin.knockBack(shooter,victim,2);
					}
					//Travi5 NPC
					if (projectile instanceof Snowball
							&& shooter instanceof Player
							&& e.getEntity() instanceof Player
							&& e.getEntity().hasMetadata("NPC")
							&& e.getEntity().getCustomName() != null
							&& e.getEntity().getCustomName().equalsIgnoreCase("Travi5")) {
						String quotes = "Please dont do that!:Thats cold!:Sometimes I wish I could fight back:I'm telling on you:Please cut that out:I have snow down my shirt now:I may be only an NPC but that doesn't mean I dont have feelings:Where are you getting all these snowballs from?";
						String [] quote = quotes.split(":");
						Random rand = new Random();
						int  n = rand.nextInt(quote.length);
						shooter.sendMessage("[Travi5] " + quote[n]);	
					}

					//Qyrun
					if (projectile instanceof Arrow
							&& shooter instanceof Player
							&& e.getEntity() instanceof Player
							&& e.getEntity().hasMetadata("NPC")
							&& e.getEntity().getCustomName() != null
							&& e.getEntity().getCustomName().equalsIgnoreCase("Qyrun")) {
						plugin.clearInv(shooter);
						shooter.getPlayer().setHealth(0.0D);
						Bukkit.broadcastMessage(ChatColor.RED + shooter.getName() + " was killed by Qyrun's NPC");
						String quotes = "Get gud:Scrub:You really think you can take me on? HA!:HAHA!:Worth it:I kill everything you love:Nice try:MAHAHAHAHAHAHA:YOLO:Nice Attempt";
						String [] quote = quotes.split(":");
						Random rand = new Random();
						int  n = rand.nextInt(quote.length);
						Bukkit.broadcastMessage(ChatColor.WHITE + "[Qyrun NPC] " + quote[n]);	
					}


				}
			}
		}
	}



	@EventHandler
	public void damagePlayer(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			//Player p = ((Player) e).getPlayer();

			if (e.getEntity().getWorld().getName().equalsIgnoreCase(plugin.Hub) && plugin.Hub != null && (e.getCause() != DamageCause.CUSTOM || e.getCause() != DamageCause.SUICIDE)){
				e.setCancelled(true);
			}
		}
	}

	//De-spawn arrows
	@EventHandler
	public void projectileHit(ProjectileHitEvent e){
		if (e.getEntity().getWorld().getName().equals(plugin.Hub)){
			if (e.getEntity().getType() == EntityType.ARROW){
				e.getEntity().remove();
			}
		}
	}


}