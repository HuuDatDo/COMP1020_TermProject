import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import javax.swing.border.Border;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

class Start extends Frame {
	public BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	Start (int numPlayers) throws IOException {
		setTitle("Tetris");
		setSize(400*numPlayers, 600);
		setLocation(300, 100);
		add(new TetrisPanel(numPlayers));
		setVisible(true);
	}
}


class GameLeaderboardGUI extends JFrame {
    public JTextArea leaderboardTextArea;

    public GameLeaderboardGUI() {
        setTitle("Game Leaderboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));

        // Set a custom background image
        URL imageUrl = getClass().getResource("tetris-nes.jpg");
        if (imageUrl != null) {
            ImageIcon backgroundIcon = new ImageIcon(imageUrl);
            JLabel backgroundLabel = new JLabel(backgroundIcon);
            backgroundLabel.setBounds(0, 0, backgroundIcon.getIconWidth(), backgroundIcon.getIconHeight());
            add(backgroundLabel);
        }

        // Create a panel to hold the components
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create a title label
        JLabel titleLabel = new JLabel("LEADERBOARD");
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create a styled border for the text area
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 2, 2, 2, Color.WHITE),
                BorderFactory.createEmptyBorder(10, 10, 10, 10));

        leaderboardTextArea = new JTextArea();
        leaderboardTextArea.setEditable(false);
        leaderboardTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        leaderboardTextArea.setForeground(Color.WHITE);
        leaderboardTextArea.setBackground(new Color(0, 0, 0, 150));
        leaderboardTextArea.setOpaque(true);
        leaderboardTextArea.setBorder(border);
        panel.add(leaderboardTextArea, BorderLayout.CENTER);

        add(panel);

        pack();
        setLocationRelativeTo(null);
    }

    public void loadLeaderboardFromFile(String filePath) {
        List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    String playerName = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    leaderboardEntries.add(new LeaderboardEntry(playerName, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        leaderboardEntries.sort(Comparator.comparingInt(LeaderboardEntry::getScore).reversed());

        StringBuilder leaderboardText = new StringBuilder();
        leaderboardText.append(String.format("%-5s %-20s %s%n", "Rank", "Name", "Score")); //Need to fix the format

        int rank = 1;
        for (LeaderboardEntry entry : leaderboardEntries) {
            String rankEntry = String.format("%-5d", rank);
            String nameEntry = String.format("%-20s", entry.getPlayerName());
            String scoreEntry = String.format("%d", entry.getScore());
            leaderboardText.append(String.format("%s %s %s%n", rankEntry, nameEntry, scoreEntry));
            rank++;
        }

        leaderboardTextArea.setText(leaderboardText.toString());
    }

    public static class LeaderboardEntry {
        public final String playerName;
        public final int score;

        public LeaderboardEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }
    }
}



public class Main extends JFrame {

    public String username;

    public void writeLeaderboard(String username) throws IOException{
        File file = new File("leaderboard.txt");
        FileWriter leaderboard = new FileWriter(file, true);
        leaderboard.write(username + " ");
        leaderboard.close();
    }


    public void initButton() throws IOException{
        JFrame f=new JFrame("Login");
        JLabel jLabel2 = new javax.swing.JLabel();
        JLabel jLabel3 = new javax.swing.JLabel();
        JLabel jLabel1 = new javax.swing.JLabel();
        JButton jButton2 = new javax.swing.JButton();
        JButton jButton1 = new javax.swing.JButton();
        JButton jButton3 = new javax.swing.JButton();
        JTextField jTextField1 = new javax.swing.JTextField();
        JCheckBox jCheckBox1 = new javax.swing.JCheckBox();
        JLabel jLabel4 = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("TETRIS");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(350, 30, 107, 48);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("BE THE CHANGE TO CHANGE THE GAME");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(200, 80, 440, 40);

        jButton2.setBackground(new java.awt.Color(0, 204, 255));
        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon("1p.png")); // NOI18N
        jButton2.setText("Single Player Mode");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jButton2.isEnabled()){
                    username = jTextField1.getText();
                    System.out.print(username);
                    try{
                        FileWriter leaderboard = new FileWriter("leaderboard.txt", true);
                        leaderboard.write("\n" +username + " ");
                        leaderboard.close();
                    } catch (IOException exc){
                        System.out.println("An error occurred.");
                    }
                    try {
                        new Start(1);
                        f.setVisible(false);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(20, 200, 370, 100);

        jButton1.setBackground(new java.awt.Color(255, 255, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon("multiplayer.png")); // NOI18N
        jButton1.setText("Multiplayer Mode");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jButton1.isEnabled()){
                    username = jTextField1.getText();
                    System.out.print(username);
                    try{
                        FileWriter leaderboard = new FileWriter("leaderboard.txt", true);
                        leaderboard.write("\n" +username + " 0");
                        leaderboard.close();
                    } catch (IOException exc){
                        System.out.println("An error occurred.");
                    }
                    try {
                        new Start(2);
                        f.setVisible(false);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(20, 320, 370, 100);

        jButton3.setBackground(new java.awt.Color(102, 255, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon("leaderboard.png")); // NOI18N
        jButton3.setText("Leaderboard");
        getContentPane().add(jButton3);
        jButton3.setBounds(420, 200, 360, 100);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jButton3.isEnabled()){
                    GameLeaderboardGUI leaderboardGUI = new GameLeaderboardGUI();
                    leaderboardGUI.loadLeaderboardFromFile("leaderboard.txt");
                    leaderboardGUI.setVisible(true);
                    f.setVisible(false);
                }
            }
        });

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jTextField1.setText("Player Name");
        // jTextField1.addActionListener(new java.awt.event.ActionListener() {
        //     public void actionPerformed(java.awt.event.ActionEvent evt) {
        //         // jTextField1ActionPerformed(evt);
        //     }
        // });
        getContentPane().add(jTextField1);
        jTextField1.setBounds(330, 140, 150, 40);

        jCheckBox1.setBackground(new java.awt.Color(153, 153, 153));
        jCheckBox1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jCheckBox1.setText("Music");
        jCheckBox1.setIcon(new javax.swing.ImageIcon("music.png")); // NOI18N
        // jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
        //     public void actionPerformed(java.awt.event.ActionEvent evt) {
        //         jCheckBox1ActionPerformed(evt);
        //     }
        // });
        getContentPane().add(jCheckBox1);
        jCheckBox1.setBounds(420, 320, 360, 100);

        jLabel4.setIcon(new javax.swing.ImageIcon("tetris_background.jpg")); // NOI18N
        getContentPane().add(jLabel4);
        jLabel4.setBounds(-3, -4, 800, 460);
        
        f.add(jLabel1);f.add(jLabel2);f.add(jLabel3);f.add(jLabel4);f.add(jButton1);f.add(jButton2);f.add(jButton3);f.add(jTextField1); f.add(jCheckBox1);
        f.setSize(800,480);  
        f.setLayout(null);  
        f.setVisible(true);
    }   

    public static void main(String[] args) throws IOException {
        var login = new Main();
        login.initButton();
    }
}