package net.keshaun.gamesbot.commands.utils;

import java.lang.reflect.Method;

import net.keshaun.gamesbot.App;
import net.keshaun.gamesbot.commands.ManagementCommands;
import net.keshaun.gamesbot.exceptions.InvalidGameTypeException;
import net.keshaun.gamesbot.utils.GameMessages;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.Configuration.BotFactory;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserChannelDao;
import org.pircbotx.hooks.events.MessageEvent;

public class ManagementUtil {
	private ManagementCommands mc;
	private MessageEvent<PircBotX> event;
	private String message;
	private Channel channel;
	private User user;
	private PircBotX bot;
	private BotFactory bf = new BotFactory();
	private UserChannelDao<User, Channel> uc;
	
	public ManagementUtil(ManagementCommands mc, MessageEvent<PircBotX> event) {
		this.mc = mc;
		this.event = event;
		loadVariables();
	}

	private void loadVariables() {
		message = event.getMessage();
		channel = event.getChannel();
		user = event.getUser();
		bot = event.getBot();
		
		uc = new UserChannelDao<User, Channel>(bot, bf);
	}
	
	public void addMe() {
		if ("Crimsonninja".equalsIgnoreCase(user.getNick()) || "Lan".equalsIgnoreCase(user.getNick())) {
			mc.addUser(user.getNick(), App.OWNER);
		} else {
			channel.send().message(Colors.RED + "Nice try, " + user.getNick() + "!");
		}
	}
	
	public void addAdmin() {
		if (mc.check(user.getNick(), App.OWNER)) {
			if (message.split(" ").length > 1) {
				mc.addUser(message.split(" ")[1], App.ADMIN);
			} else {
				event.respond(Colors.RED + "Correct Args: " + Colors.BLACK + "!addAdmin <nick>");
			}
		} else {
			event.respond(Colors.RED + "You don't have proper access to use !addAdmin");
		}
	}
	
	public void addMod() {
		if (mc.check(user.getNick(), App.ADMIN)) {
			if (message.split(" ").length > 1) {
				mc.addUser(message.split(" ")[1], App.MOD);
			} else {
				event.respond(Colors.RED + "Correct Args: " + Colors.BLACK + "!addMod <nick>");
			}
		} else {
			event.respond(Colors.RED + "You don't have proper access to use !addMod");
		}
	}
	
	public void ban() {
		if (mc.check(user.getNick(), App.MOD)) {
			if (message.split(" ").length > 1) {
				mc.addUser(message.split(" ")[1], App.BANNED);
			} else {
				event.respond(Colors.RED + "Correct Args: " + Colors.BLACK + "!ban <nick>");
			}
		} else {
			event.respond(Colors.RED + "You don't have proper access to use !ban");
		}
	}
	
	public void kb() {
		if (mc.check(user.getNick(), App.MOD)) {
			if (message.split(" ").length > 1) {
				if (mc.getLevel(user.getNick()) > mc.getLevel(message.split(" ")[1])) {
					User u = uc.getUser(message.split(" ")[1]);
					channel.send().ban(u.getNick());
					channel.send().kick(u);
				}
			} else {
				event.respond(Colors.RED + "Correct Args: " + Colors.BLACK + "!kb <nick>");
			}
		} else {
			event.respond(Colors.RED + "You don't have proper access to use !kb");
		}
	}
	
	public void kick() {
		if (mc.check(user.getNick(), App.MOD)) {
			if (message.split(" ").length > 1) {
				if (mc.getLevel(user.getNick()) >= mc.getLevel(message.split(" ")[1])) {
					User u = uc.getUser(message.split(" ")[1]);
					channel.send().kick(u);
				}
			} else {
				event.respond(Colors.RED + "Correct Args: " + Colors.BLACK + "!kick <nick>");
			}
		} else {
			event.respond(Colors.RED + "You don't have proper access to use !kick");
		}
	}
	
	public void join() {
		if (mc.check(user.getNick(), App.OWNER)) {
			if (message.split(" ").length > 2) {
				bot.sendIRC().joinChannel(message.split(" ")[1], message.split(" ")[2]);
			}else if (message.split(" ").length > 1) {
				bot.sendIRC().joinChannel(message.split(" ")[1]);
			} else {
				event.respond(Colors.RED + "Correct Args: " + Colors.BLACK + "!join <nick>");
			}
		} else {
			event.respond(Colors.RED + "You don't have proper access to use !join");
		}
	}
	
	public void quit() {
		if (mc.check(user.getNick(), App.OWNER)) {
			if (message.split(" ").length > 1) {
				String quitMessage = "";
				
				for (int i = 1; i < message.split(" ").length; i++) {
					quitMessage += message.split(" ")[i] + " ";
				}
				bot.sendIRC().quitServer(quitMessage);
				//mc.getApp().getGUI().close();
			} else {
				bot.sendIRC().quitServer();
				mc.getApp().getGUI().close();
			}
		}
	}
	
	public void enableGames() {
		if (mc.check(user.getNick(), App.ADMIN)) {
			if (!mc.getApp().getGameOn())
				mc.getApp().toogleGameOn();
			channel.send().message(Colors.GREEN + "Games enabled.");
		} else {
			event.respond(Colors.RED + "You don't have proper access to use !enableGames");
		}
	}
	
	public void disableGames() {
		if (mc.check(user.getNick(), App.ADMIN)) {
			if (mc.getApp().getGameOn())
				mc.getApp().toogleGameOn();
			channel.send().message(Colors.RED + "Games disabled.");
		} else {
			event.respond(Colors.RED + "You don't have proper access to use !enableGames");
		}
	}
	
	public void isGameEnabled() {
		if (mc.getApp().getGameOn()) {
			event.respond(Colors.GREEN + "Yes!");
		} else {
			event.respond(Colors.RED + "No!");
		}
	}
	
	public void gameType() throws InvalidGameTypeException {
		if (mc.check(user.getNick(), App.ADMIN)) {
			if (message.split(" ").length > 1) {
				if ("blackjack".equalsIgnoreCase(message.split(" ")[1]) || "0".equalsIgnoreCase(message.split(" ")[1])) {
					mc.getApp().setGameType();
					event.respond(Colors.GREEN + GameMessages.GAME_TYPE_CHANGED + Colors.BOLD + Colors.BLUE + "Blackjack");
				} else if ("poker".equalsIgnoreCase(message.split(" ")[1]) || "1".equalsIgnoreCase(message.split(" ")[1])) {
					mc.getApp().setGameType(1);
					event.respond(Colors.GREEN + GameMessages.GAME_TYPE_CHANGED + Colors.BOLD + Colors.BLUE + "Poker");
				} else if ("mafia".equalsIgnoreCase(message.split(" ")[1]) || "2".equalsIgnoreCase(message.split(" ")[1])) {
					mc.getApp().setGameType(2);
					event.respond(Colors.GREEN + GameMessages.GAME_TYPE_CHANGED + Colors.BOLD + Colors.BLUE + "Mafia");
				} else {
					event.respond(Colors.RED + "Invalid Game Type.");
				}
			} else {
				event.respond(Colors.RED + "Correct Args: " + Colors.BLACK + "!gameType <game>");
			}
		} else {
			event.respond(Colors.RED + "You don't have proper access to use !gameType");
		}
	}
	
	public Method call(String method) throws NoSuchMethodException {
		return getClass().getMethod(method);
	}
}
