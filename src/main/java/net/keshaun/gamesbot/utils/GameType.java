package net.keshaun.gamesbot.utils;

import java.util.ArrayList;
import java.util.List;

import org.pircbotx.User;

public abstract class GameType extends MySQLListener {
    protected List<User> players = new ArrayList<User>();
	
	protected abstract void setupDB();
    
    public List<User> getPlayers() {
    	return players;
    }
}
