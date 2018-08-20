package de.marcmogdanz.advertiser;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Advertiser extends JavaPlugin {
	
	public AdManager am;
    public static Economy economy = null;
	
	@Override
	public void onEnable() {
		// Setup AdManager and pass configuration
		this.am = new AdManager(this,
				getConfig().getString("server.ip"), getConfig().getInt("server.port"), getConfig().getString("secret"), getConfig().getInt("reward.money"));
		
		// Setup Vault Economy hook and check if it worked
		if(!setupEconomy()) {
			System.out.println("Couldn't hook into vault economy.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		// Setup config file
		setupConfig();
		
		// Register commands
		this.getCommand("ad").setExecutor(new AdCommand(this));
		
		// Start the automatic token pull from the node server
		this.startTokenPull();
	}
	
	@Override
	public void onDisable() { }
	
	// Scheduler to repeat running the pull
	@SuppressWarnings("deprecation")
	public void startTokenPull() {
		this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				runPull();
			}
		}, 100, this.getConfig().getInt("cooldown.pull") *20);
	}
	
	private void runPull() {
		System.out.println("Pulling redeemed tokens...");
		
		ArrayList<String> redeemedTokens = this.am.pullRedeemedTokens();
		
		if(redeemedTokens.get(0) == null) {
			System.out.println("No new redeemed tokens.");
		} else {
			System.out.println(redeemedTokens.size() + " new reedemed tokens.");
			for(int x = 0; x < redeemedTokens.size(); x++) {
				this.am.rewardUser(redeemedTokens.get(x));
			}
		}		
	}
	
	private void setupConfig() {
		File configFile = new File(getDataFolder(), "config.yml");
		
		if(!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		
		if(!configFile.exists()) {
			System.out.println("Config not found.");
			
			getConfig().set("server.ip", "127.0.0.1");
			getConfig().set("server.port", 3000);
			getConfig().set("secret", "123456789");
			getConfig().set("cooldown.pull", 30);
			getConfig().set("cooldown.generate", 600);
			getConfig().set("reward.money", 1337);
			saveConfig();
			
			System.out.println("Default config created.");
		}
	}
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if(economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}
	
}
