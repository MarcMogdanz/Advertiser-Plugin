package de.marcmogdanz.advertiser;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class AdCommand implements CommandExecutor {
	
	Advertiser main;
	
	public AdCommand(Advertiser main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			System.out.println("You can only use this command as a player. :(");
			return true;
		}
		
		Player p = (Player) sender;
		UUID uuid = p.getUniqueId();
		
		if(p.hasPermission("advertiser.command.ad") || p.hasPermission("*") || p.isOp()) { } else {
			p.sendMessage(ChatColor.RED + "You don't have enough permission.");
			return true;
		}
		
		// Check if the user still has an outstanding token
		if(main.am.hasPlayerToken(uuid)) {
			// User has an outstanding token
			Token token = main.am.getToken(uuid);
			p.sendMessage("You still have an unredeemed token, click here: " + token.getURL());
			p.sendMessage("If you've already clicked the link, please wait a few seconds.");
			return true;
		} else {
			// User has no outstanding token
			int cooldown = main.getConfig().getInt("cooldown.generate");
			
			// Check if user is in cooldown hashmap
			if(main.am.getCooldowns().containsKey(uuid)) {
				// User is in cooldown hashmap
				// Check if his cooldown is already over
				long left = ((main.am.getCooldowns().get(uuid) / 1000) + cooldown) - (System.currentTimeMillis() /1000);
				if(left > 0) {
					// User is still on cooldown
					p.sendMessage("You're still on cooldown.");
					return true;
				}
			}
			
			// Put the user in the cooldown hashmap
			main.am.getCooldowns().put(uuid, System.currentTimeMillis());
			
			// Generate new token and add it to the token list
			Token token = main.am.generateToken(p.getUniqueId().toString());
			main.am.addToken(token);
			p.sendMessage(token.getURL());
			
		}
		return true;
	}
	
}
