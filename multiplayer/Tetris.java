import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

public class Tetris  {
	public int[][] board = new int[22][10];
	public int panelR, panelC;
	public TetrisPanel panel;
	public static int[] GLOBAL_DELAY = {800,720,630,550,470,380,300,220,130,100,80,80,80,70,70,70,30,30,30,20};

	public int GLOBAL_LOCK = 1000;

	public static Color[] c = {Color.GRAY, Color.YELLOW, Color.CYAN, Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED, Color.MAGENTA, Color.DARK_GRAY};
	public static Color ghostColor = Color.DARK_GRAY;
	public static Color UIColor = Color.GRAY;

	public static int[][] movec1 = {{0, -1, -1, 0, -1}, 
		{0, +1, +1, 0, +1},
		{0, +1, +1, 0, +1},
		{0, +1, +1, 0, +1},
		{0, +1, +1, 0, +1},
		{0, -1, -1, 0, -1},
		{0, -1, -1, 0, -1},
		{0, -1, -1, 0, -1}};
	public static int[][] mover1 = {{0, 0, +1, 0, -2}, 
		{0, 0, +1, 0, -2},
		{0, 0, -1, 0, +2},
		{0, 0, -1, 0, +2},
		{0, 0, +1, 0, -2},
		{0, 0, +1, 0, -2},
		{0, 0, -1, 0, +2},
		{0, 0, -1, 0, +2}};

	public static int[][] movec2 = {{0, -2, +1, -2, +1}, 
		{0, -1, +2, -1, +2},
		{0, -1, +2, -1, +2},
		{0, +2, -1, +2, -1},
		{0, +2, -1, +2, -1},
		{0, +1, -2, +1, -2},
		{0, +1, -2, +1, -2},
		{0, -2, +1, -2, +1}};
	public static int[][] mover2 = {{0, 0, 0, -1, +2}, 
		{0, 0, 0, +2, -1},
		{0, 0, 0, +2, -1},
		{0, 0, 0, +1, -2},
		{0, 0, 0, +1, -2},
		{0, 0, 0, -2, +1},
		{0, 0, 0, -2, +1},
		{0, 0, 0, -1, +2}};

	public Queue<Integer> hold = new ArrayDeque<Integer>();
	public Piece piece = new Piece();
	public Piece.Active current_piece = null;
	public int id;

	public int holdId = 0;
	public boolean isHolding = false;

	public int time = 0;
	public int delay = GLOBAL_DELAY[0];
	public int level = 0;
	public int lockTime = 0;
	public int scores = 0;

	public  int[] y_coordinates = {50, 100, 150, 200, 300};

	public boolean Pausing = false;
	public boolean Lose = false;

	public int combo = 0;

	public Timer t = new Timer();
	public TimerTask move = new TimerTask() {
		@Override
		public void run () {
			if (Pausing || Lose)
				return;

			synchronized (hold) {
				if (hold.size() < 4)
					for (int id : piece.getPermutation())
						hold.offer(id);
			}
			if (time >= delay) {
				if (current_piece == null)
					current_piece = piece.getActive(hold.poll());

				if (move(1, 0)) {
					lockTime = 0;
					time = 0;
				} else if (lockTime >= GLOBAL_LOCK) {
					Lose = true;
					for (int i = 0; i < 4; i+=1) {
						if (current_piece.position[i].r >= 0)
							board[current_piece.position[i].r][current_piece.position[i].c] = current_piece.id;
						if (current_piece.position[i].r >= 2)
							Lose = false;
					}
					if (Lose) {
						try{
							File file = new File("leaderboard.txt");
							FileWriter leaderboard = new FileWriter(file,true);
							System.out.println(leaderboard);
							leaderboard.write(String.valueOf(scores));
							leaderboard.write("\n");
							System.out.println("WRITING SCORE");
							leaderboard.close();
						}catch (IOException exc){
							System.out.println("An error occurred.");
						}
						System.out.println("YOU LOSE AND YOUR SCORE IS" + scores);
						panel.setGameOver();
					}
					synchronized (current_piece) {
						current_piece = null;
						isHolding = false;
						lockTime = 0;
					}

					int cleared = clearLines();
					if (cleared > 0){
						combo+=1;
					}
					else{
						combo = 0;
					}
					int send = cleared > 0 ? ((1 << (cleared-1))/2 + (combo/2)): 0; 
					panel.sendGarbage(id, send);
					adjustLevel();

					time = delay;
				}
				panel.repaint();
			}
			time+=1;
			lockTime+=1;
		}
	};
	Tetris (int panelC, int panelR, TetrisPanel panel, int id) {
		this.panelC = panelC;
		this.panelR = panelR;
		this.panel = panel;
		this.id = id;
		t.scheduleAtFixedRate(move, 1000, 1);
	}
	public void adjustLevel () {
		level = scores/4;
		if (level >= 20)
			delay = GLOBAL_DELAY[19];
		else
			delay = GLOBAL_DELAY[level];
	}

