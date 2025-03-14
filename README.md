# AfkCommandPlugin

**Author:** AurorasChaos

AfkCommandPlugin is a Spigot plugin for Minecraft 1.21.4 that detects when a player is AFK (inactive) for a configurable period (default 5 minutes) and then executes a command to switch them to a specified lobby server. It also notifies the player when they become AFK and when they return, and includes a reload command for updating configuration without restarting the server.

## Features

- **AFK Detection:**  
  Automatically detects player inactivity based on movement and chat events.

- **Customizable Timeout:**  
  Configure the AFK timeout duration (in minutes) in the `config.yml`.

- **Custom Command Execution:**  
  Executes a command (`switchserver <playerName> PlayerReady-Lobby`) when a player is flagged as AFK. The command can be modified via the config file using `{player}` as a placeholder.

- **Player Notifications:**  
  Sends a configurable message to players when they are flagged as AFK and when they become active again.

- **Reloadable Configuration:**  
  Use the `/afkreload` command (permission: `afk.reload`) to reload the plugin configuration on the fly.

- **Bypass Permission:**  
  Players with the `afk.bypass` permission are excluded from the AFK check.

## Installation

1. **Clone or Download the Repository:**  
   Clone the repository to your local machine using:
   ```bash
   git clone https://github.com/YourUsername/AfkCommandPlugin.git
   ```
   or download the ZIP and extract it.

2. **Build the Plugin with Maven:**  
   Navigate to the project directory and run:
   ```bash
   mvn clean package
   ```
   This will generate the plugin JAR file (e.g., `AfkCommandPlugin-1.0.jar`) in the target directory.

3. **Deploy the Plugin:**  
   Copy the generated JAR file into your Spigot server's `plugins` directory.

4. **Restart or Reload Your Server:**  
   Restart your server or reload plugins (using a plugin manager) for the plugin to load.

## Configuration

After the first run, a default `config.yml` file will be generated in your server's `plugins/AfkCommandPlugin` folder. Open this file to customize settings:

```yaml
# Timeout before a player is flagged as AFK (in minutes)
afk-timeout: 5

# How often to check for AFK players (in seconds)
check-interval: 60

# Command to execute when a player is flagged as AFK.
# Use {player} as a placeholder for the player's name.
afk-command: "switchserver {player} PlayerReady-Lobby"

# Message sent to the player when they are flagged as AFK.
message-afk: "You are now flagged as AFK and will be switched to the lobby."

# Message sent to the player when they become active again.
message-back: "Welcome back!"

# Permission node to bypass the AFK check.
bypass-permission: "afk.bypass"
```

Feel free to adjust the timeout, command, messages, and other parameters to suit your server's needs.

## Usage

### AFK Detection
Once installed, the plugin will start monitoring player activity. If a player remains inactive for the configured duration (default 5 minutes), they will be switched to the lobby with the command:

```bash
switchserver <playerName> PlayerReady-Lobby
```

and they will receive the message defined in `message-afk`.

### Return Notification
When an AFK player moves or chats again, the plugin will send them the message defined in `message-back`.

### Reloading Configuration
To reload the configuration without restarting the server, use the command:

```bash
/afkreload
```

This command requires the `afk.reload` permission (by default granted to ops).

### Bypassing AFK Check
Players with the `afk.bypass` permission will not be subjected to the AFK check.

## Permissions

| Permission     | Description                                             | Default |
|---------------|---------------------------------------------------------|---------|
| `afk.reload`  | Allows reloading the plugin configuration using `/afkreload` | op      |
| `afk.bypass`  | Allows players to bypass the AFK check                 | false   |

## Development

### Prerequisites
- Java 17 or higher
- Maven
- Spigot 1.21.4 API

### Build
Run the following command in the project directory:

```bash
mvn clean package
```

This will compile the plugin and package it into a JAR file located in the `target` directory.

## Contributing

Contributions are welcome! Feel free to fork the repository and submit pull requests with improvements or bug fixes.

## License

This project is licensed under the GPL-3.0 License. See the LICENSE file for details.
