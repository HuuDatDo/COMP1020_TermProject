public class Piece {
	// constants for the starting position for each piece
	public final PieceShape[][] pieces = {{mp(0, 4), mp(0, 5), mp(1, 4), mp(1, 5)},
								 {mp(1, 3), mp(1, 4), mp(1, 5), mp(1, 6)},
								 {mp(0, 3), mp(1, 3), mp(1, 4), mp(1, 5)},
								 {mp(0, 5), mp(1, 3), mp(1, 4), mp(1, 5)},
								 {mp(1, 3), mp(1, 4), mp(0, 4), mp(0, 5)},
								 {mp(0, 3), mp(0, 4), mp(1, 4), mp(1, 5)},
								 {mp(1, 3), mp(1, 4), mp(1, 5), mp(0, 4)}};
								 
								 
	public PieceShape mp (int x, int y) {
		return new PieceShape(x, y);
	}
	@Deprecated

	public Active getActive () {
		int id = (int)(Math.random()*7);
		PieceShape[] newPiece = new PieceShape[4];
		for (int i = 0; i < 4; i++)
			newPiece[i] = new PieceShape(pieces[id][i].x, pieces[id][i].y);
		return new Active(newPiece, id+1);
	}

	public Active getActive (int id) {
		PieceShape[] newPiece = new PieceShape[4];
		for (int i = 0; i < 4; i++)
			newPiece[i] = new PieceShape(pieces[id][i].x, pieces[id][i].y);
		return new Active(newPiece, id+1);
	}

	public int[] getPermutation () {
		int[] res = new int[7];
		for (int i = 0; i < 7; i++)
			res[i] = i;
		permute(0, res);
		return res;
	}

	public void permute (int i, int[] a) {
		if (i == 6)
			return;
		int swap = (int)(Math.random()*(6-i) + i + 1);
		int temp = a[i];
		a[i] = a[swap];
		a[swap] = temp;
		permute(i+1, a);
	}

	static class Active {
		PieceShape[] position;
		int id;
		int lo_x, hi_x, lo_y, hi_y;
		int state = 0;
		Active (PieceShape[] position, int id) {
			this.position = position;
			this.id = id;
			if (id != 2) {
				lo_x = 0; hi_x = 2;
				lo_y = 3; hi_y = 5;
			} else {
				lo_x = 0; hi_x = 3;
				lo_y = 3; hi_y = 6;
			}
		}
	}
	// represents a point on the grid
	static class PieceShape {
		int x, y;
		PieceShape (int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
