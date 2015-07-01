package net.keshaun.gamesbot;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import net.keshaun.gamesbot.exceptions.InvalidGameTypeException;
import net.keshaun.gamesbot.utils.ErrorTexts;
import net.keshaun.gamesbot.utils.GameMessages;
import org.pircbotx.Colors;

public class GUI {
    private static final Logger LOGGER = Logger.getLogger(GUI.class.getName());
	
    App app;
	JFrame frame;
	
    public GUI(App app) {
    	this.app = app;
    	this.frame = new JFrame();
    }
    
    public void show() {
    	ActionListener gameTypeListener = new ActionListener() {
    		public void actionPerformed(ActionEvent ae) {
				try {
					app.setGameType(Integer.parseInt(ae.getActionCommand()));
					String name;
					switch(Integer.parseInt(ae.getActionCommand())) {
						case 1:
							name = "Poker";
							break;
						case 2:
							name = "Mafia";
							break;
						default:
							name = "Blackjack";
							break;
					}
					app.bot.getUserChannelDao().getAllChannels().first().send().message(Colors.GREEN + GameMessages.GAME_TYPE_CHANGED + Colors.BOLD + Colors.BLUE + name);
                    System.out.println(GameMessages.GAME_TYPE_CHANGED + name);
				} catch(InvalidGameTypeException e) {
					LOGGER.log(Level.SEVERE, ErrorTexts.INVALID_GAME_TYPE, e);
				}
			}
		};
		
		JPanel toolbar = new JPanel();
    	
    	JButton bjButton = new JButton("BlackJack");
    	bjButton.setActionCommand(String.valueOf(0));
		bjButton.addActionListener(gameTypeListener);
		toolbar.add(bjButton);
		
		JButton pokerButton = new JButton("Poker");
		pokerButton.setActionCommand(String.valueOf(1));
		pokerButton.addActionListener(gameTypeListener);
		toolbar.add(pokerButton);
		
		JButton mafiaButton = new JButton("Mafia");
		mafiaButton.setActionCommand(String.valueOf(2));
		mafiaButton.addActionListener(gameTypeListener);
		toolbar.add(mafiaButton);
		
    	this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.frame.setTitle("Bot GUI");
    	this.frame.add(toolbar, BorderLayout.SOUTH);
    	this.frame.setSize(400, 300);
    	this.frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

