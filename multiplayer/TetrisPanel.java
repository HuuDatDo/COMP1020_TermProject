import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class TetrisPanel extends Panel implements KeyListener {
	public static final long serialVersionUID = -8444879183679955468L;

	public BufferedImage bi;
	public Graphics graphics;
	public Dimension dim;

	public int numOfPlayers;
	Tetris[] screens;
	
	public BufferedReader br;
	public int[][] key;
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
			boolean currentState = screens[0].Pausing;
			for (int i = 0; i < numOfPlayers; i++)
				screens[i].Pausing = !currentState;
			repaint();
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			System.exit(0);
		} else if (e.getKeyCode() == KeyEvent.VK_R) {
			for (int i = 0; i < numOfPlayers; i++)
				screens[i].restart();
			repaint();
			return;
		}
		if (screens[0].Pausing || screens[0].Lose)
			return;
		int keyCode = e.getKeyCode();
		for (int i = 0; i < numOfPlayers; i++) {
			for (int j = 0; j < 6; j++) {
				if (keyCode == key[i][j]) {
					if (screens[i].current_piece == null)
						break;

					if (j==0){
						screens[i].move(0, -1);
						repaint();
					}
					if (j==1){
						screens[i].move(0, 1);
						repaint();
					}
					if (j==2){
						screens[i].rotate();
					}
					if (j==3){
						screens[i].delay = (screens[i].level >= 20 ? Tetris.GLOBAL_DELAY[19] : Tetris.GLOBAL_DELAY[screens[i].level])/8;
					}
					if (j==4){
						if (screens[i].isHolding)
								break;
							if (screens[i].holdId == 0) {
								screens[i].holdId = screens[i].current_piece.id;
								screens[i].current_piece = null;
							} 
							else {
								int temp = screens[i].holdId;
								screens[i].holdId = screens[i].current_piece.id;
								screens[i].current_piece = screens[i].piece.getActive(temp-1);
							}
							screens[i].isHolding = true;
							screens[i].time = 1 << 30;
					}

					if(j==5){
						screens[i].time = 1 << 30;
						screens[i].lockTime = 1 << 30;
						while(screens[i].move(1, 0));
					}
				}
			}
		}
		repaint();
	}
	public void setGameOver () {
		for (int i = 0; i < numOfPlayers; i++)
			screens[i].Lose = true;
	}
	public void sendGarbage (int id, int send) {
		if (numOfPlayers == 1)
			return;
		int rand = (int)(Math.random()*(numOfPlayers-1));
		if (rand >= id)
			rand++;
		screens[rand].holdPiece(send);
	}
}
