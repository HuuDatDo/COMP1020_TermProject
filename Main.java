package COMP1020_TermProject;

import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import javax.swing.border.Border;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.*;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;



class GameLeaderboardGUI extends JFrame {
    private JTextArea leaderboardTextArea;

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

    private static class LeaderboardEntry {
        private final String playerName;
        private final int score;

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
        JLabel jLabel4 = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("TETRIS");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(350, 50, 107, 48);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("BE THE CHANGE TO CHANGE THE GAME");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(200, 110, 440, 40);

        jButton2.setBackground(new java.awt.Color(0, 204, 255));
        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon("C:\\Users\\HP\\Downloads\\1p.png")); // NOI18N
        jButton2.setText("Single Player Mode");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jButton2.isEnabled()){
                    try {
                        new Window(1);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(20, 170, 370, 100);

        jButton1.setBackground(new java.awt.Color(255, 255, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\HP\\Downloads\\p2.jpg")); // NOI18N
        jButton1.setText("Multiplayer Mode");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jButton1.isEnabled()){
                    try {
                        new Window(2);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(20, 290, 370, 100);

        jButton3.setBackground(new java.awt.Color(102, 255, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon("C:\\Users\\HP\\Downloads\\leaderboard.png")); // NOI18N
        jButton3.setText("Leaderboard");
        getContentPane().add(jButton3);
        jButton3.setBounds(420, 170, 360, 100);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (jButton3.isEnabled()){
                    GameLeaderboardGUI leaderboardGUI = new GameLeaderboardGUI();
                    leaderboardGUI.loadLeaderboardFromFile("leaderboard.txt");
                    leaderboardGUI.setVisible(true);
                }
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon("C:\\Users\\HP\\Downloads\\tetris_background.jpg")); // NOI18N
        getContentPane().add(jLabel4);
        jLabel4.setBounds(-3, -4, 800, 460);
        
        f.add(jLabel1);f.add(jLabel2);f.add(jLabel3);f.add(jLabel4);f.add(jButton1);f.add(jButton2);f.add(jButton3);
        f.setSize(800,480);  
        f.setLayout(null);  
        f.setVisible(true);

    }   

    public static void main(String[] args) throws IOException {
        var login = new Main();
        login.initButton();
        // new gui();
    }
}


class GUI extends javax.swing.JFrame {
    public GUI() {
        initComponents();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        JFrame f=new JFrame("Login");
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("TETRIS");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(350, 50, 107, 48);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("BE THE CHANGE TO CHANGE THE GAME");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(200, 110, 440, 40);

        jButton2.setBackground(new java.awt.Color(0, 204, 255));
        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon("C:\\Users\\HP\\Downloads\\1p.png")); // NOI18N
        jButton2.setText("Single Player Mode");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(20, 170, 370, 100);

        jButton1.setBackground(new java.awt.Color(255, 255, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\HP\\Downloads\\p2.jpg")); // NOI18N
        jButton1.setText("Multiplayer Mode");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(20, 290, 370, 100);

        jButton3.setBackground(new java.awt.Color(102, 255, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon("C:\\Users\\HP\\Downloads\\leaderboard.png")); // NOI18N
        jButton3.setText("Leaderboard");
        getContentPane().add(jButton3);
        jButton3.setBounds(420, 170, 360, 100);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon("C:\\Users\\HP\\Downloads\\tetris_background.jpg")); // NOI18N
        getContentPane().add(jLabel4);
        jLabel4.setBounds(-3, -4, 800, 460);
        
        f.add(jLabel1);f.add(jLabel2);f.add(jLabel3);f.add(jLabel4);f.add(jButton1);f.add(jButton2);f.add(jButton3);
        f.setSize(800,800);  
        f.setLayout(null);  
        f.setVisible(true);
        pack();
    }// </editor-fold>                        

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        try {
            System.out.println("PRESSING");
            new Window(1);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }                                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        try {
            new Window(2);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }                           
    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent ent){
        GameLeaderboardGUI leaderboardGUI = new GameLeaderboardGUI();
        leaderboardGUI.loadLeaderboardFromFile("leaderboard.txt");
        leaderboardGUI.setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            new GUI().setVisible(true);
        }
        });
    }                 
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;             
}
