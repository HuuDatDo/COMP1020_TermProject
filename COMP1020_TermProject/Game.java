package COMP1020_TermProject;

import COMP1020_TermProject.Leaderboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;  
import java.awt.event.*;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

class Tetris extends JFrame {
    private JLabel statusbar;
    private JPanel mainPanel;
    private JPanel leaderboardPanel;

    public Tetris() {
        initUI();
    }

    private void initUI() {

        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BorderLayout());
        leaderboardPanel.setVisible(false);
        add(leaderboardPanel);
        
        var board = new Board(this);
        add(board);
        board.start();

        setTitle("Tetris");
        setSize(400, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    JLabel getStatusBar() {

        return statusbar;
    }
}

public class Game extends JFrame {

    public String username;

    public void writeLeaderboard(String username) throws IOException {
        File file = new File("leaderboard.txt");
        FileWriter leaderboard = new FileWriter(file, true);
        leaderboard.write(username + " ");
        leaderboard.close();
    }

    public void initButton() throws IOException {
        JFrame f = new JFrame("Login");
        f.setContentPane(new JLabel(new ImageIcon("Icons/tetrisWallpaper.jpg")));
        f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        Font font = new Font("SansSerif", Font.BOLD, 14);
        int mainMenuButtonWidth = 130;
        int mainMenuButtonHeight = 50;

        JLabel gameTitle = new JLabel("WELCOME TO TETRIS");
        gameTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        gameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        f.add(gameTitle);
        
        final JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(150, 20));
        tf.setAlignmentX(Component.CENTER_ALIGNMENT);
        tf.setFont(font);
        f.add(tf);

        ImageIcon playIcon = new ImageIcon("Icons/play.png");
        Image playScaledIcon = playIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Icon scaledPlayIcon = new ImageIcon(playScaledIcon);
        JButton b = new JButton("PLAY");
        b.setPreferredSize(new Dimension(mainMenuButtonWidth, mainMenuButtonHeight));
        b.setFont(font);
        b.setIcon(scaledPlayIcon);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tf.setText("Enter username.");
            }
        });
        f.add(b);
        f.add(Box.createVerticalGlue());

        ImageIcon scoreIcon = new ImageIcon("Icons/high-score.png");
        Image scoreScaledIcon = scoreIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Icon scaledScoreIcon = new ImageIcon(scoreScaledIcon);
        JButton l = new JButton("SCORES");
        l.setPreferredSize(new Dimension(mainMenuButtonWidth, mainMenuButtonHeight));
        l.setFont(font);
        l.setIcon(scaledScoreIcon);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        f.add(l);
        
        
        f.add(Box.createVerticalGlue());
        
        ImageIcon multiplayerIcon = new ImageIcon("Icons/multiplayer.png");
        Image multiplayerScaledIcon = multiplayerIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Icon scaledMultiplayerIcon = new ImageIcon(multiplayerScaledIcon);
        JButton multiplayerButton = new JButton("CO-OP");
        multiplayerButton.setPreferredSize(new Dimension(mainMenuButtonWidth, mainMenuButtonHeight));
        multiplayerButton.setFont(font);
        multiplayerButton.setIcon(scaledMultiplayerIcon);
        multiplayerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        f.add(multiplayerButton);

        JCheckBox s = new JCheckBox("SOUND");
        s.setFont(font);
        f.add(s);

        f.setSize(400, 400);
        f.setVisible(true);


        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (b.isEnabled()) {
                    System.out.println("Add Button is pressed");
                    username = tf.getText();
                    System.out.print(username);
                    try {
                        FileWriter leaderboard = new FileWriter("leaderboard.txt");
                        leaderboard.write(username + " ");
                        System.out.println("WRITING USERNAME");
                        leaderboard.close();
                    } catch (IOException exc) {
                        System.out.println("An error occurred.");
                    }
                    b.setVisible(false);
                    f.setVisible(false);
                    tf.setVisible(false);
                    EventQueue.invokeLater(() -> {
                        var game = new Tetris();
                        game.setVisible(true);
                    });
                }
                if (!b.isEnabled()) {
                    System.out.println("Add Button is not pressed");
                }
            }
        });

        l.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (l.isEnabled()) {
                    new Leaderboard(100);
                }
            }
        });

    }

    public static void main(String[] args) throws IOException {

        var login = new Game();
        login.initButton();
    }
}
