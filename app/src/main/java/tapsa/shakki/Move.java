package tapsa.shakki;

/**
 * Created by Tapsa on 11.11.2015.
 */
public class Move {
    public int fromRow, fromCol, toRow, toCol;
    // 1 = Kingside castling
    // 2 = Queenside castling
    // 3 = Double pawn move
    // 4 = En Passant move
    public int special;
    public boolean AI;

    public Move() {
    }

    public Move(int fC, int fR, int tC, int tR) {
        fromCol = fC;
        fromRow = fR;
        toCol = tC;
        toRow = tR;
        special = 0;
        AI = true;
    }

    public Move(int fC, int fR, int tC, int tR, int type) {
        fromCol = fC;
        fromRow = fR;
        toCol = tC;
        toRow = tR;
        special = type;
        AI = true;
    }

    public void setMove(int fC, int fR, int tC, int tR) {
        fromCol = fC;
        fromRow = fR;
        toCol = tC;
        toRow = tR;
        special = 0;
        AI = false;
    }

    public void setSpecial(int c) {
        special = c;
    }
}
