import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class TetrisPanel extends Panel implements KeyListener {
	private static final long serialVersionUID = -8444879183679955468L;

	// variables for double buffered display
	private BufferedImage bi;
	private Graphics graphics;

	// dimensions of the frame
	private Dimension dim;

	// constants for panel
	private final Color background = Color.BLACK;

	// Variable representing the number of players
	private int numOfPlayers;
	
	// the left and right portions of the panel
	Tetris[] screens;
	
	private BufferedReader br;
	private int[][] key;
	TetrisPanel (int numOfPlayers) {
		this.numOfPlayers = numOfPlayers;
		key = new int[numOfPlayers][6];
		screens = new Tetris[numOfPlayers];
		try {
			br = new BufferedReader(new FileReader("INPUT"));
			for (int i = 0; i < numOfPlayers; i++)
				for (int j = 0; j < 6; j++)
					key[i][j] = Integer.parseInt(br.readLine().trim());
		} catch (IOException ie) {
			System.out.println("INVALID INPUT SEQUENCE");
			System.exit(0);
		} 
		addKeyListener(this);
		for (int i = 0; i < numOfPlayers; i++)
			screens[i] = new Tetris(400*i, 0, this, i);
	}
	public void paint (Graphics g) {
		dim = getSize();
		bi = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);
		graphics = bi.getGraphics();
		update(g);
	}
	public void update (Graphics g) {
		// graphics.setColor(background);
		graphics.fillRect(0, 0, dim.width, dim.height);
		for (int i = 0; i < numOfPlayers; i++) {
			if (screens[i] == null)
				continue;
			screens[i].displayGrid(graphics);
			screens[i].displayPieces(graphics);
			screens[i].displayUI(graphics);
		}
		g.drawImage(bi, 0, 0, this);
	}

	@Override
	public void keyTyped (KeyEvent e) {}
	@Override
	public void keyReleased (KeyEvent e) {
		for (int i = 0; i < numOfPlayers; i++) {
			for (int j = 0; j < 6; j++) {
				if (e.getKeyCode() == key[i][j]) {
					if (screens[i].current_piece == null)
						break;
					if (j == 3)
						screens[i].delay = (screens[i].level >= 20 ? Tetris.GLOBAL_DELAY[19] : Tetris.GLOBAL_DELAY[screens[i].level]);
				}
			}
		}
	}
	@Override
	public void keyPressed (KeyEvent e) {
		// user input
		// three cases that handle when the user adjusts the game states (ACTIVE, PAUSED, CLOSEd)
		if (e.getKeyCode() == KeyEvent.VK_P) {
			boolean currentState = screens[0].isPaused;
			for (int i = 0; i < numOfPlayers; i++)
				screens[i].isPaused = !currentState;
			repaint();
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			System.exit(0);
		} else if (e.getKeyCode() == KeyEvent.VK_R) {
			for (int i = 0; i < numOfPlayers; i++)
				screens[i].restart();
			repaint();
			return;
		}
		if (screens[0].isPaused || screens[0].isGameOver)
			return;
		int keyCode = e.getKeyCode();
		for (int i = 0; i < numOfPlayers; i++) {
			for (int j = 0; j < 6; j++) {
				if (keyCode == key[i][j]) {
					if (screens[i].current_piece == null)
						break;
					switch (j) {
						case 0:
							screens[i].movePiece(0, -1);
							repaint();
							break;
						case 1:
							screens[i].movePiece(0, 1);
							repaint();
							break;
						case 2:
							screens[i].rotateRight();
							break;	
						case 3:
							screens[i].delay = (screens[i].level >= 20 ? Tetris.GLOBAL_DELAY[19] : Tetris.GLOBAL_DELAY[screens[i].level])/8;
							break;

						case 4:
							if (screens[i].isHolding)
								break;
							if (screens[i].holdId == 0) {
								screens[i].holdId = screens[i].current_piece.id;
								screens[i].current_piece = null;
							} else {
								int temp = screens[i].holdId;
								screens[i].holdId = screens[i].current_piece.id;
								screens[i].current_piece = screens[i].piece.getActive(temp-1);
							}
							screens[i].isHolding = true;
							screens[i].time = 1 << 30;
							break;
						case 5:
							screens[i].time = 1 << 30;
							screens[i].lockTime = 1 << 30;
							while(screens[i].movePiece(1, 0));
							break;
					}
				}
			}
		}
		repaint();
	}
	protected void setGameOver () {
		for (int i = 0; i < numOfPlayers; i++)
			screens[i].isGameOver = true;
	}
	protected void sendGarbage (int id, int send) {
		if (numOfPlayers == 1)
			return;
		int rand = (int)(Math.random()*(numOfPlayers-1));
		if (rand >= id)
			rand++;
		screens[rand].holdPiece(send);
//		System.out.println("SENT " + send);
	}
}