	public void displayGrid (Graphics graphics) {
		for (int i = 2; i < 22; i+=1) {
			for (int j = 0; j < 10; j+=1) {
				graphics.setColor(c[board[i][j]]);
				graphics.fillRect(panelC + j*25+10, panelR + i*25, 24, 24);
			}
		}
	}

	public void displayPieces (Graphics graphics) {
		if (current_piece == null)
			return;
		synchronized (current_piece) {
			int d = -1;
			boolean isValid = true;
			while (isValid) {
				d+=1;
				for (Piece.Point block : current_piece.position)
					if (block.r + d >= 0 && (block.r+d >= 22 || board[block.r+d][block.c] != 0))
						isValid = false;
			}
			d--;
			
			graphics.setColor(ghostColor);
			for (Piece.Point block : current_piece.position)
				if (block.r+d >= 2)
					graphics.fillRect(panelC + block.c*25+10, panelR + (block.r+d)*25, 24, 24);

			graphics.setColor(c[current_piece.id]);
			for (Piece.Point block : current_piece.position)
				if (block.r >= 2)
					graphics.fillRect(panelC + block.c*25+10, panelR + block.r*25, 24, 24);
		}
	}

	public void displayUI (Graphics graphics) {
		graphics.setColor(UIColor);
		graphics.drawString("SCORES: " + scores, panelC + 10, panelR + 10);
		graphics.drawString("SPEED: " + level, panelC + 10, panelR + 20);
		if (Pausing)
			graphics.drawString("PAUSED", panelC + 10, 30);
		if (Lose)
			graphics.drawString("GAMEOVER", panelC + 10, panelR + 40);
		// graphics.drawString("HOLD", panelC + 300, panelR + 300);
		// graphics.drawString("NEXT", panelC + 300, panelR + 50);
		for (int k = 0; k < 5; k+=1) {
			for (int i = 0; i < 2; i+=1) {
				for (int j = 0; j < 4; j+=1) {
					graphics.fillRect(panelC + j*20 + 300, panelR + i*20 + y_coordinates[k], 19, 19);
				}
			}
		}
		if (holdId != 0) {
			Piece.Active holdPiece = piece.getActive(holdId-1);
			graphics.setColor(c[holdPiece.id]);
			for (Piece.Point block : holdPiece.position) {
				graphics.fillRect(panelC + (block.c-3)*20+300, panelR + block.r*20 + y_coordinates[4], 19, 19);
			}
		}

		synchronized (hold) {
			int i = 0;
			for (int id : hold) {
				Piece.Active nextPiece = piece.getActive(id);
				graphics.setColor(c[nextPiece.id]);
				for (Piece.Point block : nextPiece.position) {
					graphics.fillRect(panelC + (block.c-3)*20+300, panelR + block.r*20 + y_coordinates[i], 19, 19);
				}
				i+=1;
				if (i >= 4)
					break;
			}
		}
	}

