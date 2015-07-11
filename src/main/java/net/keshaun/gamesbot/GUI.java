package net.keshaun.gamesbot;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import net.keshaun.gamesbot.exceptions.InvalidGameTypeException;
import net.keshaun.gamesbot.utils.ErrorMessages;
import net.keshaun.gamesbot.utils.GameMessages;

import org.pircbotx.Colors;
import org.pircbotx.User;

public class GUI {
    private static final Logger LOGGER = Logger.getLogger(GUI.class.getName());
	
    private App app;
	private JFrame frame;
	
	private JLabel playerList;
	
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
					app.getBot().getUserChannelDao().getAllChannels().first().send().message(Colors.GREEN + GameMessages.GAME_TYPE_CHANGED + Colors.BOLD + Colors.BLUE + name);
                    System.out.println(GameMessages.GAME_TYPE_CHANGED + name);
				} catch(InvalidGameTypeException e) {
					LOGGER.log(Level.SEVERE, ErrorMessages.INVALID_GAME_TYPE, e);
				}
			}
		};
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
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
		
		String playersText = "<html><center><h1>Player List</h1></center>";
		
		if (app.getGameOn()) {
			String name;
			switch(app.getGameType()) {
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
			playersText += "<h3>Current Game Type: " + name + "<br /><br />";
		}
		
		if (app.getGameOn() && (app.getGameQueue() || app.getGameInProgress())) {
			for (User user : app.getCurrentGameListener().getPlayers()) {
				playersText += user.getNick() + "<br />";
			}
		}
		
		playersText += "</html>";
		playerList = new JLabel(playersText, SwingConstants.CENTER);
		//playerList.setFont(new Font("Serif", Font.PLAIN, 30));
		playerList.setBounds(0, 0, screen.width, screen.height);
		playerList.setVerticalAlignment(JLabel.TOP);
		playerList.setVerticalTextPosition(JLabel.TOP);
		playerList.setMinimumSize(new Dimension(800, 600));
		this.frame.add(playerList);
		
		Timer timer = new Timer(500, null);
		ActionListener timerListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String playersText = "<html><center><h1>Player List</h1></center>";
				
				if (app.getGameOn()) {
					String name;
					switch(app.getGameType()) {
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
					playersText += "<h3>Current Game Type: " + name + "<br /><br />";
				}
				
				if (app.getGameOn() && (app.getGameQueue() || app.getGameInProgress())) {
					for (User user : app.getCurrentGameListener().getPlayers()) {
						playersText += user.getNick() + "<br />";
					}
				}
				
				playersText += "</html>";
				playerList.setText(playersText);
			}
		};
		timer.addActionListener(timerListener);
		timer.start();
		
    	this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.frame.setTitle("Bot GUI");
    	this.frame.add(toolbar, BorderLayout.SOUTH);
    	this.frame.setBounds(0, 0, screen.width, screen.height);
    	//this.frame.setSize(400, 300);
    	this.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    	this.frame.setResizable(true);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }
    
    public void close() {
    	frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}

