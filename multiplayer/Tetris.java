import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

public class Tetris  {
	// grid of color ids that stores what kind of block is where
	public int[][] grid = new int[22][10];

	// dimensions of the frame
	public int panelR, panelC;

	// Big panel
	public TetrisPanel panel;

	// the delay values for levels: the array index corresponds to the level. After level 20 the delay remains consistent
	public static int[] GLOBAL_DELAY = {800,720,630,550,470,380,300,220,130,100,80,80,80,70,70,70,30,30,30,20};

	// the global delay lock value
	public int GLOBAL_LOCK = 1000;

	/*
	 * Colors representing the different type of blocks
	 * light gray = empty square
	 * yellow = O
	 * cyan = I
	 * blue = L
	 * orange = J
	 * green = S
	 * red = Z
	 * Magenta = T
	 */
	public static Color[] c = {Color.GRAY, Color.YELLOW, Color.CYAN, Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED, Color.MAGENTA, Color.DARK_GRAY};
	public static Color ghostColor = Color.DARK_GRAY;
	public static Color UIColor = Color.GRAY;

	// Kick cases for J L S T Z blocks
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

	// Kick cases for I block
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

	// Handles the queue for pieces
	public Queue<Integer> bag = new ArrayDeque<Integer>();
	// Generates the pieces
	public Piece p = new Piece();
	// Represents the current active piece
	public Piece.Active current_piece = null;
	// Represents the ID of the current screen
	public int id;

	// Variables to manage the hold mechanism
	public int holdId = 0;
	public boolean isHolding = false;

	// Timing and level variables
	public int time = 0;
	public int delay = GLOBAL_DELAY[0];
	public int level = 0;
	public int lockTime = 0;
	public int linesCleared = 0;

	// constants for UI
	public  int[] dy = {50, 100, 150, 200, 300};

	// Game state variables
	public boolean isPaused = false;
	public boolean isGameOver = false;

	public int combo = 0;

	// Thread that manages the gravity of the pieces
	public Timer t = new Timer();
	public TimerTask move = new TimerTask() {
		@Override
		public void run () {
			// checking for game states
			if (isPaused || isGameOver)
				return;

			// refill the queue if it is close to empty
			synchronized (bag) {
				if (bag.size() < 4)
					for (int id : p.getPermutation())
						bag.offer(id);
			}
			if (time >= delay) {
				// getting a new piece
				if (current_piece == null)
					current_piece = p.getActive(bag.poll());

				// attempting to move the piece
				if (movePiece(1, 0)) {
					lockTime = 0;
					time = 0;
				} else if (lockTime >= GLOBAL_LOCK) {
					// the piece cannot be moved down any further and the lock delay has expired then place the piece and check for gameover
					isGameOver = true;
					for (int i = 0; i < 4; i++) {
						if (current_piece.pos[i].r >= 0)
							grid[current_piece.pos[i].r][current_piece.pos[i].c] = current_piece.id;
						if (current_piece.pos[i].r >= 2)
							isGameOver = false;
					}
					if (isGameOver) {
						try{
							File file = new File("leaderboard.txt");
							FileWriter leaderboard = new FileWriter(file,true);
							System.out.println(leaderboard);
							leaderboard.write(String.valueOf(linesCleared));
							leaderboard.write("\n");
							System.out.println("WRITING SCORE");
							leaderboard.close();
						}catch (IOException exc){
							System.out.println("An error occurred.");
						}
						System.out.println("YOU LOSE AND YOUR SCORE IS" + linesCleared);
						panel.setGameOver();
					}
					// set the piece down and allow the user to hold a piece. The lock time is also reset
					synchronized (current_piece) {
						current_piece = null;
						isHolding = false;
						lockTime = 0;
					}

					// clear the lines and adjust the level
					int cleared = clearLines();
					if (cleared > 0)
						combo++;
					else
						combo = 0;
					int send = cleared > 0 ? ((1 << (cleared-1))/2 + (combo/2)): 0; 
					panel.sendGarbage(id, send);
					adjustLevel();

					// immediately get another piece
					time = delay;
				}
				panel.repaint();
			}
			time++;
			lockTime++;
		}
	};
	Tetris (int panelC, int panelR, TetrisPanel panel, int id) {
		this.panelC = panelC;
		this.panelR = panelR;
		this.panel = panel;
		this.id = id;
		t.scheduleAtFixedRate(move, 1000, 1);
	}
	// adjust the level based on the number of lines cleared
	public void adjustLevel () {
		level = linesCleared/4;
		if (level >= 20)
			delay = GLOBAL_DELAY[19];
		else
			delay = GLOBAL_DELAY[level];
	}

