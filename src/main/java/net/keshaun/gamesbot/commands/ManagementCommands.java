package net.keshaun.gamesbot.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import net.keshaun.gamesbot.App;
import net.keshaun.gamesbot.commands.utils.ManagementUtil;
import net.keshaun.gamesbot.utils.ErrorMessages;
import net.keshaun.gamesbot.utils.MySQLListener;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

public class ManagementCommands extends MySQLListener {
	// private static final Logger LOGGER =
	// Logger.getLogger(ManagementCommands.class.getName());
	private ManagementUtil util;
	private App app;

	public ManagementCommands(App app) {
		super();
		this.app = app;
	}

	public App getApp() {
		return app;
	}

	@Override
	public void onMessage(MessageEvent<PircBotX> event) throws Exception {
		util = new ManagementUtil(this, event);
		String message = event.getMessage();
		String command = message.split(" ")[0];
		if ("!".equals(command.substring(0, 1))) {
			String method = command.substring(1);
			Method call = util.call(method);
			try {
				call.invoke(util);
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.SEVERE, ErrorMessages.ILLEGAL_ACCESS, e);
			} catch (InvocationTargetException e) {
				LOGGER.log(Level.SEVERE, ErrorMessages.INVOCATION_TARGET, e);
			}
		}
	}

	@Override
	protected void setupDB() {
		Statement stmt = null;
		try {
			openConnection();
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS access (id int(10) NOT NULL AUTO_INCREMENT, name VARCHAR(50) NOT NULL, level TINYINT(3) NOT NULL, PRIMARY KEY(id), UNIQUE(name));";
			stmt.executeUpdate(sql);
			stmt.close();
			closeConnection();
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, ErrorMessages.SQL, e);
		}
	}

	public void addUser(String user, int level) {
		openConnection();
		if (getLevel(user) < level) {
			String sql = "REPLACE INTO access VALUES (NULL, ?,?)";
			PreparedStatement stmt = null;
			try {
				stmt = c.prepareStatement(sql);
				stmt.setString(1, user);
				stmt.setInt(2, level);
				stmt.executeUpdate();
				closeConnection();
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, ErrorMessages.SQL, e);
			}
		}
	}

	public int getLevel(String user) {
		openConnection();
		String sql = "SELECT COUNT(*) as there, level FROM access WHERE name = ?";
		PreparedStatement stmt = null;
		int level = 1;
		try {
			stmt = c.prepareStatement(sql);
			stmt.setString(1, user);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if (rs.getInt("there") > 0)
				level = rs.getInt("level");
			rs.close();
			stmt.close();
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
