package com.keshaun.gamesbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import com.keshaun.gamesbot.App;
import com.keshaun.gamesbot.objects.Card;

public class BlackjackCommands extends ListenerAdapter<PircBotX> {
    private List<User> players = new ArrayList<User>();
    private List<Card[]> hands = new ArrayList<Card[]>();
    private Card[] dealerHand = new Card[2];

    private User currentTurn;

    private Card[][] deck = new Card[13][4];
    private List<Card> used = new ArrayList<Card>();
    
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
                        deck[i][j] = new Card("A", suits[j]);
                        break;
                    case 10:
                        deck[i][j] = new Card("J", suits[j]);
                        break;
                    case 11:
                        deck[i][j] = new Card("Q", suits[j]);
                        break;
                    case 12:
                        deck[i][j] = new Card("K", suits[j]);
                        break;
                    default:
                        deck[i][j] = new Card(String.valueOf(i + 1), suits[j]);
                }
            }
        }
    }

    @Override
    public void onMessage(MessageEvent<PircBotX> event) throws Exception {
        String message = event.getMessage();
        String command = message.split(" ")[0];
        Channel channel = event.getChannel();
        PircBotX bot = event.getBot();
        Random rand = new Random();
        
        if (app.getGameType() == 0) {
            //Get a random card from the deck.
            if ("!randomCard".equalsIgnoreCase(command)) {
                int i = rand.nextInt(13);
                int j = rand.nextInt(4);

                event.respond(deck[i][j].toString());
            }

            if ("!startQueue".equalsIgnoreCase(command)) {
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

            if ("!join".equalsIgnoreCase(command)) {
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

            if ("!start".equalsIgnoreCase(command)) {
            	//TODO Create functionality for !start
            	if (app.getGameOn() && !app.getGameInProgress() && app.getGameQueue() && players.size() > 2) {
            		app.toogleGameQueue();
            		app.toogleGameInProgress();
            		dealAllHands();
            		currentTurn = players.get(0);
            	}
            }
        }
    }

    public String getPlayersList(List<User> list) {
        String listString = "Current Players (" + players.size() + "/10):";

        for (int i = 0; i < list.size(); i++) {
            listString += " " + list.get(i).getNick();
        }

        return listString;
    }

    public String displayCard(String card) {
        return null;
    }
    
    private Card[] dealHand() {
    	Card[] hand = new Card[2];
        Random rand = new Random();
    	
        int i = 0;
    	while(i < hand.length) {
            int num = rand.nextInt(13);
            int suit = rand.nextInt(4);
            if (!used.contains(deck[num][suit])) {
            	hand[i] = deck[num][suit];
            	used.add(deck[num][suit]);
            	i++;
            }
    	}
    	
    	return hand;
    }
    
    private void dealerHand() {
    	Card[] hand = new Card[2];
        Random rand = new Random();
    	
        int i = 0;
    	while(i < hand.length) {
            int num = rand.nextInt(13);
            int suit = rand.nextInt(4);
            if (!used.contains(deck[num][suit])) {
            	hand[i] = deck[num][suit];
            	used.add(deck[num][suit]);
            	i++;
            }
    	}
    	
    	dealerHand = hand;
    }
    
    private void dealAllHands() {
    	for (int i = 0; i < players.size(); i++) {
    		hands.add(i, dealHand());
    	}
    	
    	dealerHand();
    }
}
