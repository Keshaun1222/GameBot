package com.keshaun.gamesbot.commands;

import com.keshaun.gamesbot.App;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.Random;

public class BlackjackCommands extends ListenerAdapter<PircBotX> {
    public ArrayList<User> players = new ArrayList<User>();
    public ArrayList<String[]> hands = new ArrayList<String[]>();
    public String[] dealerHand = new String[2];

    public User currentTurn;

    public String[][] deck = new String[13][4];
    public ArrayList<String> used = new ArrayList<String>();
    
    private App app;

    public BlackjackCommands(App app) {
		/*
		 * Colors for Suits:
		 * * Diamond - Red
		 * * Clubs - Black
		 * * Hearts - Brown
		 * * Spades - Blue
		 */
    	this.app = app;
    	
        String[] suits = {"D", "C", "H", "S"};
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < 4; j++) {
                switch (i) {
                    case 0:
                        deck[i][j] = "A" + "-" + suits[j];
                        break;
                    case 10:
                        deck[i][j] = "J" + "-" + suits[j];
                        break;
                    case 11:
                        deck[i][j] = "Q" + "-" + suits[j];
                        break;
                    case 12:
                        deck[i][j] = "K" + "-" + suits[j];
                        break;
                    default:
                        deck[i][j] = (i + 1) + "-" + suits[j];
                }
            }
        }
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String message = event.getMessage();
        Channel channel = event.getChannel();
        PircBotX bot = event.getBot();
        Random rand = new Random();
        
        if (app.getGameType() == 0) {
            //Get a random card from the deck.
            if (message.equalsIgnoreCase("!randomCard")) {
                int i = rand.nextInt(13);
                int j = rand.nextInt(4);

                event.respond(displayCard(deck[i][j]));
            }

            if (message.equalsIgnoreCase("!startQueue")) {
                if (app.getGameOn() && !(app.getGameInProgress() || app.getGameQueue())) {
                    app.toogleGameQueue();
                    players.add(event.getUser());
                    channel.send().message(Colors.BLUE + "A game queue has been started by " + Colors.BOLD + event.getUser().getNick() + Colors.NORMAL + Colors.BLUE + ". Enter !join to join the queue.");
                    channel.send().message(Colors.BLUE + getPlayersList(players));
                } else if (!app.getGameOn()) {
                    event.respond(Colors.RED + "Games aren't enabled right now!");
                } else if (app.getGameInProgress()) {
                    event.respond(Colors.RED + "There is already a game in progress!");
                } else if (app.getGameQueue()) {
                    event.respond(Colors.RED + "There is a queue going already!");
                }
            }

            if (message.equalsIgnoreCase("!join")) {
                if (app.getGameOn() && !app.getGameInProgress() && app.getGameQueue()) {
                    if (!players.contains(event.getUser())) {
                        players.add(event.getUser());
                        channel.send().message(Colors.BLUE + Colors.BOLD + event.getUser().getNick() + Colors.NORMAL + " has joined the queue.");
                        channel.send().message(Colors.BLUE + getPlayersList(players));
                    } else {
                        event.respond(Colors.RED + "You are already in the queue!");
                    }
                } else if (!app.getGameOn()) {
                    event.respond(Colors.RED + "Games aren't enabled right now!");
                } else if (app.getGameInProgress()) {
                    event.respond(Colors.RED + "There is already a game in progress!");
                } else if (!app.getGameQueue()) {
                    event.respond(Colors.RED + "There is no queue going!");
                }
            }

            if (message.equalsIgnoreCase("!start")) {

            }
        }
    }

    public String getPlayersList(ArrayList<User> list) {
        String listString = "Current Players (" + players.size() + "/10):";

        for (int i = 0; i < list.size(); i++) {
            listString += " " + list.get(i).getNick();
        }

        return listString;
    }

    public String displayCard(String card) {
        String value = card.split("-")[0];
        String suit = card.split("-")[1];
        String color;

        if (suit.equalsIgnoreCase("D")) {
            color = Colors.RED;
        } else if (suit.equalsIgnoreCase("C")) {
            color = Colors.BLACK;
        } else if (suit.equalsIgnoreCase("H")) {
            color = Colors.BROWN;
        } else {
            color = Colors.BLUE;
        }

        return color + "[" + value + "]";
    }
}
