package de.marcmogdanz.advertiser;

public class Token {
	
	private String token;
	private String url;
	private String uuid;
	
	public Token(String token, String url, String uuid) {
		this.token = token;
		this.url = url;
		this.uuid = uuid;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public String getURL() {
		return this.url;
	}
	
	public String getUUID() {
		return this.uuid;
	}
	
}
