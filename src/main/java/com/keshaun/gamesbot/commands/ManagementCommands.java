package com.keshaun.gamesbot.commands;

import com.keshaun.gamesbot.App;
import com.keshaun.gamesbot.utils.ErrorMessages;
import com.keshaun.gamesbot.utils.GameMessages;
import com.keshaun.gamesbot.utils.MySQLListener;
import org.pircbotx.*;
import org.pircbotx.Configuration.*;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagementCommands extends MySQLListener {
	private App app;

    private static final Logger LOGGER = Logger.getLogger(ManagementCommands.class.getName());
	
	public ManagementCommands(App app) {
		this.app = app;
	}
	
    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String message = event.getMessage();
        String command = message.split(" ")[0];
        User user = event.getUser();
        Channel channel = event.getChannel();
        PircBotX bot = event.getBot();
        BotFactory bf = new BotFactory();

        @SuppressWarnings("resource")
        UserChannelDao<User, Channel> uc = new UserChannelDao<User, Channel>(bot, bf);

        //Add an Owner to the database
        if ("!addme".equalsIgnoreCase(command)) {
            if ("Crimsonninja".equalsIgnoreCase(user.getNick()) || "Lan".equalsIgnoreCase(user.getNick())) {
                addUser(user.getNick(), App.OWNER);
            } else {
                channel.send().message(Colors.RED + "Nice try, " + user.getNick() + "!");
            }
        }

        //Add an Admin to the database
        if ("!addAdmin".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.OWNER)) {
                if (message.split(" ").length > 1) {
                    addUser(message.split(" ")[1], App.ADMIN);
                } else {
                    event.respond(Colors.RED + GameMessages.CORRECT_ARGS + Colors.BLACK + "!addAdmin <nick>");
                }
            } else {
                event.respond(Colors.RED + "You don't have proper access to use !addAdmin.");
            }
        }

        //Add a Mod to the database
        if ("!addMod".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.ADMIN)) {
                if (message.split(" ").length > 1) {
                    addUser(message.split(" ")[1], App.MOD);
                } else {
                    event.respond(Colors.RED + GameMessages.CORRECT_ARGS + Colors.BLACK + "!addMod <nick>");
                }
            } else {
                event.respond(Colors.RED + "You don't have proper access to use !addMod.");
            }
        }

        //Add a ban to the database
        if ("!ban".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.MOD)) {
                if (message.split(" ").length > 1) {
                    addUser(message.split(" ")[1], App.BANNED);
                } else {
                    event.respond(Colors.RED + GameMessages.CORRECT_ARGS + Colors.BLACK + "!ban <nick>");
                }
            } else {
                event.respond(Colors.RED + "You don't have proper access to use !ban.");
            }
        }

        //Kick and ban a user from the channel
        if ("!kb".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.MOD)) {
                if (message.split(" ").length > 1) {
                    if (getLevel(user.getNick()) > getLevel(message.split(" ")[1])) {
                        User u = uc.getUser(message.split(" ")[1]);
                        channel.send().ban(u.getNick());
                        channel.send().kick(u);
                    }
                } else {
                    event.respond(Colors.RED + GameMessages.CORRECT_ARGS + Colors.BLACK + "!kb <nick>");
                }
            } else {
                event.respond(Colors.RED + "You don't have proper access to use !kb.");
            }
        }

        //Kick a user from the channel
        if ("!kick".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.MOD)) {
                if (message.split(" ").length > 1) {
                    if (getLevel(user.getNick()) > getLevel(message.split(" ")[1])) {
                        User u = uc.getUser(message.split(" ")[1]);
                        channel.send().kick(u);
                    }
                } else {
                    event.respond(Colors.RED + GameMessages.CORRECT_ARGS + Colors.BLACK + "!kick <nick>");
                }
            } else {
                event.respond(Colors.RED + "You don't have proper access to use !kb.");
            }
        }

        //Make the bot join a channel
        if ("!join".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.OWNER)) {
                if (message.split(" ").length > 2) {
                    bot.sendIRC().joinChannel(message.split(" ")[1], message.split(" ")[2]);
                } else if (message.split(" ").length > 1) {
                    bot.sendIRC().joinChannel(message.split(" ")[1]);
                } else {
                    event.respond(Colors.RED + GameMessages.CORRECT_ARGS + Colors.BLACK + "!join <channel> <key>");
                }
            } else {
                event.respond(Colors.RED + "You don't have proper access to use !join.");
            }
        }

        if ("!quit".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.OWNER)) {
                if (message.split(" ").length > 1) {
                    String quitMessage = "";

                    for (int i = 1; i < message.split(" ").length; i++) {
                        quitMessage += message.split(" ")[i];
                        if (i != message.split(" ").length - 1)
                            quitMessage += " ";
                    }

                    bot.sendIRC().quitServer(quitMessage);
                } else {
                    bot.sendIRC().quitServer();
                }
            }
        }

        //Enable people to play games
        if ("!enableGames".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.ADMIN)) {
                if (!app.getGameOn()) 
                	app.toogleGameOn();
                channel.send().message(Colors.GREEN + "Games enabled.");
            } else {
                event.respond(Colors.RED + "You don't have proper access to use !enableGames.");
            }
        }

        //Disable people to play games
        if ("!disableGames".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.ADMIN)) {
            	if (app.getGameOn()) 
                	app.toogleGameOn();
                channel.send().message(Colors.RED + "Games disabled.");
            } else {
                event.respond(Colors.RED + "You don't have proper access to use !disableGames.");
            }
        }

        if ("!isGameEnabled".equalsIgnoreCase(command)) {
            if (app.getGameOn()) {
                event.respond(Colors.GREEN + "Yes!");
            } else {
                event.respond(Colors.RED + "No!");
            }
        }

        if ("!gameType".equalsIgnoreCase(command)) {
            if (check(user.getNick(), App.OWNER)) {
                if (message.split(" ").length > 1) {
                    if ("blackjack".equalsIgnoreCase(message.split(" ")[1]) || "0".equalsIgnoreCase(message.split(" ")[1])) {
                        app.setGameType();
                        event.respond(Colors.GREEN + GameMessages.GAME_TYPE_CHANGED + Colors.BOLD + Colors.BLUE + "Blackjack");
                    } else if ("poker".equalsIgnoreCase(message.split(" ")[1]) || "1".equalsIgnoreCase(message.split(" ")[1])) {
                        app.setGameType(1);
                        event.respond(Colors.GREEN + GameMessages.GAME_TYPE_CHANGED + Colors.BOLD + Colors.BLUE + "Poker");
                    } else if ("mafia".equalsIgnoreCase(message.split(" ")[1]) || "2".equalsIgnoreCase(message.split(" ")[1])) {
                        app.setGameType(2);
                        event.respond(Colors.GREEN + GameMessages.GAME_TYPE_CHANGED + Colors.BOLD + Colors.BLUE + "Mafia");
                    } else {
                        event.respond(Colors.RED + "Invalid Game Type");
                    }
                }
            } else {
                event.respond(Colors.RED + "You don't have proper access to use !gameType.");
            }
        }
    }

    @Override
    public void setupDB() {
        Statement stmt = null;
        try {
            openConnection();
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS access (name CHAR(50), level TINYINT(3));";
            stmt.executeUpdate(sql);
            stmt.close();
            closeConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, ErrorMessages.SQL, e);
        }
    }

    public void addUser(String user, int level) {
        openConnection();
        String sql = "REPLACE INTO access VALUES (?,?)";
        PreparedStatement statement = null;
        try {
            statement = c.prepareStatement(sql);
            statement.setString(1, user);
            statement.setInt(2, level);
            statement.executeUpdate();
            statement.close();
            closeConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, ErrorMessages.SQL, e);
        }
    }

    public int getLevel(String user) {
        openConnection();
        String sql = "SELECT COUNT(*) as there, level FROM access WHERE name = ?";
        PreparedStatement statement = null;
        int level = 1;
        try {
            statement = c.prepareStatement(sql);
            statement.setString(1, user);
            ResultSet rs = statement.executeQuery();
            rs.next();
            if (rs.getInt("there") > 0)
                level = rs.getInt("level");
            rs.close();
            statement.close();
            closeConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, ErrorMessages.SQL, e);
        }

        return level;
    }

    public boolean check(String user, int level) {
        return getLevel(user) >= level;
    }

    public boolean checkBanned(String user) {
        return getLevel(user) == App.BANNED;
    }
}
