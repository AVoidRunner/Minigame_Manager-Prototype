package me.voidrunnerv.minigamemanagerprototype;

import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin implements Listener{
	
	ArrayList<String> eventPlayerArrayList = new ArrayList<String>();
	int eventPlayers = 0;
	int timer = 10;
	int playersNeededToStart = 1;
	
	boolean started = false;
	
	Score score2;
	Score score3;
	
	private int taskID;
	
	public int count = 0;
	
	ConsoleCommandSender console;
	
	ArrayList<String> teamOne = new ArrayList<String>();
	ArrayList<String> teamTwo = new ArrayList<String>();
	ArrayList<String> teamThree = new ArrayList<String>();
	ArrayList<String> teamFour = new ArrayList<String>();
	
	Location teamSpawnOne;
	Location teamSpawnTwo;
	Location teamSpawnThree;
	Location teamSpawnFour;
	
	//boolean bendingWallsStarted
	boolean eventStarted = false;
	
	int wallCountdownTimer = 60;
	
	@Override
	public void onEnable() {
		
		count = 0;
		
		started = false;
		
		getServer().getPluginManager().registerEvents(this, this);
		
		this.saveDefaultConfig();
		console = Bukkit.getServer().getConsoleSender();
		
		String teamOneSpawn = this.getConfig().getString("teamOneSpawn");
		String[] teamOneSpawnCoords = teamOneSpawn.split(" ");
		Double teamOneSpawnX = Double.parseDouble(teamOneSpawnCoords[0]);
		Double teamOneSpawnY = Double.parseDouble(teamOneSpawnCoords[1]);
		Double teamOneSpawnZ = Double.parseDouble(teamOneSpawnCoords[2]);
		
		String teamTwoSpawn = this.getConfig().getString("teamTwoSpawn");
		String[] teamTwoSpawnCoords = teamTwoSpawn.split(" ");
		Double teamTwoSpawnX = Double.parseDouble(teamTwoSpawnCoords[0]);
		Double teamTwoSpawnY = Double.parseDouble(teamTwoSpawnCoords[1]);
		Double teamTwoSpawnZ = Double.parseDouble(teamTwoSpawnCoords[2]);
		
		String teamThreeSpawn = this.getConfig().getString("teamThreeSpawn");
		String[] teamThreeSpawnCoords = teamThreeSpawn.split(" ");
		Double teamThreeSpawnX = Double.parseDouble(teamThreeSpawnCoords[0]);
		Double teamThreeSpawnY = Double.parseDouble(teamThreeSpawnCoords[1]);
		Double teamThreeSpawnZ = Double.parseDouble(teamThreeSpawnCoords[2]);
		
		String teamFourSpawn = this.getConfig().getString("teamFourSpawn");

		String[] teamFourSpawnCoords = teamFourSpawn.split(" ");
		Double teamFourSpawnX = Double.parseDouble(teamFourSpawnCoords[0]);
		Double teamFourSpawnY = Double.parseDouble(teamFourSpawnCoords[1]);
		Double teamFourSpawnZ = Double.parseDouble(teamFourSpawnCoords[2]);
		
		String worldName = this.getConfig().getString("WorldName");
		World teamSpawnWorld = Bukkit.getWorld(worldName);
		
		teamSpawnOne = new Location(teamSpawnWorld, teamOneSpawnX, teamOneSpawnY, teamOneSpawnZ);
		teamSpawnTwo = new Location(teamSpawnWorld, teamTwoSpawnX, teamTwoSpawnY, teamTwoSpawnZ);
		teamSpawnThree = new Location(teamSpawnWorld, teamThreeSpawnX, teamThreeSpawnY, teamThreeSpawnZ);
		teamSpawnFour = new Location(teamSpawnWorld, teamFourSpawnX, teamFourSpawnY, teamFourSpawnZ);
		
		//For when we /reload lol
		if(!Bukkit.getOnlinePlayers().isEmpty()) {
			for(Player online : Bukkit.getOnlinePlayers()) {
				setScoreBoard(online);
			}
		}
		
	}
	
	@Override
	public void onDisable() {
		//Server shutdown, reloads, or plugin reloads
		this.saveConfig();
	}
	
	public void bendingWallsGame(ArrayList<String> wallsAlivePlayersTeamOne, ArrayList<String> wallsAlivePlayersTeamTwo, ArrayList<String> wallsAlivePlayersTeamThree, ArrayList<String> wallsAlivePlayersTeamFour) {
		ArrayList<String> wallsSpectators = new ArrayList<String>();
		
		ArrayList<String> wallsAlivePlayersTeamOneOriginal = wallsAlivePlayersTeamOne;
		ArrayList<String> wallsAlivePlayersTeamTwoOriginal = wallsAlivePlayersTeamTwo;
		ArrayList<String> wallsAlivePlayersTeamThreeOriginal = wallsAlivePlayersTeamThree;
		ArrayList<String> wallsAlivePlayersTeamFourOriginal = wallsAlivePlayersTeamFour;
		
		int teamsAlive = 0;
		
		if(wallsAlivePlayersTeamOne.size() > 0) teamsAlive++;
		if(wallsAlivePlayersTeamTwo.size() > 0) teamsAlive++;
		if(wallsAlivePlayersTeamThree.size() > 0) teamsAlive++;
		if(wallsAlivePlayersTeamFour.size() > 0) teamsAlive++;
		
		wallCountdownTimer = 60;
		
		Player masterVDevTesting = Bukkit.getPlayer("mastervrunner");
		if(masterVDevTesting != null) {
			masterVDevTesting.sendMessage("Teams alive: " + teamsAlive);
		}
		
		if(teamsAlive > 0) {
			bendingWallsUpdateScoreboard(wallsAlivePlayersTeamOne,wallsAlivePlayersTeamTwo,wallsAlivePlayersTeamThree,wallsAlivePlayersTeamFour, wallsSpectators, wallCountdownTimer);
			new BukkitRunnable(){
				  public void run(){
					  if(wallCountdownTimer > 0) {
						  
						  //Time until wall falls is more than 0, so every second it subtracts one.
						  
						  wallCountdownTimer--;
						  bendingWallsUpdateScoreboard(wallsAlivePlayersTeamOne,wallsAlivePlayersTeamTwo,wallsAlivePlayersTeamThree,wallsAlivePlayersTeamFour, wallsSpectators, wallCountdownTimer);
					  }
					  
					  if(wallCountdownTimer <= 0) {
						  //I'm not using else if because I want it to remove the walls right when it reaches 0.
						  
						  //Time until walls fall is 0 (or less possibly idk how or why that would happen)
						  
						  //Code to remove walls from config here, the variables should be defined above in the on enable so it has less lag in the long run.
						  
						  
					  }
				  }
			}.runTaskTimer(this, 0, 20);
		}
		
		
	}
	
	public void bendingWallsUpdateScoreboard(ArrayList<String> bendingWallsTeamOne, ArrayList<String> bendingWallsTeamTwo, ArrayList<String> bendingWallsTeamThree, ArrayList<String> bendingWallsTeamFour, ArrayList<String> bendingWallsSpectators, int wallsFallTime) {
		
		ArrayList<String> allBendingWallsPlayers = new ArrayList<String>();
		
		int wallsTeamsAlive = 0;
		if(bendingWallsTeamOne.size() > 0) wallsTeamsAlive++;
		if(bendingWallsTeamTwo.size() > 0) wallsTeamsAlive++;
		if(bendingWallsTeamThree.size() > 0) wallsTeamsAlive++;
		if(bendingWallsTeamFour.size() > 0) wallsTeamsAlive++;
		
		Player masterVDevTesting = Bukkit.getPlayer("mastervrunner");
		if(masterVDevTesting != null) {
			masterVDevTesting.sendMessage("Teams alive in updateScoreboard: " + wallsTeamsAlive);
		}
		
		for(String wallPlayers : bendingWallsTeamOne) {
			allBendingWallsPlayers.add(wallPlayers);
		}
		
		for(String wallPlayers : bendingWallsTeamTwo) {
			allBendingWallsPlayers.add(wallPlayers);
		}
		
		for(String wallPlayers : bendingWallsTeamThree) {
			allBendingWallsPlayers.add(wallPlayers);
		}
		
		for(String wallPlayers : bendingWallsTeamFour) {
			allBendingWallsPlayers.add(wallPlayers);
		}
		
		for(String wallPlayers : bendingWallsSpectators) {
			allBendingWallsPlayers.add(wallPlayers);
		}
		
		for(Player bendingWallsPlayer : Bukkit.getOnlinePlayers()) {
			
			String bendingWallsPlayerUuid = bendingWallsPlayer.getUniqueId().toString();
			
			if(allBendingWallsPlayers.contains(bendingWallsPlayerUuid)) {
				
				Scoreboard board = bendingWallsPlayer.getScoreboard();
				if(bendingWallsPlayer.getScoreboard() != null && bendingWallsPlayer.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
					board.getTeam("EPC").setPrefix(ChatColor.AQUA + "Alive Players: " + (allBendingWallsPlayers.size() - bendingWallsSpectators.size()));
					
					if(wallsFallTime > 0) {
						board.getTeam("TSC").setPrefix(ChatColor.AQUA + "Time Until Walls Fall: " + ChatColor.DARK_AQUA + wallsFallTime);
					} else {
						board.getTeam("TSC").setPrefix(ChatColor.AQUA + "Teams Alive: " + ChatColor.DARK_AQUA + wallsTeamsAlive);
					}
				}
			}
		}
		
	}
	
	public String beginEvent() {
		
		int teamSelection = 1;
		int playerTeamCount = 0;
		
		if(!Bukkit.getOnlinePlayers().isEmpty()) {
			
			eventStarted = true;

			for(Player eventPlayer : Bukkit.getOnlinePlayers()) {
				String eventPlayerUUIDString = eventPlayer.getUniqueId().toString();
				
				if(eventPlayerArrayList.contains(eventPlayerUUIDString)) {
					switch(teamSelection) {
					case 1:
						teamOne.add(eventPlayerUUIDString);
						eventPlayer.sendMessage("You have joined team one!");
						playerTeamCount++;
						eventPlayer.teleport(teamSpawnOne);
						break;
					case 2:
						teamTwo.add(eventPlayerUUIDString);
						eventPlayer.sendMessage("You have joined team two!");
						playerTeamCount++;
						eventPlayer.teleport(teamSpawnTwo);
						break;
					case 3:
						teamThree.add(eventPlayerUUIDString);
						eventPlayer.sendMessage("You have joined team three!");
						playerTeamCount++;
						eventPlayer.teleport(teamSpawnThree);
						break;
					case 4:
						teamFour.add(eventPlayerUUIDString);
						eventPlayer.sendMessage("You have joined team four!");
						playerTeamCount++;
						eventPlayer.teleport(teamSpawnFour);
						
						break;
						
					}
					
					if(teamSelection == 4) {
						teamSelection = 1;
					} else {
						teamSelection++;
					}
					
					
				}
			}
			if(eventPlayerArrayList.size() > 0) {
				if(playerTeamCount > 1) {
					return "Added " + playerTeamCount + " players to teams!";
				} else {
					return "Added " + playerTeamCount + " player to teams!";
				}
			}
		}
		
		return "Could not begin event.";
	}
	
	public void start() {
		
		new BukkitRunnable(){
			  public void run(){
				  
				  if(eventPlayers > 0 && timer > 0) {
						
					  timer--;
						
						if(timer == 0) {
							String beginningEvent = beginEvent();
							Bukkit.getLogger().info(beginningEvent);
						}
					}
					
				  count++;
				  if(count == 4) {
					  count = 0;
				  }
					
				  if(!Bukkit.getOnlinePlayers().isEmpty()) {
					  for(Player online : Bukkit.getOnlinePlayers()) {
						  updateScoreBoard(online);
					  }
				  }
				  
				  
				  
				  if(eventStarted){
			      this.cancel();
			    }
			  }
			}.runTaskTimer(this, 0, 20);
		
	}
	
	public void createBoard(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective obj = board.registerNewObjective("EventSB-1", "dummy", ChatColor.translateAlternateColorCodes('&', "&6HoB - Event"));
		
		//Change the scoreboard name from HoB - Event to: Event obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Event"));
		
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		//SCORES CANT BE MORE THAN 40 CHARACTERS! Or 32 characters if you're before 1.13
		
		Score score1 = obj.getScore(ChatColor.BLUE + "=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		score1.setScore(3);

		score2 = obj.getScore(ChatColor.AQUA + "Event players: " + eventPlayers);
		score2.setScore(2);
		
		//Woah you can get player stats! Epic! player.getStatistic(Statistic.ANIMALS_BRED)
		
		if(eventPlayers < playersNeededToStart) {
			score3 = obj.getScore(ChatColor.AQUA + "Waiting for " + playersNeededToStart + " players to start...");
			score3.setScore(1);
		} else {
			score3 = obj.getScore(ChatColor.AQUA + "Time until Event Start: " + ChatColor.DARK_AQUA + timer);
			score3.setScore(1);
		}
		
		player.setScoreboard(board);
		
	}
	
	public void setScoreBoard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("ServerName", "dummy", ChatColor.GOLD + "HoB - Events");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        Score blankSpace = obj.getScore(ChatColor.BLUE + "---------------------------");
        blankSpace.setScore(16);
        
        Team eventPlayersCounter = board.registerNewTeam("EPC");
        eventPlayersCounter.addEntry(ChatColor.BLUE + "" + ChatColor.WHITE);
        eventPlayersCounter.setPrefix(ChatColor.AQUA + "Event players: " + eventPlayers);
        obj.getScore(ChatColor.BLUE + "" + ChatColor.WHITE).setScore(12);

        Team timerScoreCounter = board.registerNewTeam("TSC");
        timerScoreCounter.addEntry(ChatColor.RED + "" + ChatColor.WHITE);
        timerScoreCounter.setPrefix(ChatColor.AQUA + "Time until Event Start: " + ChatColor.DARK_AQUA + timer);
        
        obj.getScore(ChatColor.RED + "" + ChatColor.WHITE).setScore(10);
        
        player.setScoreboard(board);
        
	}
	
	public void updateScoreBoard(Player player) {

        Scoreboard board = player.getScoreboard();
        
        //This FIRST tests if the scoreboard is null THEN if it isn't null, it tests if its the sidebar is null. It will stop if the first one is null so it wont make an error because its checking the objective of a null object, because it will never check the objective if the scoreboard its self is null because it checks it first then stops if it is null.
        if(player.getScoreboard() != null && player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
        	
	        switch(count) {
			
			case 0:
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eHoB - Event"));
				break;
			
			case 1:
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6H&eoB - Event"));
				break;
				
			case 2:
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eH&6o&eB - Event"));
				break;
				
				
			case 3:
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(ChatColor.translateAlternateColorCodes('&', "&eHo&6B&e - Event"));
				//createBoard(player);
				break;
	        }
	        
	        board.getTeam("EPC").setPrefix(ChatColor.AQUA + "Event players: " + eventPlayers);
	        board.getTeam("TSC").setPrefix(ChatColor.AQUA + "Time until Event Start: " + ChatColor.DARK_AQUA + timer);
	        
        }
        
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		setScoreBoard(player);
		
		updateScoreBoard(event.getPlayer());
	}
	
	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		String quitPlayerUUID = player.getUniqueId().toString();
		
		if(eventPlayerArrayList.contains(quitPlayerUUID)) {
			eventPlayerArrayList.remove(quitPlayerUUID);
			if(eventPlayers > 0) {
				eventPlayers--;
			} else {
				getLogger().info("Somehow, event players is already at or below 0? Removed from arraylist but did not subtract 1 from eventPlayers variable. EventPlayers value: " + eventPlayers);
			}
			getLogger().info("Removed player " + player.getName().toString() + " from event.");
		}
		
		updateScoreBoard(event.getPlayer());
		
		return;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(label.equalsIgnoreCase("addToEvent") && sender.hasPermission("Events.addToEvent")) {
			
			if(eventStarted) {
				sender.sendMessage("Event already started!");
				return true;
			}
			
			if(args.length > 0) {
				Player eventPlayer = Bukkit.getPlayer(args[0]);
				if(eventPlayer != null) {
					String eventPlayerUUID = eventPlayer.getUniqueId().toString();
					if(!eventPlayerArrayList.contains(eventPlayerUUID)) {
						//PLAYER JOINED EVENT
						eventPlayerArrayList.add(eventPlayerUUID);
						eventPlayers++;
						
						eventPlayer.sendMessage("Joined event.");

						if(!Bukkit.getOnlinePlayers().isEmpty()) {
							for(Player online : Bukkit.getOnlinePlayers()) {
								updateScoreBoard(online);
							}
						}
						
						if(!started) {
							start();
							started = true;
						}
						
					} else {
						sender.sendMessage("Player: " + args[0] + " is already added to the event!");
					}
				} else {
					sender.sendMessage("Could not find player to add to event!");
				}
				
			} else {
				if(label.equalsIgnoreCase("addToEvent") && sender instanceof Player && args.length == 0) {
					Player eventPlayer = (Player) sender;
					if(eventPlayer != null) {
						String eventPlayerUUID = eventPlayer.getUniqueId().toString();
						if(!eventPlayerArrayList.contains(eventPlayerUUID)) {
							//PLAYER JOINED EVENT
							eventPlayerArrayList.add(eventPlayerUUID);
							eventPlayers++;
							
							eventPlayer.sendMessage("Joined event.");
							if(!Bukkit.getOnlinePlayers().isEmpty()) {
								for(Player online : Bukkit.getOnlinePlayers()) {
									updateScoreBoard(online);
								}
							}
							
							if(!started) {
								start();
								started = true;
							}
							
						} else {							
							sender.sendMessage("You are already in the event!");
						}
					}
				}
			}
		} else {
			if(label.equalsIgnoreCase("addToEvent") && sender instanceof Player && args.length == 0) {
				Player eventPlayer = (Player) sender;
				if(eventPlayer != null) {
					String eventPlayerUUID = eventPlayer.getUniqueId().toString();
					if(!eventPlayerArrayList.contains(eventPlayerUUID)) {
						//PLAYER JOINED EVENT
						eventPlayerArrayList.add(eventPlayerUUID);
						eventPlayers++;
						
						eventPlayer.sendMessage("Joined event.");
						
						if(!Bukkit.getOnlinePlayers().isEmpty()) {
							for(Player online : Bukkit.getOnlinePlayers()) {
								updateScoreBoard(online);
							}
						}
						
						if(!started) {
							start();
							started = true;
						}
						
					} else {
						sender.sendMessage("You are already in the event!");
					}
				}
			}
		}
		
		if(label.equalsIgnoreCase("leaveEvent")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				String playerUUID = player.getUniqueId().toString();
				
				if(eventPlayerArrayList.contains(playerUUID)) {
					
					//PLAYER LEAVING EVENT
					player.sendMessage("Leaving event.");
					
					eventPlayerArrayList.remove(playerUUID);
					if(eventPlayers > 0) {
						eventPlayers--;
					} else {
						getLogger().info("Somehow, event players is already at or below 0? Removed from arraylist but did not subtract 1 from eventPlayers variable. EventPlayers value: " + eventPlayers);
					}
					
					updateScoreBoard(player);
					
					getLogger().info("Removed player " + player.getName().toString() + " from event.");
					
					Location worldSpawn = player.getWorld().getSpawnLocation();
					
					String sendToSpawnCommand = "minecraft:tp " + player.getName() + " " + worldSpawn.getX() + " " + worldSpawn.getY() + " " + worldSpawn.getZ();
					Bukkit.dispatchCommand(console, sendToSpawnCommand);
					
				} else {
					player.sendMessage("You need to be in the event before you can use /leaveEvent.....");
				}
				
			} else {
				sender.sendMessage("Only a player can use the command /leaveEvent.");
			}
		}
		
		return false;
	}
	
}
