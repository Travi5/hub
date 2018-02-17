package me.travi5plays.hub;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.bukkit.entity.Firework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
//TODO
//Add point counter then write to config after round end
//Change arrow sign and bow sign to give the value on line 3 instead of programmed
//Change Start Sign to Started when game started

public class HubController extends JavaPlugin
implements Listener{
	public String Hub; //Map World Name

	public int LauchPadForce;
	public int WalkSpeed;
	public boolean DenyWeatherChange = true;
	public boolean AllowAllBuild = false;

	//Chicken Game
	public boolean chickenGameRun = false;
	public boolean allowWeaponsbuy = false;
	Map<String, Integer> playerMap = new HashMap<String, Integer>();

	Map<UUID, Integer> PlayersScores = new HashMap<UUID, Integer>();
	Map<String, Long> cooldowns = new HashMap<String, Long>();
	int cooldownSpamTime = 200; //in milli


	File PointsYmlFile;
	FileConfiguration PointsFile;


	public HubController() {
		Hub = getConfig().getString("Hub-World");
		LauchPadForce = getConfig().getInt("Launch-Pad-Force");
		WalkSpeed = getConfig().getInt("Walking-Speed");
		DenyWeatherChange = getConfig().getBoolean("Weather-Lock");
	}

	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);

		getConfig().options().copyDefaults(true);

		PointsYmlFile = new File(getDataFolder()+"/PointsFile.yml");
		PointsFile = YamlConfiguration.loadConfiguration(PointsYmlFile);

		this.getCommand("hub").setExecutor(new Commands (this));
		this.getCommand("lobby").setExecutor(new Commands (this));

		this.getServer().getPluginManager().registerEvents(new CombatListener(this), this);
		this.getServer().getPluginManager().registerEvents(new WorldProtection(this), this);
		this.getServer().getPluginManager().registerEvents(new Modifiers(this), this);
		this.getServer().getPluginManager().registerEvents(new SignListener(this), this);

		LauchPadForce = getConfig().getInt("Launch-Pad-Force");
		WalkSpeed = getConfig().getInt("Walking-Speed");
		DenyWeatherChange = getConfig().getBoolean("Weather-Lock");
		saveDefaultConfig();

		List <String> s = PointsFile.getStringList("Scores");
		for (String str : s) {
			String[] words  = str.split(":");
			UUID uniqueId = UUID.fromString(words[0]);
			int Score = Integer.parseInt(words[1]);
			PlayersScores.put(uniqueId,Score);
		}


		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[HUB by Travi5Plays Sucessfully Enabled]");
	}

	public void onDisable() {
		//save hashmap
		PointsFile.set("Scores", null); //Clear it
		List <String> s = PointsFile.getStringList("Scores");
		for (Entry<UUID, Integer> PlayerScores  : PlayersScores.entrySet()) {
			s.add(PlayerScores.getKey() + ":" + PlayerScores.getValue());
		}
		PointsFile.set("Scores", s);
		savePointsFile();
	}

	public void loadHashMap () {
		List <String> s = PointsFile.getStringList("Scores");
		for (String str : s) {
			String[] words  = str.split(":");
			UUID uniqueId = UUID.fromString(words[0]);
			int Score = Integer.parseInt(words[1]);
			PlayersScores.put(uniqueId,Score);
		}

	}

	public String checkHighestScore() {
		int Score = 0;
		UUID uuid = null;
		String name= "";

		for (Entry<UUID, Integer> PlayerScores  : PlayersScores.entrySet()) {
			int playersScore = PlayerScores.getValue();
			if (playersScore > Score) {
				uuid = PlayerScores.getKey();
				Score = PlayerScores.getValue();
				if (Bukkit.getPlayer(uuid) != null) {
					name = Bukkit.getPlayer(uuid).getName();
				}
				else {
					name = Bukkit.getOfflinePlayer(uuid).getName();
				}

			}
		}

		return name + ":" + Integer.toString(Score);
	}



	//TODO add to end round
	public void updateScoreSign(Player p) {
		String type = "ScoreSign.";
		if (getConfig().getString(type + ".World") != null) {
			World  w = Bukkit.getServer().getWorld(getConfig().getString((type + ".World")));
			int x = getConfig().getInt(type + ".X");
			int y = getConfig().getInt(type + ".Y");
			int z = getConfig().getInt(type + ".Z");
			Block bloc = w.getBlockAt(x,y,z);

			if (bloc.getType() == Material.SIGN_POST || bloc.getType() == Material.WALL_SIGN || (bloc.getType() == Material.SIGN)) {
				Sign sign = (Sign)bloc.getState();

				if (p == null) {
					String scoreData = checkHighestScore();
					if (scoreData != null) {
						String[] data = scoreData.split(":");
						if (data[0] != null) {
							sign.setLine(0, ChatColor.BLACK + "Total Hits!");
							sign.setLine(1,ChatColor.GREEN + data[0]);
							sign.setLine(2,data[1]);
							sign.setLine(3, ChatColor.BLACK + "Check Yours?");
							sign.update(true);
						}
					}
				}
				else {
					if (p != null) {
						sign.setLine(0, ChatColor.BLACK + "Your Score");
						sign.setLine(1,ChatColor.GREEN + p.getName());
						sign.setLine(2,PlayersScores.get(p.getUniqueId()).toString());
						sign.setLine(3, ChatColor.BLACK + "Check Yours?");
						sign.update(true);
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
							public void run() 
							{
								updateScoreSign(null);
							}
						}, 2*20);


					}
				}
			}
		}
	}

	public void savePointsFile() {
		try {
			PointsFile.save(PointsYmlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadAll() {
		LauchPadForce = getConfig().getInt("Launch-Pad-Force");
		WalkSpeed = getConfig().getInt("Walking-Speed");
		DenyWeatherChange = getConfig().getBoolean("Weather-Lock");
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity(); //TODO
			if (p.getWorld().getName().equalsIgnoreCase(Hub))
			{
				e.setCancelled(true);
			}
		}
	}

	public void signUpdater(String type,String Player, int points) {	
		//High Score Sign
		//debug Bukkit.broadcast("Sign Code looking to update","Hub.setup");
		//TODO add highest code here
		if (getConfig().getString(type + ".World") != null) {
			World  w = Bukkit.getServer().getWorld(getConfig().getString((type + ".World")));
			int x = getConfig().getInt(type + ".X");
			int y = getConfig().getInt(type + ".Y");
			int z = getConfig().getInt(type + ".Z");
			Block bloc = w.getBlockAt(x,y,z);
			if (bloc.getType() == Material.SIGN_POST || bloc.getType() == Material.WALL_SIGN || (bloc.getType() == Material.SIGN)) {
				Sign sign = (Sign)bloc.getState();
				sign.setLine(2, Player);
				String pointString = Integer.toString(points);
				sign.setLine(3, pointString);
				sign.update(true);
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if (p.getWorld().getName().equalsIgnoreCase(Hub)){ //Just double check world!
			//clearInv(p);
			giveInventory(p);
		}
	}
	//clear when teleport back into world
	@EventHandler
	public void onPlayerTeleportEvent(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		if (p.getWorld().getName().equalsIgnoreCase(Hub)){ //Just double check world!
			//clearInv(p);
			giveInventory(p);
		}
	}
	//Has game started?
	public void startChickenGame(Player p){
		playerMap.clear();
		allowWeaponsbuy = true;
		if (chickenGameRun == true){
			p.sendMessage("Sorry, a game is already in progress");
			return;
		}
		else {
			chickenGameRun = true;	
			World  w = Bukkit.getServer().getWorld(getConfig().getString(("StartSign" + ".World")));
			int x = getConfig().getInt("StartSign" + ".X");
			int y = getConfig().getInt("StartSign" + ".Y");
			int z = getConfig().getInt("StartSign" + ".Z");
			Location signLoc =  new Location ((World) w, x, y, z);
			for (int i = 0; i < 4; i++) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
					public void run()
					{
						signLoc.getWorld().playSound(signLoc, Sound.BLOCK_NOTE_BELL, 1, 1);
					}
				}, (20 * (i + 1)));
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				public void run() 
				{	//If player accepted
					addPlayers (signLoc);
					signLoc.getWorld().playSound(signLoc, Sound.ENTITY_CHICKEN_DEATH, 1, 1);
					gameStop(signLoc);
				}
			}, 100);  
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	public void addPlayers(Location loc){
		for(Player near : Bukkit.getOnlinePlayers()){
			if (near.getWorld().getName().equalsIgnoreCase(Hub)){
				if((near.getLocation().distance(loc) < 15)) {
					if (playerMap.get(near) == null){ //Player not found in game
						near.sendMessage(ChatColor.GREEN + "Shoot the chicken as many times as you can!");
						addPlayer(near);
					}
				}
			}
		}
	}

	public void gameStop(Location loc){ //stops game and gets topscore on round
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			public void run() 
			{
				//RUN THIS CODE IN 30 SECS
				chickenGameRun = false; //end the game
				loc.getWorld().playSound(loc, Sound.ENTITY_CHICKEN_DEATH, 1, 1); //sound on game finish
				checkPlayer();
				allowWeaponsbuy = false;
				updateScoreSign(null);

			}
		}, 600);  //30 sex

	}


	public void checkPlayer(){
		int i = 0;
		int b = 0;
		String playerName = "Unknown";
		for (Map.Entry<String, Integer> playerMap : playerMap.entrySet()) {
			b = b + 1;
			Player p1 = Bukkit.getPlayer(playerMap.getKey());
			if (p1 != null){
				clearInv(p1);
			}
			//if point is higher save name
			if (playerMap.getValue() > i){
				playerName = playerMap.getKey();
				i = playerMap.getValue();
			}
		}
		if (playerMap.size() == b){
			//debug messagePlayer("all players in map have been checked: " + b + " winner was " + playerName);
			winner(playerName,i);
		}
	}

	public void clearInv(Player p){
		if (p.getWorld().getName().equalsIgnoreCase(Hub) && p != null){
			p.getInventory().clear();
			giveInventory(p);
		}
	}

	public void winner(String playerName, int topScore){
		if (topScore > 0 && playerName != null){ //if score is larger than 0 (means someone played the game)
			messagePlayer(ChatColor.GOLD + "Winner: " + playerName + " with " + topScore + " points!");

			Bukkit.getPlayer(playerName).sendTitle(ChatColor.GREEN + "Congratulations",ChatColor.GOLD + "You won the round with " + topScore + " points");

			signUpdater("HighRoundSign",playerName , topScore);

			World  w = Bukkit.getServer().getWorld(getConfig().getString(("HighScoreSign" + ".World")));
			int x = getConfig().getInt("HighScoreSign" + ".X");
			int y = getConfig().getInt("HighScoreSign" + ".Y");
			int z = getConfig().getInt("HighScoreSign" + ".Z");
			Block bloc = w.getBlockAt(x,y,z);
			if (bloc.getType() == Material.SIGN_POST || bloc.getType() == Material.WALL_SIGN || (bloc.getType() == Material.SIGN)) {
				Sign sign = (Sign)bloc.getState();
				int currentScore = Integer.parseInt(sign.getLine(3));
				//has the score been beaten?
				if (topScore > currentScore){
					signUpdater("HighScoreSign",playerName , topScore);
					releaseFireworksPlayer(Bukkit.getPlayer(playerName), "high");
					messagePlayer(ChatColor.GOLD + "New High Score!");
					chickenGameRun = false;


				} //do lamer fireworks
				else{
					releaseFireworksPlayer(Bukkit.getPlayer(playerName), "round");
					chickenGameRun = false;	
				}
			}
		}

		if (topScore == 0){
			chickenGameRun = false;
			messagePlayer(ChatColor.GOLD + "Try harder, no one scored!");
			allClearInv();
		}
	}
	public void allClearInv(){
		for(Player p : Bukkit.getOnlinePlayers()){
			if (p.getWorld().getName().equalsIgnoreCase(Hub) && p != null && playerMap.get(p.getName()) != null){
				clearInv(p);
			}
		}
	}

	public void giveInventory(Player p) {

		ItemStack item = new ItemStack(Material.IRON_SPADE, 1);
		ItemMeta itemmeta = item.getItemMeta();
		itemmeta.setDisplayName("Snow ball launcher");
		ArrayList<String> lore = new ArrayList<String>();
		String[] string_message = "Shoot other players!\nCloserange knockback!".split("\n");
		int i = 0;
		for (String output : string_message)
		{
			lore.add(i, output);
			i ++;
		}
		itemmeta.setLore(lore);
		item.setItemMeta(itemmeta);

		p.getInventory().setItem(1,new ItemStack(item));

	}

	@EventHandler
	public void onInteractEvent(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();
		int id = item.getType().getId();
		if (p.getWorld().getName().equalsIgnoreCase(Hub) && p != null){
			if (e.getAction()== Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR){
				if (item.getType().equals(Material.IRON_SPADE)){
					
					if (cooldowns.get(p.getName()) == null || cooldowns.get(p.getName()) + cooldownSpamTime < System.currentTimeMillis()){

						p.launchProjectile(Snowball.class);
						//ADD PLAYER TO COOL DOWN
						cooldowns.put(p.getName(), System.currentTimeMillis());	//update time
						
					}
				}

			}
		}
	}


	public void messagePlayer(String message){
		for(Player player : Bukkit.getOnlinePlayers()){
			if (playerMap.get(player.getName()) != null){
				player.sendMessage(message);
			}
		}
	}
	public void addPlayer(Player p) {
		if (playerMap.get(p.getName()) == null) {
			playerMap.put(p.getName(), 0);
		}
	}

	public void releaseFireworksPlayer(Player p, String type) {
		if (p.getWorld().getName().equalsIgnoreCase(Hub) && p != null){   
			if (type == "round"){
				for (int i = 0; i < 5; i++) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
						public void run()
						{
							if (p.getWorld().getName().equalsIgnoreCase(Hub) && p != null) {
								Firework firework = p.getWorld().spawn(p.getLocation().add(0,2,0), Firework.class);
								FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
								data.addEffects(FireworkEffect.builder().withColor(Color.PURPLE).with(Type.BALL_LARGE).build());
								data.setPower(1);
								firework.setFireworkMeta(data);
							}
						}
					}, (20 * (i + 1)));
				}
			}
			if (type == "high"){
				for (int i = 0; i < 10; i++) {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
						public void run()
						{
							Firework firework = p.getWorld().spawn(p.getLocation().add(0,2,0), Firework.class);
							FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
							data.addEffects(FireworkEffect.builder().withColor(Color.ORANGE).with(Type.BALL_LARGE).build());
							data.setPower(1);
							firework.setFireworkMeta(data);
						}
					}, (10 * (i + 1)));
				}
			}
		}
	}

	public void knockBack(Player attacker, Player victim, Integer i){
		//knock back effect
		Location loc1 = attacker.getLocation();//Get the location from the source player
		Location loc2 = victim.getLocation();//Get the location from the target player

		double deltaX = loc2.getX() - loc1.getX();//Get X Delta
		double deltaZ = loc2.getZ() - loc1.getZ();//Get Z delta

		Vector vec = new Vector(deltaX, 0, deltaZ);//Create new vector
		vec.normalize();//Normalize it so we don't shoot the player into oblivion
		victim.setVelocity(vec.multiply(i / (Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0)))));
	}

	public void TeleportToHub(Player p) {
		if (Hub != null) {
			World  w = Bukkit.getServer().getWorld(Hub);
			double x = getConfig().getDouble("Hub-Coordinates." + ".X");
			double y = getConfig().getDouble("Hub-Coordinates." + ".Y");
			double z = getConfig().getDouble("Hub-Coordinates." + ".Z");
			float pitch = getConfig().getInt("Hub-Coordinates." + ".Pitch");
			float yaw = getConfig().getInt("Hub-Coordinates." + ".Yaw");
			final Location HubSpawn = new Location ((World) w, x, y, z, yaw, pitch).add(new Vector(0.5, 0, 0.5));
			p.teleport(HubSpawn);
			p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERPEARL_THROW, 1, 1);
		}
		else {
			p.sendMessage(ChatColor.RED + "No Hub/ lobby has been definded. Please contact server admin");
		}
	}


}