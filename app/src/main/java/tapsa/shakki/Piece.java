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
        switch (owner) {
            case BLACK:
                switch (who) {
                    case KING:
                        return "♚";
                    case QUEEN:
                        return "♛";
                    case ROOK:
                        return "♜";
                    case BISHOP:
                        return "♝";
                    case KNIGHT:
                        return "♞";
                    case PAWN:
                        return "♟";
                }
            case WHITE:
                switch (who) {
                    case KING:
                        return "♔";
                    case QUEEN:
                        return "♕";
                    case ROOK:
                        return "♖";
                    case BISHOP:
                        return "♗";
                    case KNIGHT:
                        return "♘";
                    case PAWN:
                        return "♙";
                }
            default:
                return " ";
        }
    }
}
