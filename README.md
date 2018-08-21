# Advertiser (Plugin)

Adf.ly links to generate money on your Minecraft server.

## How It Works

1. User runs /ad command
2. Plugin sends a request to the node.js server to get a token
3. Node.js server generates a token, shortens it in a url via adf.ly and returns it to the plugin
4. Send the user the url
5. User clicks on the url, watches an ad by adf.ly and gets redirected to the node.js server and automatically redeems the token
6. The next time the server pulls all newly redeemed tokens, the user gets a reward

## Getting Started

You need to install this plugin to your Bukkit/Spigot server and host the node.js server.

## Installing

Put the .jar, you can either compile it yourself or use a pre-compiled .jar under the releases tab.

Start your server, wait for the message that a config file has been generated and stop your server.

Edit your config and restart your server.

## Config

**server.ip:** IP of the node.js server

**server.port:** Port of the node.js server

**secret: Secret** which is used to authentificate each other (plugin and node.js server)

**cooldown.pull:** Time in seconds until the next pull of redeemed tokens

**cooldown.generate:** Time in seconds until a player can get a new token

**reward.money:** The amount of money the user should recieve

## Build With

* [Spigot](https://www.spigotmc.org/)
* [Vault](https://www.spigotmc.org/resources/vault.34315/)
* [gson](https://github.com/google/gson)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
