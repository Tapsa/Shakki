package tapsa.shakki;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.TreeSet;

/**
 * Created by Tapsa on 11.11.2015.
 */
public class Position {
    private List<Piece> whitePieces, blackPieces;
    // Chess board which contais pointers to pieces.
    // If a square doesn't have a piece on it, it'll point to NULL.
    private Piece[][] board = new Piece[8][8];
    // Keeps track of turns.
    private Owner whoseTurn;
    // bit 1 = White's kingside
    // bit 2 = White's queenside
    // bit 3 = Black's kingside
    // bit 4 = Black's queenside
    private short canCastle;
    // For en passant move.
    private Piece passer;
    private int movesDone, lastEatMove;

    // Kertoimet
    private final static int c1 = 20, c2 = 10, c4 = 1, c5 = 7;

    // Normal: 78, max: 206
    private final static int value[] = {0, 18, 10, 6, 6, 2};
    // Max: 48
    private final static int center[][] = {
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 2, 2, 2, 2, 2, 1},
            {1, 2, 3, 3, 3, 3, 2, 1},
            {1, 2, 3, 3, 3, 3, 2, 1},
            {1, 2, 3, 3, 3, 3, 2, 1},
            {1, 2, 3, 3, 3, 3, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 1},
            {1, 1, 1, 1, 1, 1, 1, 1}
    };
    // Max: 2
    private final static int safety[][] = {
            {2, 2, 1, 0, 0, 1, 2, 2},
            {2, 1, 0, 0, 0, 0, 1, 2},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {2, 1, 0, 0, 0, 0, 1, 2},
            {2, 2, 1, 0, 0, 1, 2, 2}
    };
    // Max: 32
    private final static int promotion[][] = {
            {0, 3, 1, 0, 0, 0, 0, 0}, // Black
            {0, 0, 0, 0, 0, 1, 3, 0}  // White
    };

    public Position() {
    }

    public Position(Position pos) {
        clear();

        for (Piece p : pos.whitePieces) {
            Piece newP = new Piece(p);
            whitePieces.add(newP);
            board[p.row.ordinal()][p.col.ordinal()] = newP;
        }

        for (Piece p : pos.blackPieces) {
            Piece newP = new Piece(p);
            blackPieces.add(newP);
            board[p.row.ordinal()][p.col.ordinal()] = newP;
        }

        whoseTurn = pos.whoseTurn;
        canCastle = pos.canCastle;
        passer = (null != pos.passer) ? board[pos.passer.row.ordinal()][pos.passer.col.ordinal()] : null;
        movesDone = pos.movesDone;
        lastEatMove = pos.lastEatMove;
    }

    public String showSpecialInfo() {
        String message = "Castling bits: " + canCastle + "\nEn Passer:";
        if (null != passer) message += printColRow(passer.col, passer.row);
        return message + "\n";
    }

    public boolean isDraw() {
        if (movesDone - lastEatMove >= 100) return true;
        return false;
    }

    public Owner tellTurn() {
        return whoseTurn;
    }

    // Removes all Pieces from the chess board.
    public void clear() {
        for (Piece[] row : board) {
            for (Piece p : row) {
                p = null;
            }
        }
        whitePieces = new LinkedList<Piece>();
        blackPieces = new LinkedList<Piece>();
    }

    // Puts pieces in starting position.
    public void start() {
        clear();
        // Assigning chess pieces to players.
        whitePieces.add(new Piece(Owner.WHITE, Who.KING, Rank.R1, File.E));
        whitePieces.add(new Piece(Owner.WHITE, Who.QUEEN, Rank.R1, File.D));
        whitePieces.add(new Piece(Owner.WHITE, Who.ROOK, Rank.R1, File.A));
        whitePieces.add(new Piece(Owner.WHITE, Who.ROOK, Rank.R1, File.H));
        whitePieces.add(new Piece(Owner.WHITE, Who.BISHOP, Rank.R1, File.C));
        whitePieces.add(new Piece(Owner.WHITE, Who.BISHOP, Rank.R1, File.F));
        whitePieces.add(new Piece(Owner.WHITE, Who.KNIGHT, Rank.R1, File.B));
        whitePieces.add(new Piece(Owner.WHITE, Who.KNIGHT, Rank.R1, File.G));
        whitePieces.add(new Piece(Owner.WHITE, Who.PAWN, Rank.R2, File.A));
        whitePieces.add(new Piece(Owner.WHITE, Who.PAWN, Rank.R2, File.B));
        whitePieces.add(new Piece(Owner.WHITE, Who.PAWN, Rank.R2, File.C));
        whitePieces.add(new Piece(Owner.WHITE, Who.PAWN, Rank.R2, File.D));
        whitePieces.add(new Piece(Owner.WHITE, Who.PAWN, Rank.R2, File.E));
        whitePieces.add(new Piece(Owner.WHITE, Who.PAWN, Rank.R2, File.F));
        whitePieces.add(new Piece(Owner.WHITE, Who.PAWN, Rank.R2, File.G));
        whitePieces.add(new Piece(Owner.WHITE, Who.PAWN, Rank.R2, File.H));

        for (Piece p : whitePieces) {
            board[p.row.ordinal()][p.col.ordinal()] = p;
        }

        blackPieces.add(new Piece(Owner.BLACK, Who.KING, Rank.R8, File.E));
        blackPieces.add(new Piece(Owner.BLACK, Who.QUEEN, Rank.R8, File.D));
        blackPieces.add(new Piece(Owner.BLACK, Who.ROOK, Rank.R8, File.A));
        blackPieces.add(new Piece(Owner.BLACK, Who.ROOK, Rank.R8, File.H));
        blackPieces.add(new Piece(Owner.BLACK, Who.BISHOP, Rank.R8, File.C));
        blackPieces.add(new Piece(Owner.BLACK, Who.BISHOP, Rank.R8, File.F));
        blackPieces.add(new Piece(Owner.BLACK, Who.KNIGHT, Rank.R8, File.B));
        blackPieces.add(new Piece(Owner.BLACK, Who.KNIGHT, Rank.R8, File.G));
        blackPieces.add(new Piece(Owner.BLACK, Who.PAWN, Rank.R7, File.A));
        blackPieces.add(new Piece(Owner.BLACK, Who.PAWN, Rank.R7, File.B));
        blackPieces.add(new Piece(Owner.BLACK, Who.PAWN, Rank.R7, File.C));
        blackPieces.add(new Piece(Owner.BLACK, Who.PAWN, Rank.R7, File.D));
        blackPieces.add(new Piece(Owner.BLACK, Who.PAWN, Rank.R7, File.E));
        blackPieces.add(new Piece(Owner.BLACK, Who.PAWN, Rank.R7, File.F));
        blackPieces.add(new Piece(Owner.BLACK, Who.PAWN, Rank.R7, File.G));
        blackPieces.add(new Piece(Owner.BLACK, Who.PAWN, Rank.R7, File.H));

        for (Piece p : blackPieces) {
            board[p.row.ordinal()][p.col.ordinal()] = p;
        }

        whoseTurn = Owner.WHITE;
        canCastle = 15;
        passer = null;
        movesDone = lastEatMove = 0;
    }

    public static String printColRow(File col, Rank row) {
        return " " + (char) (col.ordinal() + 97) + (row.ordinal() + 1);
    }

    // For checking any square.
    // Returns true if a piece is found.
    private boolean moveCheck(Piece p, List<Move> m, int fC, int fR, int tC, int tR) {
        if (null == p) {
            // Legal move!
            m.add(new Move(fC, fR, tC, tR));
            // Continue checking for moves.
            return false;
        }
        if (p.owner != whoseTurn) {
            // Enemy sighted!
            // Legal move!
            m.add(new Move(fC, fR, tC, tR));
            // No more moves!
        }
        return true;
    }

    private static boolean moveCheckPawn(Piece p, List<Move> m, int fC, int fR, int tC, int tR, int two) {
        if (null != p) return false;
        m.add(new Move(fC, fR, tC, tR, two));
        return true;
    }

    private void eatCheckPawn(Piece p, List<Move> m, int fC, int fR) {
        if (null != p && p.owner != whoseTurn) {
            m.add(new Move(fC, fR, p.col.ordinal(), p.row.ordinal()));
        }
    }

    private void enPassantCheckPawn(Piece p, List<Move> m, int fC, int tR) {
        if (p == passer) {
            m.add(new Move(fC, p.row.ordinal(), p.col.ordinal(), tR, 4));
        }
    }

    private boolean threatenCheck(Piece p, List<Piece> threats, Who secondPieceType) {
        if (null == p) return false;
        if (p.owner != whoseTurn && (p.who == Who.QUEEN || p.who == secondPieceType)) {
            threats.add(p);
        }
        // Stop checking.
        return true;
    }

    private void threatenCheckKing(Piece p, IntRef threats) {
        if (null != p && p.who == Who.KING && p.owner != whoseTurn) ++threats.value;
    }

    private boolean threatenCheck(Piece p, IntRef threats, Who secondPieceType) {
        // No piece, no threat.
        // Continue checking.
        if (null == p) return false;
        // Our piece, no threat.
        // Is it queen or rook/bishop = is the king threatened?
        if (p.owner != whoseTurn && (p.who == Who.QUEEN || p.who == secondPieceType)) {
            ++threats.value;
        }
        // Stop checking.
        return true;
    }

    private void threatenCheck1Piece(Piece p, IntRef threats, Who pieceType) {
        if (null != p && p.owner != whoseTurn && p.who == pieceType) ++threats.value;
    }

    private void threatenCheck1Piece(Piece p, List<Piece> threats, Who pieceType) {
        if (null != p && p.owner != whoseTurn && p.who == pieceType) {
            threats.add(p);
        }
    }

    private boolean isProtectingKing(Piece p, Piece king, int tC, int tR) {
        int rowDiff = p.row.ordinal() - king.row.ordinal(), colDiff = p.col.ordinal() - king.col.ordinal();
        Direction direction = calcDirection(rowDiff, colDiff);
        // Nappi hetkeksi pois jotta nähdään suojaako se kuningasta.
        board[p.row.ordinal()][p.col.ordinal()] = null;
        // Otetaan myös nykyinen uhkaaja pois, koska se ei saa vaikuttaa tähän.
        Piece t = board[tR][tC];
        board[tR][tC] = null;
        if (colDiff == 0 || rowDiff == 0 ||
                colDiff == rowDiff || colDiff == -rowDiff)
            if (isKingThreatened(king.col.ordinal(), king.row.ordinal(), direction)) {
                board[p.row.ordinal()][p.col.ordinal()] = p;
                board[tR][tC] = t;
                return true;
            }
        board[p.row.ordinal()][p.col.ordinal()] = p;
        board[tR][tC] = t;
        return false;
    }

    private boolean canMoveOwn(Piece p, Piece king, List<Move> m, Who secondPieceType, int tC, int tR) {
        // Ei nappia. Tästä ei voi siirtää kuninkaan suojeluun.
        if (null == p) return false;
        // Vihollisen nappi. Lopeta tarkistus.
        if (p.owner != whoseTurn) return true;
        // Oma oikeantyyppinen nappi siirrettävissä kuninkaan suojeluun.
        // Älä siirrä jos suojaa kuningasta!
        if (isProtectingKing(p, king, tC, tR)) return true;
        if (p.who == Who.QUEEN || p.who == secondPieceType) {
            m.add(new Move(p.col.ordinal(), p.row.ordinal(), tC, tR));
        }
        return true;
    }

    private void canMoveOwn1Piece(Piece p, Piece king, List<Move> m, Who pieceType, int tC, int tR) {
        // Ei nappia. Tästä ei voi siirtää kuninkaan suojeluun.
        if (null == p) return;
        // Oma oikeantyyppinen nappi siirrettävissä kuninkaan suojeluun.
        // Älä siirrä jos suojaa kuningasta!
        if (isProtectingKing(p, king, tC, tR)) return;
        if (p.owner == whoseTurn && p.who == pieceType) {
            m.add(new Move(p.col.ordinal(), p.row.ordinal(), tC, tR));
        }
    }

    private boolean canMoveOwnPawn(Piece p, Piece king, List<Move> m, int tC, int tR, int two) {
        // Ei nappia. Tästä ei voi siirtää kuninkaan suojeluun.
        if (null == p) return true;
        // Oma oikeantyyppinen nappi siirrettävissä kuninkaan suojeluun.
        // Älä siirrä jos suojaa kuningasta!
        if (isProtectingKing(p, king, tC, tR)) return false;
        if (p.owner == whoseTurn && p.who == Who.PAWN) {
            m.add(new Move(p.col.ordinal(), p.row.ordinal(), tC, tR, two));
        }
        return false;
    }

    private boolean isKingThreatened(int col, int row) {
        // Uhkien määrä (tarvitaan myöhemmin)
        IntRef threats = new IntRef();

        // Enemy king
        if (row < Rank.R8.ordinal()) {
            threatenCheckKing(board[row + 1][col], threats);
            if (col < File.H.ordinal()) threatenCheckKing(board[row + 1][col + 1], threats);
        }
        if (col < File.H.ordinal()) {
            threatenCheckKing(board[row][col + 1], threats);
            if (row > Rank.R1.ordinal()) threatenCheckKing(board[row - 1][col + 1], threats);
        }
        if (row > Rank.R1.ordinal()) {
            threatenCheckKing(board[row - 1][col], threats);
            if (col > File.A.ordinal()) threatenCheckKing(board[row - 1][col - 1], threats);
        }
        if (col > File.A.ordinal()) {
            threatenCheckKing(board[row][col - 1], threats);
            if (row < Rank.R8.ordinal()) threatenCheckKing(board[row + 1][col - 1], threats);
        }

        // Up/Down/Left/Right: queens, rooks
        // Up
        for (int r = row + 1; r < 8; r++) {
            if (threatenCheck(board[r][col], threats, Who.ROOK)) break;
        }
        // Right
        for (int c = col + 1; c < 8; c++) {
            if (threatenCheck(board[row][c], threats, Who.ROOK)) break;
        }
        // Down
        for (int r = row - 1; r >= 0; r--) {
            if (threatenCheck(board[r][col], threats, Who.ROOK)) break;
        }
        // Left
        for (int c = col - 1; c >= 0; c--) {
            if (threatenCheck(board[row][c], threats, Who.ROOK)) break;
        }

        // Sideways: queens, bishops
        // Up-right
        for (int r = row + 1, c = col + 1; r < 8 && c < 8; r++, c++) {
            if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
        }
        // Right-down
        for (int r = row - 1, c = col + 1; r >= 0 && c < 8; r--, c++) {
            if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
        }
        // Down-left
        for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) {
            if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
        }
        // Left-up
        for (int r = row + 1, c = col - 1; r < 8 && c >= 0; r++, c--) {
            if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
        }

        // Checking for knights
        int r = row, c = col;
        // Up
        if (r < 6) {
            // Right
            if (c < 7) threatenCheck1Piece(board[r + 2][c + 1], threats, Who.KNIGHT);
            // Left
            if (c > 0) threatenCheck1Piece(board[r + 2][c - 1], threats, Who.KNIGHT);
        }
        // Right
        if (c < 6) {
            // Up
            if (r < 7) threatenCheck1Piece(board[r + 1][c + 2], threats, Who.KNIGHT);
            // Down
            if (r > 0) threatenCheck1Piece(board[r - 1][c + 2], threats, Who.KNIGHT);
        }
        // Down
        if (r > 1) {
            // Right
            if (c < 7) threatenCheck1Piece(board[r - 2][c + 1], threats, Who.KNIGHT);
            // Left
            if (c > 0) threatenCheck1Piece(board[r - 2][c - 1], threats, Who.KNIGHT);
        }
        // Left
        if (c > 1) {
            // Up
            if (r < 7) threatenCheck1Piece(board[r + 1][c - 2], threats, Who.KNIGHT);
            // Down
            if (r > 0) threatenCheck1Piece(board[r - 1][c - 2], threats, Who.KNIGHT);
        }

        // Checking for pawns.
        boolean doPawns = true;
        if (whoseTurn == Owner.WHITE) {
            // No threat from pawns.
            if (row == 7) doPawns = false;
            row++;
        } else {
            // No threat from pawns.
            if (row == 0) doPawns = false;
            row--;
        }
        if (doPawns) {
            // Right side
            if (col < 7) threatenCheck1Piece(board[row][col + 1], threats, Who.PAWN);
            // Left side
            if (col > 0) threatenCheck1Piece(board[row][col - 1], threats, Who.PAWN);
        }

        return threats.value != 0;
    }

    private boolean isKingThreatened(int col, int row, List<Piece> threats) {
        // Up/Down/Left/Right: queens, rooks
        // Up
        for (int r = row + 1; r < 8; r++) {
            if (threatenCheck(board[r][col], threats, Who.ROOK)) break;
        }
        // Right
        for (int c = col + 1; c < 8; c++) {
            if (threatenCheck(board[row][c], threats, Who.ROOK)) break;
        }
        // Down
        for (int r = row - 1; r >= 0; r--) {
            if (threatenCheck(board[r][col], threats, Who.ROOK)) break;
        }
        // Left
        for (int c = col - 1; c >= 0; c--) {
            if (threatenCheck(board[row][c], threats, Who.ROOK)) break;
        }

        // Sideways: queens, bishops
        // Up-right
        for (int r = row + 1, c = col + 1; r < 8 && c < 8; r++, c++) {
            if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
        }
        // Right-down
        for (int r = row - 1, c = col + 1; r >= 0 && c < 8; r--, c++) {
            if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
        }
        // Down-left
        for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) {
            if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
        }
        // Left-up
        for (int r = row + 1, c = col - 1; r < 8 && c >= 0; r++, c--) {
            if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
        }

        // Checking for knights
        int r = row, c = col;
        // Up
        if (r < 6) {
            // Right
            if (c < 7)
                threatenCheck1Piece(board[r + 2][c + 1], threats, Who.KNIGHT);
            // Left
            if (c > 0)
                threatenCheck1Piece(board[r + 2][c - 1], threats, Who.KNIGHT);
        }
        // Right
        if (c < 6) {
            // Up
            if (r < 7)
                threatenCheck1Piece(board[r + 1][c + 2], threats, Who.KNIGHT);
            // Down
            if (r > 0)
                threatenCheck1Piece(board[r - 1][c + 2], threats, Who.KNIGHT);
        }
        // Down
        if (r > 1) {
            // Right
            if (c < 7)
                threatenCheck1Piece(board[r - 2][c + 1], threats, Who.KNIGHT);
            // Left
            if (c > 0)
                threatenCheck1Piece(board[r - 2][c - 1], threats, Who.KNIGHT);
        }
        // Left
        if (c > 1) {
            // Up
            if (r < 7)
                threatenCheck1Piece(board[r + 1][c - 2], threats, Who.KNIGHT);
            // Down
            if (r > 0)
                threatenCheck1Piece(board[r - 1][c - 2], threats, Who.KNIGHT);
        }

        // Checking for pawns.
        boolean doPawns = true;
        if (whoseTurn == Owner.WHITE) {
            // No threat from pawns.
            if (row == 7) doPawns = false;
            row++;
        } else {
            // No threat from pawns.
            if (row == 0) doPawns = false;
            row--;
        }
        if (doPawns) {
            // Right side
            if (col < 7)
                threatenCheck1Piece(board[row][col + 1], threats, Who.PAWN);
            // Left side
            if (col > 0)
                threatenCheck1Piece(board[row][col - 1], threats, Who.PAWN);
        }

        return threats.size() != 0;
    }

    // Onko kuningasta suojaavan napin takana uhka?
    // Tämä funktio ei voi koskaan asettaa uhkaajaa kuin kerran.
    private boolean isKingThreatened(int col, int row, Direction d) {
        // Uhkien määrä (tarvitaan myöhemmin)
        IntRef threats = new IntRef();

        // Up/Down/Left/Right: queens, rooks
        // Sideways: queens, bishops
        switch (d) {
            case NORTH:
                // Up
                for (int r = row + 1; r < 8; r++) {
                    if (threatenCheck(board[r][col], threats, Who.ROOK)) break;
                }
                break;
            case NORTHEAST:
                // Up-right
                for (int r = row + 1, c = col + 1; r < 8 && c < 8; r++, c++) {
                    if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
                }
                break;
            case EAST:
                // Right
                for (int c = col + 1; c < 8; c++) {
                    if (threatenCheck(board[row][c], threats, Who.ROOK)) break;
                }
                break;
            case SOUTHEAST:
                // Right-down
                for (int r = row - 1, c = col + 1; r >= 0 && c < 8; r--, c++) {
                    if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
                }
                break;
            case SOUTH:
                // Down
                for (int r = row - 1; r >= 0; r--) {
                    if (threatenCheck(board[r][col], threats, Who.ROOK)) break;
                }
                break;
            case SOUTHWEST:
                // Down-left
                for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) {
                    if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
                }
                break;
            case WEST:
                // Left
                for (int c = col - 1; c >= 0; c--) {
                    if (threatenCheck(board[row][c], threats, Who.ROOK)) break;
                }
                break;
            case NORTHWEST:
                // Left-up
                for (int r = row + 1, c = col - 1; r < 8 && c >= 0; r++, c--) {
                    if (threatenCheck(board[r][c], threats, Who.BISHOP)) break;
                }
                break;
        }

        return threats.value != 0;
    }

    private void checkOwnMovesToHere(List<Move> m, int tC, int tR, Piece king) {
        // Samanlainen tarkistus kuin uhkaamisista.
        // Tässä vaan tarkistetaan toisin päin.
        // Kuningasta jo suojaavia nappeja ei saa siirtää!
        // Katso missä suunnassa tämä nappi on kuninkaasta.
        // Katso suojaako se kuningasta.
        // Jos ei, niin jatka.

        // Up/Down/Left/Right: queens, rooks
        // Up
        for (int r = tR + 1; r < 8; r++) {
            if (canMoveOwn(board[r][tC], king, m, Who.ROOK, tC, tR)) break;
        }
        // Right
        for (int c = tC + 1; c < 8; c++) {
            if (canMoveOwn(board[tR][c], king, m, Who.ROOK, tC, tR)) break;
        }
        // Down
        for (int r = tR - 1; r >= 0; r--) {
            if (canMoveOwn(board[r][tC], king, m, Who.ROOK, tC, tR)) break;
        }
        // Left
        for (int c = tC - 1; c >= 0; c--) {
            if (canMoveOwn(board[tR][c], king, m, Who.ROOK, tC, tR)) break;
        }

        // Sideways: queens, bishops
        // Up-right
        for (int r = tR + 1, c = tC + 1; r < 8 && c < 8; r++, c++) {
            if (canMoveOwn(board[r][c], king, m, Who.BISHOP, tC, tR)) break;
        }
        // Right-down
        for (int r = tR - 1, c = tC + 1; r >= 0 && c < 8; r--, c++) {
            if (canMoveOwn(board[r][c], king, m, Who.BISHOP, tC, tR)) break;
        }
        // Down-left
        for (int r = tR - 1, c = tC - 1; r >= 0 && c >= 0; r--, c--) {
            if (canMoveOwn(board[r][c], king, m, Who.BISHOP, tC, tR)) break;
        }
        // Left-up
        for (int r = tR + 1, c = tC - 1; r < 8 && c >= 0; r++, c--) {
            if (canMoveOwn(board[r][c], king, m, Who.BISHOP, tC, tR)) break;
        }

        // Checking for knights
        int r = tR, c = tC;
        // Up
        if (r < 6) {
            // Right
            if (c < 7)
                canMoveOwn1Piece(board[r + 2][c + 1], king, m, Who.KNIGHT, tC, tR);
            // Left
            if (c > 0)
                canMoveOwn1Piece(board[r + 2][c - 1], king, m, Who.KNIGHT, tC, tR);
        }
        // Right
        if (c < 6) {
            // Up
            if (r < 7)
                canMoveOwn1Piece(board[r + 1][c + 2], king, m, Who.KNIGHT, tC, tR);
            // Down
            if (r > 0)
                canMoveOwn1Piece(board[r - 1][c + 2], king, m, Who.KNIGHT, tC, tR);
        }
        // Down
        if (r > 1) {
            // Right
            if (c < 7)
                canMoveOwn1Piece(board[r - 2][c + 1], king, m, Who.KNIGHT, tC, tR);
            // Left
            if (c > 0)
                canMoveOwn1Piece(board[r - 2][c - 1], king, m, Who.KNIGHT, tC, tR);
        }
        // Left
        if (c > 1) {
            // Up
            if (r < 7)
                canMoveOwn1Piece(board[r + 1][c - 2], king, m, Who.KNIGHT, tC, tR);
            // Down
            if (r > 0)
                canMoveOwn1Piece(board[r - 1][c - 2], king, m, Who.KNIGHT, tC, tR);
        }

        // Checking for pawns.
        r = tR;
        if (whoseTurn == Owner.BLACK) {
            if (r == 7) return;
            r++;
        } else {
            if (r == 0) return;
            r--;
        }
        // Can move pawn to eat.
        if (null != board[tR][tC]) {
            // Right side
            if (tC < 7) {
                canMoveOwn1Piece(board[r][tC + 1], king, m, Who.PAWN, tC, tR);
                if (passer == board[tR][tC])
                    canMoveOwnPawn(board[tR][tC + 1], king, m, tC, tR + tR - r, 4);
            }
            // Left side
            if (tC > 0) {
                canMoveOwn1Piece(board[r][tC - 1], king, m, Who.PAWN, tC, tR);
                if (passer == board[tR][tC])
                    canMoveOwnPawn(board[tR][tC - 1], king, m, tC, tR + tR - r, 4);
            }
        } else {
            // Can only move pawn.
            if (canMoveOwnPawn(board[r][tC], king, m, tC, tR, 0)) {
                if (whoseTurn == Owner.WHITE) {
                    if (r == 2)
                        canMoveOwnPawn(board[r - 1][tC], king, m, tC, tR, 3);
                } else {
                    if (r == 5)
                        canMoveOwnPawn(board[r + 1][tC], king, m, tC, tR, 3);
                }
            }
        }
    }

    private void canKingMove(Piece p, List<Move> m, int fC, int fR, int tC, int tR) {
        if (null == p || p.owner != whoseTurn) {
            // Uhataanko tätä ruutua?
            if (!isKingThreatened(tC, tR)) m.add(new Move(fC, fR, tC, tR));
        }
    }

    private boolean isClearPath(Piece p, int col, int row) {
        if (null == p) {
            // Uhataanko tätä ruutua?
            return !isKingThreatened(col, row);
        }
        return false;
    }

    // Laske suunta perustuen rivien ja sarakkeiden erotuksiin.
    private Direction calcDirection(int rowD, int colD) {
        // Kuninkaan alapuolella
        if (rowD < 0) {
            if (colD < 0) return Direction.SOUTHWEST;
            if (colD > 0) return Direction.SOUTHEAST;
            return Direction.SOUTH;
        }
        // Kuninkaan yläpuolella
        if (rowD > 0) {
            if (colD < 0) return Direction.NORTHWEST;
            if (colD > 0) return Direction.NORTHEAST;
            return Direction.NORTH;
        }
        // Kuninkaan tasossa
        if (colD < 0) return Direction.WEST;
        return Direction.EAST;
    }

    private void kingSideCastling(List<Move> m, Piece king) {
        if (whoseTurn == Owner.WHITE && (canCastle & 1) != 0 || whoseTurn == Owner.BLACK && (canCastle & 4) != 0) {
            for (int c = king.col.ordinal() + 1; c < File.H.ordinal(); c++) {
                if (!isClearPath(board[king.row.ordinal()][c], c, king.row.ordinal())) return;
            }
            // Luo tornitussiirto
            m.add(new Move(File.E.ordinal(), king.row.ordinal(), File.G.ordinal(), king.row.ordinal(), 1));
        }
    }

    private void queenSideCastling(List<Move> m, Piece king) {
        if (whoseTurn == Owner.WHITE && (canCastle & 2) != 0 || whoseTurn == Owner.BLACK && (canCastle & 8) != 0) {
            for (int c = king.col.ordinal() - 1; c > File.B.ordinal(); c--) {
                if (!isClearPath(board[king.row.ordinal()][c], c, king.row.ordinal())) return;
            }
            // Luo tornitussiirto
            m.add(new Move(File.E.ordinal(), king.row.ordinal(), File.C.ordinal(), king.row.ordinal(), 2));
        }
    }

    private void switchCaseFallThroughWorkaround1(List<Move> moves, int col, int row, Piece king) {
        // Tarkistetaan suora jono nappia kohti nappi mukaanlukien.
        for (int r = row, c = col; r != king.row.ordinal() || c != king.col.ordinal(); ) {
            // Kutsu funktiota joka tarkistaa voiko oman napin siirtää tähän kohtaan laudalla.
            checkOwnMovesToHere(moves, c, r, king);

            if (r > king.row.ordinal()) r--;
            else if (r < king.row.ordinal()) r++;

            if (c > king.col.ordinal()) c--;
            else if (c < king.col.ordinal()) c++;
        }
    }

    private void switchCaseFallThroughWorkaround2(Piece p, List<Move> moves) {
        // Up
        for (int r = p.row.ordinal() + 1; r < 8; r++) {
            if (moveCheck(board[r][p.col.ordinal()], moves, p.col.ordinal(), p.row.ordinal(), p.col.ordinal(), r))
                break;
        }
        // Down
        for (int r = p.row.ordinal() - 1; r >= 0; r--) {
            if (moveCheck(board[r][p.col.ordinal()], moves, p.col.ordinal(), p.row.ordinal(), p.col.ordinal(), r))
                break;
        }
    }

    private void switchCaseFallThroughWorkaround3(Piece p, List<Move> moves) {
        // Up-right
        for (int r = p.row.ordinal() + 1, c = p.col.ordinal() + 1; r < 8 && c < 8; r++, c++) {
            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                break;
        }
        // Down-left
        for (int r = p.row.ordinal() - 1, c = p.col.ordinal() - 1; r >= 0 && c >= 0; r--, c--) {
            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                break;
        }
    }

    private void switchCaseFallThroughWorkaround4(Piece p, List<Move> moves) {
        // Right
        for (int c = p.col.ordinal() + 1; c < 8; c++) {
            if (moveCheck(board[p.row.ordinal()][c], moves, p.col.ordinal(), p.row.ordinal(), c, p.row.ordinal()))
                break;
        }
        // Left
        for (int c = p.col.ordinal() - 1; c >= 0; c--) {
            if (moveCheck(board[p.row.ordinal()][c], moves, p.col.ordinal(), p.row.ordinal(), c, p.row.ordinal()))
                break;
        }
    }

    private void switchCaseFallThroughWorkaround5(Piece p, List<Move> moves) {
        // Right-down
        for (int r = p.row.ordinal() - 1, c = p.col.ordinal() + 1; r >= 0 && c < 8; r--, c++) {
            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                break;
        }
        // Left-up
        for (int r = p.row.ordinal() + 1, c = p.col.ordinal() - 1; r < 8 && c >= 0; r++, c--) {
            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                break;
        }
    }

    private void switchCaseFallThroughWorkaround6(Piece p, List<Move> moves) {
        // Up
        for (int r = p.row.ordinal() + 1; r < 8; r++) {
            if (moveCheck(board[r][p.col.ordinal()], moves, p.col.ordinal(), p.row.ordinal(), p.col.ordinal(), r))
                break;
        }
        // Down
        for (int r = p.row.ordinal() - 1; r >= 0; r--) {
            if (moveCheck(board[r][p.col.ordinal()], moves, p.col.ordinal(), p.row.ordinal(), p.col.ordinal(), r))
                break;
        }
    }

    private void switchCaseFallThroughWorkaround7(Piece p, List<Move> moves) {
        // Right
        for (int c = p.col.ordinal() + 1; c < 8; c++) {
            if (moveCheck(board[p.row.ordinal()][c], moves, p.col.ordinal(), p.row.ordinal(), c, p.row.ordinal()))
                break;
        }
        // Left
        for (int c = p.col.ordinal() - 1; c >= 0; c--) {
            if (moveCheck(board[p.row.ordinal()][c], moves, p.col.ordinal(), p.row.ordinal(), c, p.row.ordinal()))
                break;
        }
    }

    private void switchCaseFallThroughWorkaround8(Piece p, List<Move> moves) {
        // Up-right
        for (int r = p.row.ordinal() + 1, c = p.col.ordinal() + 1; r < 8 && c < 8; r++, c++) {
            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                break;
        }
        // Down-left
        for (int r = p.row.ordinal() - 1, c = p.col.ordinal() - 1; r >= 0 && c >= 0; r--, c--) {
            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                break;
        }
    }

    private void switchCaseFallThroughWorkaround9(Piece p, List<Move> moves) {
        // Right-down
        for (int r = p.row.ordinal() - 1, c = p.col.ordinal() + 1; r >= 0 && c < 8; r--, c++) {
            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                break;
        }
        // Left-up
        for (int r = p.row.ordinal() + 1, c = p.col.ordinal() - 1; r < 8 && c >= 0; r++, c--) {
            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                break;
        }
    }

    public int generateLegalMoves(List<Move> moves, List<Piece> threats) {
        // • Käydään läpi vuorossa olevan pelaajan nappulat.
        // • Ensimmäisenä on aina kuningas.
        // • Tarkistetaan uhkaako vihollisen nappulat kuningasta.
        // • Jos yli 2 nappulaa uhkaa, siirrytään selaamaan kuninkaan siirtoja.
        // • Jos 1 nappula uhkaa, katsotaan voiko oman nappulan laittaa väliin.
        // • Jos kuningasta voi siirtää, jatketaan muihin omiin nappuloihin.
        // • Niissä ei enää pohdita kuninkaan uhkaamisia.

        List<Piece> playersPieces = (whoseTurn == Owner.WHITE) ? whitePieces : blackPieces;

        // Kuninkaan käsittely tähän
        Piece king = playersPieces.get(0);

        // Voiko kuningas liikkua?
        {
            // "Remove" the king from the board to have proper checks.
            board[king.row.ordinal()][king.col.ordinal()] = null;

            int r = king.row.ordinal() + 1, c = king.col.ordinal() + 1;
            if (r < 8) {
                // First up
                canKingMove(board[r][king.col.ordinal()], moves, king.col.ordinal(), king.row.ordinal(), king.col.ordinal(), r);
                if (c < 8) {
                    // Then up-right
                    canKingMove(board[r][c], moves, king.col.ordinal(), king.row.ordinal(), c, r);
                }
            }

            r = king.row.ordinal() - 1;
            if (c < 8) {
                // Then right
                canKingMove(board[king.row.ordinal()][c], moves, king.col.ordinal(), king.row.ordinal(), c, king.row.ordinal());
                if (r >= 0) {

                    // Then right-down
                    canKingMove(board[r][c], moves, king.col.ordinal(), king.row.ordinal(), c, r);
                }
            }

            c = king.col.ordinal() - 1;
            if (r >= 0) {
                // Then down
                canKingMove(board[r][king.col.ordinal()], moves, king.col.ordinal(), king.row.ordinal(), king.col.ordinal(), r);
                if (c >= 0) {
                    // Then down-left
                    canKingMove(board[r][c], moves, king.col.ordinal(), king.row.ordinal(), c, r);
                }
            }

            if (c >= 0) {
                // Then left
                canKingMove(board[king.row.ordinal()][c], moves, king.col.ordinal(), king.row.ordinal(), c, king.row.ordinal());
                r = king.row.ordinal() + 1;
                if (r < 8) {
                    // Then left-up
                    canKingMove(board[r][c], moves, king.col.ordinal(), king.row.ordinal(), c, r);
                }
            }

            // Restore the king to the board.
            board[king.row.ordinal()][king.col.ordinal()] = king;
        }

        // Uhataanko?
        if (isKingThreatened(king.col.ordinal(), king.row.ordinal(), threats)) {
            //result = (whoseTurn == WHITE) ? -1000 : 1000;
            // Jos vain yksi vihollinen uhkaa, tarkista voiko laittaa eteen nappeja.
            // Tämä on totta vain jos kuningasta uhataan.
            if (1 == threats.size()) {
                // Tarkista pelkästään voiko tähän väliin laittaa nappeja.
                // TEE OMA FUNKTIO JOSSA TARKISTETAAN VOIKO OMAN LAITTAA RUUTUUN!
                // Vihollistyypin perusteella katsotaan mitä ruutuja tarkistetaan!
                Piece threat = threats.get(0);
                int row = threat.row.ordinal(), col = threat.col.ordinal();
                switch (threat.who) {
                    case QUEEN:
                        switchCaseFallThroughWorkaround1(moves, col, row, king);
                        break;
                    case ROOK:
                        switchCaseFallThroughWorkaround1(moves, col, row, king);
                        break;
                    case BISHOP:
                        switchCaseFallThroughWorkaround1(moves, col, row, king);
                        break;
                    case KNIGHT:
                        // Voiko hevosen syödä?
                        // Kutsu funktiota joka tarkistaa voiko oman napin siirtää tähän kohtaan laudalla.
                        checkOwnMovesToHere(moves, col, row, king);
                        break;
                    case PAWN:
                        // Voiko sotilaan syödä?
                        // Kutsu funktiota joka tarkistaa voiko oman napin siirtää tähän kohtaan laudalla.
                        checkOwnMovesToHere(moves, col, row, king);
                        break;
                }
            }

            /*/ Muita nappeja ei voi siirtää muualle!
            if (moves.size() == 0) {
                result.value = 0x4000;
            }*/
            return moves.size();
        }

        // Tarkista tornitus
        kingSideCastling(moves, king);
        queenSideCastling(moves, king);

        // Sitten loput napit
        if (playersPieces.size() > 1) {
            ListIterator<Piece> it = playersPieces.listIterator(1);
            while (it.hasNext()) {
                Piece p = it.next();
                // Jokaisesta napista pitää tarkistaa, onko se estämässä shakkia!
                // "Nosta" nappi pois ja katso tuleeko shakkia siitä suunnasta.
                board[p.row.ordinal()][p.col.ordinal()] = null;
                // Tutki missä suunnassa tämä nappi on kuninkaasta katsottuna.
                int rowDiff = p.row.ordinal() - king.row.ordinal(), colDiff = p.col.ordinal() - king.col.ordinal();
                Direction direction = calcDirection(rowDiff, colDiff);

                // Vain näissä tapauksissa nappi voi edes suojata kuningasta.
                // Muissa tapauksissa on täysin hyödytöntä tehdä allaolevaa.
                if (colDiff == 0 || rowDiff == 0 ||
                        colDiff == rowDiff || colDiff == -rowDiff)
                    if (isKingThreatened(king.col.ordinal(), king.row.ordinal(), direction)) {
                        // Laitetaan nappi "takaisin" laudalle.
                        board[p.row.ordinal()][p.col.ordinal()] = p;
                        // Tätä nappia ei voi siirtää pois suojaamasta kuningasta.
                        // Tarkista siirrot vain siihen suuntaan jossa uhkaaja on!
                        // Ja myös kuninkaan suuntaan, koska kyllähän peruuttaa voi.
                        // Ne ovat ainoita laillisia siirtoja.
                        switch (p.who) {
                            case QUEEN: {
                                switch (direction) {
                                    case NORTH:
                                        switchCaseFallThroughWorkaround2(p, moves);
                                        break;
                                    case SOUTH:
                                        switchCaseFallThroughWorkaround2(p, moves);
                                        break;
                                    case NORTHEAST:
                                        switchCaseFallThroughWorkaround3(p, moves);
                                        break;
                                    case SOUTHWEST:
                                        switchCaseFallThroughWorkaround3(p, moves);
                                        break;
                                    case EAST:
                                        switchCaseFallThroughWorkaround4(p, moves);
                                        break;
                                    case WEST:
                                        switchCaseFallThroughWorkaround4(p, moves);
                                        break;
                                    case SOUTHEAST:
                                        switchCaseFallThroughWorkaround5(p, moves);
                                        break;
                                    case NORTHWEST:
                                        switchCaseFallThroughWorkaround5(p, moves);
                                        break;
                                }
                            }
                            break;
                            case ROOK: {
                                switch (direction) {
                                    case NORTH:
                                        switchCaseFallThroughWorkaround6(p, moves);
                                        break;
                                    case SOUTH:
                                        switchCaseFallThroughWorkaround6(p, moves);
                                        break;
                                    case EAST:
                                        switchCaseFallThroughWorkaround7(p, moves);
                                        break;
                                    case WEST:
                                        switchCaseFallThroughWorkaround7(p, moves);
                                        break;
                                }
                            }
                            break;
                            case BISHOP: {
                                switch (direction) {
                                    case NORTHEAST:
                                        switchCaseFallThroughWorkaround8(p, moves);
                                        break;
                                    case SOUTHWEST:
                                        switchCaseFallThroughWorkaround8(p, moves);
                                        break;
                                    case SOUTHEAST:
                                        switchCaseFallThroughWorkaround9(p, moves);
                                        break;
                                    case NORTHWEST:
                                        switchCaseFallThroughWorkaround9(p, moves);
                                        break;
                                }
                            }
                            break;
                            case KNIGHT:
                                // Jos ratsu suojaa (on ainoa suojaaja), ei sitä voi siirtää minnekään.
                                break;
                            case PAWN:
                                // Sen sijaan sotilaan voi siirtää.
                            {
                                int r = p.row.ordinal(), c = p.col.ordinal();
                                if (whoseTurn == Owner.WHITE) {
                                    if (r < 7) r++;
                                } else {
                                    if (r > 0) r--;
                                }
                                // Right side
                                if (direction == Direction.SOUTHEAST || direction == Direction.NORTHEAST)
                                    if (c < 7) {
                                        eatCheckPawn(board[r][c + 1], moves, c, p.row.ordinal());
                                        if (null != passer)
                                            enPassantCheckPawn(board[p.row.ordinal()][c + 1], moves, c, r);
                                    }
                                // Left side
                                if (direction == Direction.SOUTHWEST || direction == Direction.NORTHWEST)
                                    if (c > 0) {
                                        eatCheckPawn(board[r][c - 1], moves, c, p.row.ordinal());
                                        if (null != passer)
                                            enPassantCheckPawn(board[p.row.ordinal()][c - 1], moves, c, r);
                                    }
                                // Move forward
                                if (direction == Direction.SOUTH || direction == Direction.NORTH)
                                    if (moveCheckPawn(board[r][c], moves, c, p.row.ordinal(), c, r, 0)) {
                                        if (whoseTurn == Owner.WHITE) {
                                            if (r == 2)
                                                moveCheckPawn(board[r + 1][c], moves, c, p.row.ordinal(), c, r + 1, 3);
                                        } else {
                                            if (r == 5)
                                                moveCheckPawn(board[r - 1][c], moves, c, p.row.ordinal(), c, r - 1, 3);
                                        }
                                    }
                            }
                            break;
                        }
                        continue;
                    }
                board[p.row.ordinal()][p.col.ordinal()] = p;

                switch (p.who) {
                    case QUEEN: {
                        // First up
                        for (int r = p.row.ordinal() + 1; r < 8; r++) {
                            if (moveCheck(board[r][p.col.ordinal()], moves, p.col.ordinal(), p.row.ordinal(), p.col.ordinal(), r))
                                break;
                        }

                        // Then up-right
                        for (int r = p.row.ordinal() + 1, c = p.col.ordinal() + 1; r < 8 && c < 8; r++, c++) {
                            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                                break;
                        }

                        // Then right
                        for (int c = p.col.ordinal() + 1; c < 8; c++) {
                            if (moveCheck(board[p.row.ordinal()][c], moves, p.col.ordinal(), p.row.ordinal(), c, p.row.ordinal()))
                                break;
                        }

                        // Then right-down
                        for (int r = p.row.ordinal() - 1, c = p.col.ordinal() + 1; r >= 0 && c < 8; r--, c++) {
                            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                                break;
                        }

                        // Then down
                        for (int r = p.row.ordinal() - 1; r >= 0; r--) {
                            if (moveCheck(board[r][p.col.ordinal()], moves, p.col.ordinal(), p.row.ordinal(), p.col.ordinal(), r))
                                break;
                        }

                        // Then down-left
                        for (int r = p.row.ordinal() - 1, c = p.col.ordinal() - 1; r >= 0 && c >= 0; r--, c--) {
                            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                                break;
                        }

                        // Then left
                        for (int c = p.col.ordinal() - 1; c >= 0; c--) {
                            if (moveCheck(board[p.row.ordinal()][c], moves, p.col.ordinal(), p.row.ordinal(), c, p.row.ordinal()))
                                break;
                        }

                        // Then left-up
                        for (int r = p.row.ordinal() + 1, c = p.col.ordinal() - 1; r < 8 && c >= 0; r++, c--) {
                            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                                break;
                        }
                    }
                    break;
                    case ROOK: {
                        // First up
                        for (int r = p.row.ordinal() + 1; r < 8; r++) {
                            if (moveCheck(board[r][p.col.ordinal()], moves, p.col.ordinal(), p.row.ordinal(), p.col.ordinal(), r))
                                break;
                        }

                        // Then right
                        for (int c = p.col.ordinal() + 1; c < 8; c++) {
                            if (moveCheck(board[p.row.ordinal()][c], moves, p.col.ordinal(), p.row.ordinal(), c, p.row.ordinal()))
                                break;
                        }

                        // Then down
                        for (int r = p.row.ordinal() - 1; r >= 0; r--) {
                            if (moveCheck(board[r][p.col.ordinal()], moves, p.col.ordinal(), p.row.ordinal(), p.col.ordinal(), r))
                                break;
                        }

                        // Then left
                        for (int c = p.col.ordinal() - 1; c >= 0; c--) {
                            if (moveCheck(board[p.row.ordinal()][c], moves, p.col.ordinal(), p.row.ordinal(), c, p.row.ordinal()))
                                break;
                        }
                    }
                    break;
                    case BISHOP: {
                        // Then up-right
                        for (int r = p.row.ordinal() + 1, c = p.col.ordinal() + 1; r < 8 && c < 8; r++, c++) {
                            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                                break;
                        }

                        // Then right-down
                        for (int r = p.row.ordinal() - 1, c = p.col.ordinal() + 1; r >= 0 && c < 8; r--, c++) {
                            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                                break;
                        }

                        // Then left-up
                        for (int r = p.row.ordinal() - 1, c = p.col.ordinal() - 1; r >= 0 && c >= 0; r--, c--) {
                            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                                break;
                        }

                        // Then down-left
                        for (int r = p.row.ordinal() + 1, c = p.col.ordinal() - 1; r < 8 && c >= 0; r++, c--) {
                            if (moveCheck(board[r][c], moves, p.col.ordinal(), p.row.ordinal(), c, r))
                                break;
                        }
                    }
                    break;
                    case KNIGHT: {
                        int r = p.row.ordinal(), c = p.col.ordinal();
                        // Up
                        if (r < 6) {
                            // Right
                            if (c < 7)
                                moveCheck(board[r + 2][c + 1], moves, c, r, c + 1, r + 2);
                            // Left
                            if (c > 0)
                                moveCheck(board[r + 2][c - 1], moves, c, r, c - 1, r + 2);
                        }
                        // Right
                        if (c < 6) {
                            // Up
                            if (r < 7)
                                moveCheck(board[r + 1][c + 2], moves, c, r, c + 2, r + 1);
                            // Down
                            if (r > 0)
                                moveCheck(board[r - 1][c + 2], moves, c, r, c + 2, r - 1);
                        }
                        // Down
                        if (r > 1) {
                            // Right
                            if (c < 7)
                                moveCheck(board[r - 2][c + 1], moves, c, r, c + 1, r - 2);
                            // Left
                            if (c > 0)
                                moveCheck(board[r - 2][c - 1], moves, c, r, c - 1, r - 2);
                        }
                        // Left
                        if (c > 1) {
                            // Up
                            if (r < 7)
                                moveCheck(board[r + 1][c - 2], moves, c, r, c - 2, r + 1);
                            // Down
                            if (r > 0)
                                moveCheck(board[r - 1][c - 2], moves, c, r, c - 2, r - 1);
                        }
                    }
                    break;
                    case PAWN: {
                        int r = p.row.ordinal(), c = p.col.ordinal();
                        if (whoseTurn == Owner.WHITE) {
                            if (r < 7) r++;
                        } else {
                            if (r > 0) r--;
                        }
                        // Right side
                        if (c < 7) {
                            eatCheckPawn(board[r][c + 1], moves, c, p.row.ordinal());
                            if (null != passer)
                                enPassantCheckPawn(board[p.row.ordinal()][c + 1], moves, c, r);
                        }
                        // Left side
                        if (c > 0) {
                            eatCheckPawn(board[r][c - 1], moves, c, p.row.ordinal());
                            if (null != passer)
                                enPassantCheckPawn(board[p.row.ordinal()][c - 1], moves, c, r);
                        }
                        // Move forward
                        if (moveCheckPawn(board[r][c], moves, c, p.row.ordinal(), c, r, 0)) {
                            if (whoseTurn == Owner.WHITE) {
                                if (r == 2)
                                    moveCheckPawn(board[r + 1][c], moves, c, p.row.ordinal(), c, r + 1, 3);
                            } else {
                                if (r == 5)
                                    moveCheckPawn(board[r - 1][c], moves, c, p.row.ordinal(), c, r - 1, 3);
                            }
                        }
                    }
                    break;
                }
            }
        }

        return moves.size();
    }

    private static Integer negamaxed, piecesEaten, negamaxTotal;

    public int evaluate(int moveCount) {
        ListIterator<Piece> whiteit = whitePieces.listIterator();
        ListIterator<Piece> blackit = blackPieces.listIterator();
        int matValue = 0, mobValue = 0, pawnValue = 0;
        // Huomioi linnoituksen mahdollisuus?
        Piece w = whiteit.next(), b = blackit.next();
        int safetyValue =
                safety[w.row.ordinal()][w.col.ordinal()] -
                        safety[b.row.ordinal()][b.col.ordinal()];
        while (whiteit.hasNext()) {
            w = whiteit.next();
            matValue += value[w.who.ordinal()];
            if (w.who == Who.PAWN)
                pawnValue += promotion[w.owner.ordinal()][w.row.ordinal()]; // vain sotilaille!
            else
                mobValue += center[w.col.ordinal()][w.row.ordinal()]; // liian korkea kerroin tällä hetkellä
        }
        while (blackit.hasNext()) {
            b = blackit.next();
            matValue -= value[b.who.ordinal()];
            if (b.who == Who.PAWN)
                pawnValue -= promotion[b.owner.ordinal()][b.row.ordinal()];
            else
                mobValue -= center[b.col.ordinal()][b.row.ordinal()];
        }
        return c1 * (matValue + pawnValue) + c2 * mobValue + c4 * moveCount + c5 * safetyValue;
    }

    private int negamax(Position pos, int depth, int a, int b, int color) {
        synchronized (negamaxed) {
            ++negamaxed;
        }
        List<Move> moves = new LinkedList<Move>();
        List<Piece> threats = new LinkedList<Piece>();
        int moveCount = pos.generateLegalMoves(moves, threats);
        if (moveCount == 0) {
            // Peli päättynyt
            // Nopeampi matti arvokkaammaksi.
            return 0x1000 * depth * -threats.size();
        }
        if (depth == 0) {
            return color * pos.evaluate(color * moveCount);
        }
        for (Move m : moves) {
            Position p = new Position(pos);
            p.executeMove(m);
            p.changeTurn();
            int value = -negamax(p, depth - 1, -b, -a, -color);
            if (value >= b) return b;
            if (value > a) a = value;
        }
        //return max;
        return a;
    }

    private class MultimapWorkaround implements Comparable {
        Integer value;
        Move move;

        public MultimapWorkaround(int val, Move m) {
            value = val;
            move = m;
        }

        @Override
        public int compareTo(Object another) {
            MultimapWorkaround other = (MultimapWorkaround) another;
            int equals = value - other.value;
            if (equals == 0) {
                equals = move.hashCode() - other.hashCode();
            }
            return equals;
        }
    }

    public static int completion() {
        synchronized (negamaxed) {
            return (int) (100 * ((float) negamaxed / (float) negamaxTotal));
        }
    }

    private void minmax(Move m, TreeSet<MultimapWorkaround> values, int color) {
        Position p = new Position(this);
        p.executeMove(m);
        p.changeTurn();
        // Debug: 4 max
        // Release: 5-6
        // Suurenna kun napit vähenee runsaasti?
        int value = color * negamax(p, 3, -30000, 30000, color);
        synchronized (values) {
            values.add(new MultimapWorkaround(value, m));
        }
    }

    private class MinMaxThread extends Thread {
        Move move;
        TreeSet<MultimapWorkaround> values;
        int color;

        MinMaxThread(Move m, TreeSet<MultimapWorkaround> vals, int colour) {
            move = m;
            values = vals;
            color = colour;
        }

        public void run() {
            minmax(move, values, color);
        }
    }

    public Move selectBestMove(List<Move> moves) {
        negamaxed = piecesEaten = 0;
        int movesAt0 = moves.size();
        negamaxTotal = movesAt0 * movesAt0 * movesAt0 * movesAt0;
        System.out.println("AI negamax:");
        List<Thread> threads = new LinkedList<Thread>();
        TreeSet<MultimapWorkaround> values = new TreeSet<MultimapWorkaround>();
        long time1 = System.currentTimeMillis();
        int color = (whoseTurn == Owner.WHITE) ? -1 : 1;
        for (Move m : moves) {
            MinMaxThread thread = new MinMaxThread(m, values, color);
            thread.run();
            threads.add(thread);
        }
        for (Thread t : threads) {
            boolean working = true;
            while (working) {
                try {
                    t.join();
                    working = false;
                } catch (InterruptedException e) {
                }
            }
        }
        for (MultimapWorkaround mw : values) {
            String line = "";
            switch (board[mw.move.fromRow][mw.move.fromCol].who) {
                case KING:
                    line = "King ";
                    break;
                case QUEEN:
                    line = "Queen ";
                    break;
                case ROOK:
                    line = "Rook ";
                    break;
                case BISHOP:
                    line = "Bishop ";
                    break;
                case KNIGHT:
                    line = "Knight ";
                    break;
                case PAWN:
                    line = "Pawn ";
                    break;
            }
            line += Position.printFile(mw.move.fromCol) + Position.printRank(mw.move.fromRow) + "-" +
                    Position.printFile(mw.move.toCol) + Position.printRank(mw.move.toRow);
            System.out.printf("%s : %d%n", line, mw.value);
        }

        long time2 = System.currentTimeMillis();
        System.out.println("Negamax used " + negamaxed + " times");
        System.out.println("Pieces eaten " + piecesEaten);
        System.out.println("Time elapsed: " + (time2 - time1) + " ms");
        ArrayList<Move> sameValues = new ArrayList<Move>();
        Random rand = new Random();
        rand.setSeed(time2);
        if (whoseTurn == Owner.WHITE) {
            for (MultimapWorkaround i : values.descendingSet()) {
                if (i.value - values.last().value > -5)
                    sameValues.add(i.move);
            }
        } else {
            for (MultimapWorkaround i : values) {
                if (i.value - values.first().value < 5)
                    sameValues.add(i.move);
            }
        }
        int pick = rand.nextInt(sameValues.size());
        System.out.println(pick + " : " + sameValues.size());
        return sameValues.get(pick);
    }

    private void pawnPromotion(Piece pawn, boolean AI) {
        Who type;
        //if(AI)
        {
            type = Who.QUEEN;
        }
        /*else
        {
            String cmd;
            //cout << "Give the promotion type [Q/N/R/B]" << endl;
            //getline(cin, cmd, '\n');
            //if(cmd.size() < 1) type = Who.QUEEN;
            switch(cmd[0])
            {
                case 'B': type = Who.BISHOP; break;
                case 'N': type = Who.KNIGHT; break;
                case 'Q': type = Who.QUEEN; break;
                case 'R': type = Who.ROOK; break;
                default: type = Who.QUEEN; break;
            }
        }*/
        List<Piece> list = (pawn.owner == Owner.BLACK) ? blackPieces : whitePieces;
        for (Piece p : list) {
            if (p == pawn) {
                p.who = type;
                return;
            }
        }
    }

    public void executeMove(Move m) {
        Piece to = board[m.toRow][m.toCol];
        if (null != to) {
            // Delete eatable chess piece.
            List<Piece> list = (to.owner == Owner.BLACK) ? blackPieces : whitePieces;
            list.remove(to);
            lastEatMove = movesDone + 1;
            synchronized (piecesEaten) {
                ++piecesEaten;
            }
        }

        Piece from = board[m.fromRow][m.fromCol];
        // Change pointers to chess pieces on the chess board.
        board[m.toRow][m.toCol] = from;
        board[m.fromRow][m.fromCol] = null;
        if (null == from) return;
        // Change the location of movable chess piece.
        from.row = Rank.val(m.toRow);
        from.col = File.val(m.toCol);

        if (canCastle != 0)
            switch (from.who) {
                case KING:
                    if (m.special != 0) {
                        if (m.special == 1) {
                            Move kingSide = new Move(File.H.ordinal(), from.row.ordinal(), File.F.ordinal(), from.row.ordinal());
                            executeMove(kingSide);
                        } else if (m.special == 2) {
                            Move queenSide = new Move(File.A.ordinal(), from.row.ordinal(), File.D.ordinal(), from.row.ordinal());
                            executeMove(queenSide);
                        }
                    }
                    if (from.owner == Owner.WHITE) {
                        canCastle &= ~3;
                    } else {
                        canCastle &= ~12;
                    }
                    passer = null;
                    return;
                case ROOK:
                    if (from.col == File.H) {
                        if (from.owner == Owner.WHITE)
                            canCastle &= ~1;
                        else
                            canCastle &= ~4;
                    } else if (from.col == File.A) {
                        if (from.owner == Owner.WHITE)
                            canCastle &= ~2;
                        else
                            canCastle &= ~8;
                    }
                    passer = null;
                    return;
            }

        // Has to be pawn.
        if (m.special >= 3) {
            if (m.special == 3) {
                passer = from;
                return;
            }
            List<Piece> list = (from.owner == Owner.WHITE) ? blackPieces : whitePieces;
            if (list.remove(passer))
                board[passer.row.ordinal()][passer.col.ordinal()] = null; // Will this work?
            lastEatMove = movesDone + 1;
        }

        if (from.who == Who.PAWN) {
            if (from.owner == Owner.WHITE) {
                if (from.row == Rank.R8) {
                    pawnPromotion(from, m.AI);
                }
            } else {
                if (from.row == Rank.R1) {
                    pawnPromotion(from, m.AI);
                }
            }
        }

        // This clears the en passant possibilty.
        passer = null;
        movesDone++;
    }

    public void changeTurn() {
        // Turn changes.
        whoseTurn = (whoseTurn == Owner.WHITE) ? Owner.BLACK : Owner.WHITE;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public static String printFile(int file) {
        switch (file) {
            case 0:
                return "a";
            case 1:
                return "b";
            case 2:
                return "c";
            case 3:
                return "d";
            case 4:
                return "e";
            case 5:
                return "f";
            case 6:
                return "g";
            case 7:
                return "h";
            default:
                return "?";
        }
    }

    public static String printRank(int rank) {
        switch (rank) {
            case 0:
                return "1";
            case 1:
                return "2";
            case 2:
                return "3";
            case 3:
                return "4";
            case 4:
                return "5";
            case 5:
                return "6";
            case 6:
                return "7";
            case 7:
                return "8";
            default:
                return "?";
        }
    }

    public static String printMove(Move m) {
        return printFile(m.fromCol) + printRank(m.fromRow) + "-" +
                printFile(m.toCol) + printRank(m.toRow);
    }
}
