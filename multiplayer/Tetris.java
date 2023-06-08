import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

public class Tetris  {
	public int[][] board = new int[22][10];
	public int window_x, window_y;
	public TetrisPanel gameWindow;
	public static int[] GLOBAL_DELAY = {800,720,630,550,470,380,300,220,130,100,80,80,80,70,70,70,30,30,30,20};

	public int GLOBAL_LOCK = 1000;

	public static Color[] y = {Color.GRAY, Color.YELLOW, Color.CYAN, Color.BLUE, Color.ORANGE, Color.GREEN, Color.RED, Color.MAGENTA, Color.DARK_GRAY};
	public static Color ghostColor = Color.DARK_GRAY;
	public static Color UIColor = Color.GRAY;

	public static int[][] move_down1 = {{0, -1, -1, 0, -1}, 
		{0, +1, +1, 0, +1},
		{0, +1, +1, 0, +1},
		{0, +1, +1, 0, +1},
		{0, +1, +1, 0, +1},
		{0, -1, -1, 0, -1},
		{0, -1, -1, 0, -1},
		{0, -1, -1, 0, -1}};

	public static int[][] move_right1 = {{0, 0, +1, 0, -2}, 
		{0, 0, +1, 0, -2},
		{0, 0, -1, 0, +2},
		{0, 0, -1, 0, +2},
		{0, 0, +1, 0, -2},
		{0, 0, +1, 0, -2},
		{0, 0, -1, 0, +2},
		{0, 0, -1, 0, +2}};

	public static int[][] move_down2 = {{0, -2, +1, -2, +1}, 
		{0, -1, +2, -1, +2},
		{0, -1, +2, -1, +2},
		{0, +2, -1, +2, -1},
		{0, +2, -1, +2, -1},
		{0, +1, -2, +1, -2},
		{0, +1, -2, +1, -2},
		{0, -2, +1, -2, +1}};

