package COMP1020_TermProject;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.io.PrintWriter;
import java.io.IOException;


public class Leaderboard {
    JFrame leaderboardFrame = new JFrame();
    public String name;
    private int score = 0;
    private int time;
    static LinkedList<String> leaderboard = new LinkedList<>();
    Leaderboard(int score){
        this.score = score;
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader("leaderboard.txt"));
            Scanner sc = new Scanner(br);
            time = Integer.parseInt(sc.nextLine());
            while (sc.hasNextLine())
                leaderboard.add(sc.nextLine());
        }
        catch(Exception e){
            System.out.print(e);
        }
        initUI();
    }
    private void initUI() {
        leaderboardFrame.setTitle("Leaderboard");
        leaderboardFrame.setLayout(new BorderLayout());
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        List<String> leaderboardData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("leaderboard.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                leaderboardData.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(leaderboardData, new Comparator<String>() {
            @Override
            public int compare(String entry1, String entry2) {
                int score1 = Integer.parseInt(entry1.split(" ")[1]);
                int score2 = Integer.parseInt(entry2.split(" ")[1]);
                return Integer.compare(score2, score1);
            }
        });
        
        JLabel titleLabel = new JLabel("LEADERBOARD");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        leaderboardFrame.add(titleLabel, BorderLayout.NORTH);
    
        JLabel scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        leaderboardFrame.add(scoreLabel, BorderLayout.CENTER);
        
        JPanel leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));

        for (String entry : leaderboardData) {
            JLabel entryLabel = new JLabel(entry);
            entryLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            entryLabel.setHorizontalAlignment(JLabel.CENTER);
            leaderboardPanel.add(entryLabel);
        }
        leaderboardFrame.add(leaderboardPanel, BorderLayout.CENTER);

        leaderboardFrame.pack();
        leaderboardFrame.setVisible(true);
    }
}