	public int clearLines () {
		int numCleared = 0;
		while (true) {
			int index = -1;
			for (int j = 0; j < 22; j+=1) {
				int cnt = 0;
				for (int i = 0; i < 10; i+=1) {
					cnt += board[j][i] != 0 ? 1 : 0;
				}
				if (cnt == 10) {
					index = j;
					break;
				}
			}
			if (index == -1)
				break;
			int[][] temp = new int[22][10];
			for (int i = 0; i < 22; i+=1)
				for (int j = 0; j < 10; j+=1)
					temp[i][j] = board[i][j];
			for (int i = 0; i < index+1; i+=1) {
				for (int j = 0; j < 10; j+=1) {
					if (i == 0)
						board[i][j] = 0;
					else
						board[i][j] = temp[i-1][j];
				}
			}
			scores+=1;
			numCleared+=1;
		}
		return numCleared;
	}
	public void restart () {
		current_piece = null;
		board = new int[22][10];
		hold.clear();
		level = 0;
		scores = 0;
		holdId = 0;
		isHolding = false;
		Lose = false;
	}
	
	public void rotate () {
		if (current_piece.id == 1)
			return;
		Piece.Point[] np = new Piece.Point[4];
		for (int i = 0; i < 4; i+=1) {
			int nr = current_piece.position[i].c - current_piece.loc + current_piece.lor;
			int nc = current_piece.position[i].r - current_piece.lor + current_piece.loc;
			np[i] = new Piece.Point(nr, nc);
		}
		int loc = current_piece.loc;
		int hic = current_piece.hic;
		for (int i = 0; i < 4; i+=1) {
			np[i].c = hic - (np[i].c-loc);
		}
		push(np, current_piece.state*2);
		panel.repaint();

	}

	public void push (Piece.Point[] position, int id) {
		for (int i = 0; i < 5; i+=1) {
			boolean valid = true;
			int dr = current_piece.id == 2 ? mover2[id][i] : mover1[id][i];
			int dc = current_piece.id == 2 ? movec2[id][i] : movec1[id][i];
			for (Piece.Point block : position) {
				if (block.r + dr < 0 || block.r + dr >= 22)
					valid = false;
				else if (block.c + dc < 0 || block.c + dc >= 10)
					valid = false;
				else if (board[block.r+dr][block.c+dc] != 0)
					valid = false;
			}
			if (valid) {
				for (int j = 0; j < 4; j+=1) {
					current_piece.position[j].r = position[j].r + dr;
					current_piece.position[j].c = position[j].c + dc;
				}
				current_piece.hic += dc;
				current_piece.loc += dc;
				current_piece.hir += dr;
				current_piece.lor += dr;
				if (id % 2 == 1)
					current_piece.state = (current_piece.state+3)%4;
				else
					current_piece.state = (current_piece.state+1)%4;
				return;
			}
		}
	}

	public boolean move (int dr, int dc) {
		if (current_piece == null)
			return false;
		for (Piece.Point block : current_piece.position) {
			if (block.r+dr < 0 || block.r+dr >= 22)
				return false;
			if (block.c+dc < 0 || block.c+dc >= 10)
				return false;
			if (board[block.r+dr][block.c+dc] != 0)
				return false;
		}
		for (int i = 0; i < 4; i+=1) {
			current_piece.position[i].r += dr;
			current_piece.position[i].c += dc;
		}
		current_piece.loc += dc;
		current_piece.hic += dc;
		current_piece.lor += dr;
		current_piece.hir += dr;
		return true;
	}
	public void holdPiece (int lines) {
		for (int i = 0; i < 22; i+=1) {
			for (int j = 0; j < 10; j+=1) {
				if (board[i][j] != 0 && i - lines < 0) {
					Lose = true;
					panel.setGameOver();
				} else if (i - lines >= 0){
					board[i-lines][j] = board[i][j];
				}
			}
		}
		for (int i = 21; i >= Math.max(0, 22-lines); i--) {
			for (int j = 0; j < 10; j+=1)
				board[i][j] = 8;
			board[i][(int)(Math.random()*8)] = 0;
		}
		if (current_piece == null) {
			panel.repaint();
			return;
		}
		boolean valid = false;
		while (!valid) {
			valid = true;
			for (Piece.Point block : current_piece.position) {
				if (block.r >= 0 && board[block.r][block.c] != 0)
					valid = false;
			}
			if (!valid)
				for (int i = 0; i < 4; i+=1)
					current_piece.position[i].r--;
		}
		panel.repaint();
	}
}
