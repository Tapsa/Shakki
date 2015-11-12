package tapsa.shakki;

/**
 * Created by Tapsa on 11.11.2015.
 */
public class Piece {
    public Who who;
    public Owner owner;
    // Location on the chess board.
    public File col;
    public Rank row;

    public Piece(Piece p) {
        owner = p.owner;
        who = p.who;
        row = p.row;
        col = p.col;
    }

    public Piece(Owner o, Who w, Rank r, File c) {
        owner = o;
        who = w;
        row = r;
        col = c;
    }

    String debugPrint() {
        switch (who) {
            case KING:
                return "K";
            case QUEEN:
                return "Q";
            case ROOK:
                return "R";
            case BISHOP:
                return "B";
            case KNIGHT:
                return "N";
            case PAWN:
                return "P";
            default:
                return " ";
        }
    }
}