	public static int[][] move_right2 = {{0, 0, 0, -1, +2}, 
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
						if (current_piece.position[i].x >= 0)
							board[current_piece.position[i].x][current_piece.position[i].y] = current_piece.id;
						if (current_piece.position[i].x >= 2)
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
						gameWindow.setGameOver();
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
					gameWindow.sendGarbage(id, send);
					adjustLevel();

					time = delay;
				}
				gameWindow.repaint();
			}
			time+=1;
			lockTime+=1;
		}
	};
	Tetris (int window_y, int window_x, TetrisPanel gameWindow, int id) {
		this.window_y = window_y;
		this.window_x = window_x;
		this.gameWindow = gameWindow;
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
				graphics.setColor(y[board[i][j]]);
				graphics.fillRect(window_y + j*25+10, window_x + i*25, 24, 24);
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
				for (Piece.PieceShape block : current_piece.position)
					if (block.x + d >= 0 && (block.x+d >= 22 || board[block.x+d][block.y] != 0))
						isValid = false;
			}
			d--;
			
			graphics.setColor(ghostColor);
			for (Piece.PieceShape block : current_piece.position)
				if (block.x+d >= 2)
					graphics.fillRect(window_y + block.y*25+10, window_x + (block.x+d)*25, 24, 24);

			graphics.setColor(y[current_piece.id]);
			for (Piece.PieceShape block : current_piece.position)
				if (block.x >= 2)
					graphics.fillRect(window_y + block.y*25+10, window_x + block.x*25, 24, 24);
		}
	}

	public void displayUI (Graphics graphics) {
		graphics.setColor(UIColor);
		graphics.drawString("SCORES: " + scores, window_y + 10, window_x + 10);
		graphics.drawString("SPEED: " + level, window_y + 10, window_x + 20);
		if (Pausing)
			graphics.drawString("PAUSED", window_y + 10, 30);
		if (Lose)
			graphics.drawString("GAMEOVER", window_y + 10, window_x + 40);
		// graphics.drawString("HOLD", window_y + 300, window_x + 300);
		// graphics.drawString("NEXT", window_y + 300, window_x + 50);
		for (int k = 0; k < 5; k+=1) {
			for (int i = 0; i < 2; i+=1) {
				for (int j = 0; j < 4; j+=1) {
					graphics.fillRect(window_y + j*20 + 300, window_x + i*20 + y_coordinates[k], 19, 19);
				}
			}
		}
		if (holdId != 0) {
			Piece.Active holdPiece = piece.getActive(holdId-1);
			graphics.setColor(y[holdPiece.id]);
			for (Piece.PieceShape block : holdPiece.position) {
				graphics.fillRect(window_y + (block.y-3)*20+300, window_x + block.x*20 + y_coordinates[4], 19, 19);
			}
		}

		synchronized (hold) {
			int i = 0;
			for (int id : hold) {
				Piece.Active nextPiece = piece.getActive(id);
				graphics.setColor(y[nextPiece.id]);
				for (Piece.PieceShape block : nextPiece.position) {
					graphics.fillRect(window_y + (block.y-3)*20+300, window_x + block.x*20 + y_coordinates[i], 19, 19);
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
					if (board[j][i] != 0){
						cnt += 1;
					}
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
		Piece.PieceShape[] np = new Piece.PieceShape[4];
		for (int i = 0; i < 4; i+=1) {
			int new_x = current_piece.position[i].y - current_piece.lo_y + current_piece.lo_x;
			int new_y = current_piece.position[i].x - current_piece.lo_x + current_piece.lo_y;
			np[i] = new Piece.PieceShape(new_x, new_y);
		}
		int lo_y = current_piece.lo_y;
		int hi_y = current_piece.hi_y;
		for (int i = 0; i < 4; i+=1) {
			np[i].y = hi_y - (np[i].y-lo_y);
		}
		push(np, current_piece.state*2);
		gameWindow.repaint();

	}

	public void push (Piece.PieceShape[] position, int id) {
		for (int i = 0; i < 5; i+=1) {
			boolean canMove = true;
			int move_right;
			if (current_piece.id ==2){
				move_right = move_right2[id][i];
			}
			else{
				move_right = move_right1[id][i];
			}
			int move_down = current_piece.id == 2 ? move_down2[id][i] : move_down1[id][i];
			if (current_piece.id ==2){
				move_down = move_down2[id][i];
			}
			else{
				move_down = move_down1[id][i];
			}
			for (Piece.PieceShape block : position) {
				if (block.x + move_right < 0 || block.x + move_right >= 22)
					canMove = false;
				else if (block.y + move_down < 0 || block.y + move_down >= 10)
					canMove = false;
				else if (board[block.x+move_right][block.y+move_down] != 0)
					canMove = false;
			}
			if (canMove) {
				for (int j = 0; j < 4; j+=1) {
					current_piece.position[j].x = position[j].x + move_right;
					current_piece.position[j].y = position[j].y + move_down;
				}
				current_piece.hi_y += move_down;
				current_piece.lo_y += move_down;
				current_piece.hi_x += move_right;
				current_piece.lo_x += move_right;
				if (id % 2 == 1)
					current_piece.state = (current_piece.state+3)%4;
				else
					current_piece.state = (current_piece.state+1)%4;
				return;
			}
		}
	}

	public boolean move (int move_right, int move_down) {
		if (current_piece == null)
			return false;
		for (Piece.PieceShape block : current_piece.position) {
			if (block.x+move_right < 0 || block.x+move_right >= 22)
				return false;
			if (block.y+move_down < 0 || block.y+move_down >= 10)
				return false;
			if (board[block.x+move_right][block.y+move_down] != 0)
				return false;
		}
		for (int i = 0; i < 4; i+=1) {
			current_piece.position[i].x += move_right;
			current_piece.position[i].y += move_down;
		}
		current_piece.lo_y += move_down;
		current_piece.hi_y += move_down;
		current_piece.lo_x += move_right;
		current_piece.hi_x += move_right;
		return true;
	}
	public void holdPiece (int lines) {
		for (int i = 0; i < 22; i+=1) {
			for (int j = 0; j < 10; j+=1) {
				if (board[i][j] != 0 && i - lines < 0) {
					Lose = true;
					gameWindow.setGameOver();
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
			gameWindow.repaint();
			return;
		}
		boolean canMove = false;
		while (!canMove) {
			canMove = true;
			for (Piece.PieceShape block : current_piece.position) {
				if (block.x >= 0 && board[block.x][block.y] != 0)
					canMove = false;
			}
			if (!canMove)
				for (int i = 0; i < 4; i+=1)
					current_piece.position[i].x--;
		}
		gameWindow.repaint();
	}
}
