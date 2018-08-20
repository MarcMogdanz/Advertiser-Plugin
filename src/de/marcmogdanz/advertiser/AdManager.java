package de.marcmogdanz.advertiser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.Gson;

public class AdManager {
	
	private ArrayList<Token> tokens;
	public HashMap<UUID, Long> cooldowns = new HashMap<UUID, Long>();
	
	private Advertiser main;
	private String secret;
	private int rewardMoney;
	
	private String URL_BASE;
	private String URL_ADFLY = "/api/adfly";
	private String URL_ADFLY_PULL = "/api/adfly/pull";
	
	public AdManager(Advertiser main, String ip, int port, String secret, int rewardMoney) {
		tokens = new ArrayList<Token>();
		
		this.main = main;
		this.secret = secret;
		this.rewardMoney = rewardMoney;
		this.URL_BASE = "http://" + ip + ":" + port;
	}
	
	// Generate token and return it
	public Token generateToken(String uuid) {
		try {
			// Send request to the node server to generate a token and return the adf.ly shortened url
			URL url = new URL(URL_BASE + URL_ADFLY);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("secret", secret);
			int responseCode = con.getResponseCode();
			
			// Check if everything went fine
			if(responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				
				while((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				
				in.close();
				
				// Parse json to Java objecs (RawToken as a helper class)
				Gson gson = new Gson();
				RawToken rawtoken = gson.fromJson(response.toString(), RawToken.class);
				
				// Create a new token object
				Token token = new Token(rawtoken.token, rawtoken.url, uuid);
				return token;
			} else {
				System.out.println("Tried to generate token, got response code: " + responseCode);
				return null;
			}
		} catch (IOException e) {
			System.out.println("Tried to generate token, got error: ");
			e.printStackTrace();
			return null;
		}
	}
	
	// Get all redeemed tokens from the node server
	public ArrayList<String> pullRedeemedTokens() {
		try {
			// Send request to the node server to get all redeemed tokens
			URL url = new URL(URL_BASE + URL_ADFLY_PULL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("secret", secret);
			int responseCode = con.getResponseCode();
			
			// Check if everything went fine
			if(responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				
				while((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				
				in.close();
				
				// Parse json to Java objecs (RedeemedTokens as a helper class)
				Gson gson = new Gson();
				RedeemedTokens redeemedTokens = gson.fromJson(response.toString(), RedeemedTokens.class);
				
				// Return the tokens as ArrayList
				return redeemedTokens.tokens;
			} else {
				System.out.println("Tried to pull redeemed tokens, got response code: " + responseCode);
				return null;
			}
		} catch (IOException e) {
			System.out.println("Tried to pull redeemed tokens, got error: ");
			e.printStackTrace();
			return null;
		}
	}
	
	// Pass the configured reward to the user associated with the specific token
	@SuppressWarnings({ "deprecation", "static-access" })
	public void rewardUser(String token) {
		// Get token object by its token string
		Token tokenObject = getToken(token);
		if(tokenObject == null) return;
		
		// Delete the token from the token list
		removeToken(token);
		
		// Get the to be rewarded user
		Player p = (Player) Bukkit.getOfflinePlayer(UUID.fromString(tokenObject.getUUID()));
		
		// Check if he is online and even exist
		if(p != null && p.isOnline()) {
			if(main.economy.hasAccount(p.getName())) {
				p.sendMessage("Your reward is here.");
				main.economy.depositPlayer(p.getName(), this.rewardMoney);
			} else {
				p.sendMessage("You don't have a bank account, couldn't give you your reward.");
			}
		}
	}
	
	// Add token to the token list
	public void addToken(Token token) {
		this.tokens.add(token);
	}
	
	// Get a token from the token list by the token string
	public Token getToken(String token) {
		for(int x = 0; x < this.tokens.size(); x++) {
			Token tokenFromList = this.tokens.get(x);
			if(tokenFromList.getToken().equals(token)) {
				return tokenFromList;
			}
		}
		return null;
	}
	
	// Get a token from the token list by the user uuid associated with the token
	public Token getToken(UUID uuid) {
		for(int x = 0; x < this.tokens.size(); x++) {
			Token tokenFromList = this.tokens.get(x);
			if(tokenFromList.getUUID().equals(uuid.toString())) {
				return tokenFromList;
			}
		}
		return null;
	}
	
	// Remove a token from the token list by the token string
	public void removeToken(String token) {
		for(int x = 0; x < this.tokens.size(); x++) {
			Token tokenFromList = this.tokens.get(x);
			if(tokenFromList.getToken().equals(token)) {
				this.tokens.remove(x);
				return;
			}
		}
	}
	
	// Check if a specific uuid/player has a token in the token list
	public boolean hasPlayerToken(UUID uuid) {
		for(int x = 0; x < this.tokens.size(); x++) {
			Token tokenFromList = this.tokens.get(x);
			if(tokenFromList.getUUID().equals(uuid.toString())) return true;
		}
		return false;
	}
	
	// Return the cooldown hashmap
	public HashMap<UUID, Long> getCooldowns() {
		return this.cooldowns;
	}
	
}