	// paints the grid based on the color id values in the 2D Array
	public void displayGrid (Graphics graphics) {
		for (int i = 2; i < 22; i++) {
			for (int j = 0; j < 10; j++) {
				graphics.setColor(c[grid[i][j]]);
				graphics.fillRect(panelC + j*25+10, panelR + i*25, 24, 24);
			}
		}
	}
	// paints the current piece
	public void displayPieces (Graphics graphics) {
		if (current_piece == null)
			return;
		synchronized (current_piece) {
			int d = -1;
			// displaying the ghost piece
			boolean isValid = true;
			while (isValid) {
				d++;
				for (Piece.Point block : current_piece.pos)
					if (block.r + d >= 0 && (block.r+d >= 22 || grid[block.r+d][block.c] != 0))
						isValid = false;
			}
			d--;
			// painting the ghost piece and the active piece
			graphics.setColor(ghostColor);
			for (Piece.Point block : current_piece.pos)
				if (block.r+d >= 2)
					graphics.fillRect(panelC + block.c*25+10, panelR + (block.r+d)*25, 24, 24);

			graphics.setColor(c[current_piece.id]);
			for (Piece.Point block : current_piece.pos)
				if (block.r >= 2)
					graphics.fillRect(panelC + block.c*25+10, panelR + block.r*25, 24, 24);
		}
	}
	// paints the user interface
	public void displayUI (Graphics graphics) {
		graphics.setColor(UIColor);
		graphics.drawString("SCORES: " + linesCleared, panelC + 10, panelR + 10);
		graphics.drawString("SPEED: " + level, panelC + 10, panelR + 20);
		if (isPaused)
			graphics.drawString("PAUSED", panelC + 10, 30);
		if (isGameOver)
			graphics.drawString("GAMEOVER -- Q FOR QUIT; R FOR RESTART", panelC + 10, panelR + 40);
		graphics.drawString("HOLD", panelC + 300, panelR + 300);
		graphics.drawString("NEXT", panelC + 300, panelR + 50);
		for (int k = 0; k < 5; k++) {
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 4; j++) {
					graphics.fillRect(panelC + j*20 + 300, panelR + i*20 + dy[k], 19, 19);
				}
			}
		}
		// paints the hold piece
		if (holdId != 0) {
			Piece.Active holdPiece = p.getActive(holdId-1);
			graphics.setColor(c[holdPiece.id]);
			for (Piece.Point block : holdPiece.pos) {
				graphics.fillRect(panelC + (block.c-3)*20+300, panelR + block.r*20 + dy[4], 19, 19);
			}
		}
		// paints the queue of blocks
		synchronized (bag) {
			int i = 0;
			for (int id : bag) {
				Piece.Active nextPiece = p.getActive(id);
				graphics.setColor(c[nextPiece.id]);
				for (Piece.Point block : nextPiece.pos) {
					graphics.fillRect(panelC + (block.c-3)*20+300, panelR + block.r*20 + dy[i], 19, 19);
				}
				i++;
				if (i >= 4)
					break;
			}
		}
	}
	// Post condition: any full lines are cleared and the respective variable is incremented
	public int clearLines () {
		int numCleared = 0;
		while (true) {
			// checking if there is a line that is full
			int index = -1;
			for (int j = 0; j < 22; j++) {
				int cnt = 0;
				for (int i = 0; i < 10; i++) {
					cnt += grid[j][i] != 0 ? 1 : 0;
				}
				if (cnt == 10) {
					index = j;
					break;
				}
			}
			if (index == -1)
				break;
			// removing the full lines one by one
			int[][] temp = new int[22][10];
			for (int i = 0; i < 22; i++)
				for (int j = 0; j < 10; j++)
					temp[i][j] = grid[i][j];
			for (int i = 0; i < index+1; i++) {
				for (int j = 0; j < 10; j++) {
					if (i == 0)
						grid[i][j] = 0;
					else
						grid[i][j] = temp[i-1][j];
				}
			}
			linesCleared++;
			numCleared++;
		}
		return numCleared;
	}
	public void restart () {
		current_piece = null;
		grid = new int[22][10];
		bag.clear();
		level = 0;
		linesCleared = 0;
		holdId = 0;
		isHolding = false;
		isGameOver = false;
	}
	// attempt to rotate the piece counterclockwise
	// Post condition: the current piece will be rotated counterclockwise if there is one case (out of five) that work
	public void rotateLeft () {
		if (current_piece.id == 1)
			return;
		Piece.Point[] np = new Piece.Point[4];
		for (int i = 0; i < 4; i++) {
			int nr = current_piece.pos[i].c - current_piece.loc + current_piece.lor;
			int nc = current_piece.pos[i].r - current_piece.lor + current_piece.loc;
			np[i] = new Piece.Point(nr, nc);
		}
		int lor = current_piece.lor;
		int hir = current_piece.hir;
		for (int i = 0; i < 4; i++) {
			np[i].r= hir - (np[i].r-lor);
		}
		kick(np, current_piece.state*2+1);
		panel.repaint();
	}
	// attempt to rotate the piece clockwise
	// Post condition: the current piece will be rotated clockwise if there is one case (out of five) that work
	public void rotateRight () {
		if (current_piece.id == 1)
			return;
		Piece.Point[] np = new Piece.Point[4];
		for (int i = 0; i < 4; i++) {
			int nr = current_piece.pos[i].c - current_piece.loc + current_piece.lor;
			int nc = current_piece.pos[i].r - current_piece.lor + current_piece.loc;
			np[i] = new Piece.Point(nr, nc);
		}
		int loc = current_piece.loc;
		int hic = current_piece.hic;
		for (int i = 0; i < 4; i++) {
			np[i].c = hic - (np[i].c-loc);
		}
		kick(np, current_piece.state*2);
		panel.repaint();

	}
	// handles the kick cases
	// Post condition: rotates the piece according to the state of the rotation
	// this method performs the actual rotation and copies the positions of the blocks into the active block
	public void kick (Piece.Point[] pos, int id) {
		for (int i = 0; i < 5; i++) {
			boolean valid = true;
			int dr = current_piece.id == 2 ? mover2[id][i] : mover1[id][i];
			int dc = current_piece.id == 2 ? movec2[id][i] : movec1[id][i];
			for (Piece.Point block : pos) {
				if (block.r + dr < 0 || block.r + dr >= 22)
					valid = false;
				else if (block.c + dc < 0 || block.c + dc >= 10)
					valid = false;
				else if (grid[block.r+dr][block.c+dc] != 0)
					valid = false;
			}
			if (valid) {
				for (int j = 0; j < 4; j++) {
					current_piece.pos[j].r = pos[j].r + dr;
					current_piece.pos[j].c = pos[j].c + dc;
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
	// attempts to move the active piece
	// Post-condition: will return false if it cannot move and true if it can move
	public boolean movePiece (int dr, int dc) {
		if (current_piece == null)
			return false;
		for (Piece.Point block : current_piece.pos) {
			if (block.r+dr < 0 || block.r+dr >= 22)
				return false;
			if (block.c+dc < 0 || block.c+dc >= 10)
				return false;
			if (grid[block.r+dr][block.c+dc] != 0)
				return false;
		}
		for (int i = 0; i < 4; i++) {
			current_piece.pos[i].r += dr;
			current_piece.pos[i].c += dc;
		}
		current_piece.loc += dc;
		current_piece.hic += dc;
		current_piece.lor += dr;
		current_piece.hir += dr;
		return true;
	}
	public void addGarbage (int lines) {
		for (int i = 0; i < 22; i++) {
			for (int j = 0; j < 10; j++) {
				if (grid[i][j] != 0 && i - lines < 0) {
					isGameOver = true;
					panel.setGameOver();
				} else if (i - lines >= 0){
					grid[i-lines][j] = grid[i][j];
				}
			}
		}
		for (int i = 21; i >= Math.max(0, 22-lines); i--) {
			for (int j = 0; j < 10; j++)
				grid[i][j] = 8;
			grid[i][(int)(Math.random()*8)] = 0;
		}
		if (current_piece == null) {
			panel.repaint();
			return;
		}
		boolean valid = false;
		while (!valid) {
			valid = true;
			for (Piece.Point block : current_piece.pos) {
				if (block.r >= 0 && grid[block.r][block.c] != 0)
					valid = false;
			}
			if (!valid)
				for (int i = 0; i < 4; i++)
					current_piece.pos[i].r--;
		}
		panel.repaint();
	}
}
