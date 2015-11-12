package tapsa.shakki;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Position position;
    Piece[][] board;
    DrawingView surf;
    boolean cheat = false;
    GameThread gameThread = null;
    int fromCol = 0xF, fromRow, toCol, toRow;
    List<Move> moves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout layout = (FrameLayout) findViewById(R.id.frame001);
        layout.addView(new DrawingView(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        position = new Position();
        position.start();
        surf.invalidate();
        gameThread = new GameThread();
        gameThread.run();
    }

    class DrawingView extends SurfaceView implements SurfaceHolder.Callback {

        private final SurfaceHolder surfaceHolder;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int boardSide, viewWidth, viewHeight;
        private int[] lines;

        public DrawingView(Context context) {
            super(context);
            paint.setStyle(Paint.Style.FILL);
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            surf = this;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            updateBoard(canvas);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            viewWidth = getWidth();
            viewHeight = getHeight();
            boardSide = Math.min(viewWidth, viewHeight);
            lines = new int[]{
                    (int) (boardSide * 0.1f),
                    (int) (boardSide * 0.2f),
                    (int) (boardSide * 0.3f),
                    (int) (boardSide * 0.4f),
                    (int) (boardSide * 0.5f),
                    (int) (boardSide * 0.6f),
                    (int) (boardSide * 0.7f),
                    (int) (boardSide * 0.8f),
                    (int) (boardSide * 0.9f),
                    (int) (boardSide * 0.25f),
                    (int) (boardSide * 0.35f),
                    (int) (boardSide * 0.45f),
                    (int) (boardSide * 0.55f),
                    (int) (boardSide * 0.65f),
                    (int) (boardSide * 0.75f),
                    (int) (boardSide * 0.85f),
                    (int) (boardSide * 0.95f),
                    (int) (boardSide * 1.05f),
                    (int) (boardSide * 0.03f),
                    (int) (boardSide * 0.93f),
                    (int) (boardSide * 0.225f),
                    (int) (boardSide * 1.125f)
            };
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        protected void updateBoard(Canvas canvas) {
            paint.setTextSize(lines[0]);
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.RED);
            String turnText = (Owner.BLACK == position.tellTurn()) ? "AI's turn" : "Your turn";
            canvas.drawText(turnText, 10, lines[0], paint);

            // Paint the chess board
            paint.setColor(Color.YELLOW);
            canvas.drawRect(lines[0], lines[9], lines[8], lines[17], paint);
            paint.setColor(Color.rgb(128, 128, 0));
            canvas.drawRect(lines[1], lines[9], lines[2], lines[10], paint);
            canvas.drawRect(lines[3], lines[9], lines[4], lines[10], paint);
            canvas.drawRect(lines[5], lines[9], lines[6], lines[10], paint);
            canvas.drawRect(lines[7], lines[9], lines[8], lines[10], paint);
            canvas.drawRect(lines[0], lines[10], lines[1], lines[11], paint);
            canvas.drawRect(lines[2], lines[10], lines[3], lines[11], paint);
            canvas.drawRect(lines[4], lines[10], lines[5], lines[11], paint);
            canvas.drawRect(lines[6], lines[10], lines[7], lines[11], paint);
            canvas.drawRect(lines[1], lines[11], lines[2], lines[12], paint);
            canvas.drawRect(lines[3], lines[11], lines[4], lines[12], paint);
            canvas.drawRect(lines[5], lines[11], lines[6], lines[12], paint);
            canvas.drawRect(lines[7], lines[11], lines[8], lines[12], paint);
            canvas.drawRect(lines[0], lines[12], lines[1], lines[13], paint);
            canvas.drawRect(lines[2], lines[12], lines[3], lines[13], paint);
            canvas.drawRect(lines[4], lines[12], lines[5], lines[13], paint);
            canvas.drawRect(lines[6], lines[12], lines[7], lines[13], paint);
            canvas.drawRect(lines[1], lines[13], lines[2], lines[14], paint);
            canvas.drawRect(lines[3], lines[13], lines[4], lines[14], paint);
            canvas.drawRect(lines[5], lines[13], lines[6], lines[14], paint);
            canvas.drawRect(lines[7], lines[13], lines[8], lines[14], paint);
            canvas.drawRect(lines[0], lines[14], lines[1], lines[15], paint);
            canvas.drawRect(lines[2], lines[14], lines[3], lines[15], paint);
            canvas.drawRect(lines[4], lines[14], lines[5], lines[15], paint);
            canvas.drawRect(lines[6], lines[14], lines[7], lines[15], paint);
            canvas.drawRect(lines[1], lines[15], lines[2], lines[16], paint);
            canvas.drawRect(lines[3], lines[15], lines[4], lines[16], paint);
            canvas.drawRect(lines[5], lines[15], lines[6], lines[16], paint);
            canvas.drawRect(lines[7], lines[15], lines[8], lines[16], paint);
            canvas.drawRect(lines[0], lines[16], lines[1], lines[17], paint);
            canvas.drawRect(lines[2], lines[16], lines[3], lines[17], paint);
            canvas.drawRect(lines[4], lines[16], lines[5], lines[17], paint);
            canvas.drawRect(lines[6], lines[16], lines[7], lines[17], paint);

            // Paint the pieces
            paint.setTextSize(boardSide * 0.08f);
            paint.setColor(Color.rgb(192, 192, 192));
            canvas.drawText("a", boardSide * 0.13f, lines[20], paint);
            canvas.drawText("b", boardSide * 0.23f, lines[20], paint);
            canvas.drawText("c", boardSide * 0.33f, lines[20], paint);
            canvas.drawText("d", boardSide * 0.43f, lines[20], paint);
            canvas.drawText("e", boardSide * 0.53f, lines[20], paint);
            canvas.drawText("f", boardSide * 0.63f, lines[20], paint);
            canvas.drawText("g", boardSide * 0.73f, lines[20], paint);
            canvas.drawText("h", boardSide * 0.83f, lines[20], paint);
            canvas.drawText("8", lines[18], boardSide * 0.325f, paint);
            canvas.drawText("7", lines[18], boardSide * 0.425f, paint);
            canvas.drawText("6", lines[18], boardSide * 0.525f, paint);
            canvas.drawText("5", lines[18], boardSide * 0.625f, paint);
            canvas.drawText("4", lines[18], boardSide * 0.725f, paint);
            canvas.drawText("3", lines[18], boardSide * 0.825f, paint);
            canvas.drawText("2", lines[18], boardSide * 0.925f, paint);
            canvas.drawText("1", lines[18], boardSide * 1.025f, paint);
            canvas.drawText("a", boardSide * 0.13f, lines[21], paint);
            canvas.drawText("b", boardSide * 0.23f, lines[21], paint);
            canvas.drawText("c", boardSide * 0.33f, lines[21], paint);
            canvas.drawText("d", boardSide * 0.43f, lines[21], paint);
            canvas.drawText("e", boardSide * 0.53f, lines[21], paint);
            canvas.drawText("f", boardSide * 0.63f, lines[21], paint);
            canvas.drawText("g", boardSide * 0.73f, lines[21], paint);
            canvas.drawText("h", boardSide * 0.83f, lines[21], paint);
            canvas.drawText("8", lines[19], boardSide * 0.325f, paint);
            canvas.drawText("7", lines[19], boardSide * 0.425f, paint);
            canvas.drawText("6", lines[19], boardSide * 0.525f, paint);
            canvas.drawText("5", lines[19], boardSide * 0.625f, paint);
            canvas.drawText("4", lines[19], boardSide * 0.725f, paint);
            canvas.drawText("3", lines[19], boardSide * 0.825f, paint);
            canvas.drawText("2", lines[19], boardSide * 0.925f, paint);
            canvas.drawText("1", lines[19], boardSide * 1.025f, paint);
            board = position.getBoard();
            for (int row = 0; row < 8; ++row) {
                for (int col = 0; col < 8; ++col) {
                    if (null != board[row][col]) {
                        if (board[row][col].owner == Owner.BLACK)
                            paint.setColor(Color.BLACK);
                        else
                            paint.setColor(Color.BLUE);
                        int imgrow = row - 7;
                        canvas.drawText(board[row][col].debugPrint(), boardSide * ((col * 0.1f + 0.03f) + 0.1f), boardSide * ((-imgrow * 0.1f + 0.025f) + 0.3f), paint);
                    }
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            paint.setTextSize(lines[0]);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (surfaceHolder.getSurface().isValid()) {
                    int xfile = (int) ((event.getX() - lines[0]) / boardSide * 10);
                    int yrank = -(int) ((event.getY() - lines[9]) / boardSide * 10 - 8);
                    String message = "";
                    boolean moved = false;

                    if (Owner.WHITE == position.tellTurn()) {
                        if (0 <= xfile && xfile < 8 && 0 <= yrank && yrank < 8) {
                            if (fromCol == 0xF) {
                                fromCol = xfile;
                                fromRow = yrank;
                                message = printFile(fromCol) + printRank(fromRow) + "-";
                            } else {
                                toCol = xfile;
                                toRow = yrank;
                                Move move = new Move(fromCol, fromRow, toCol, toRow);
                                boolean legal = false;
                                for (Move m : moves) {
                                    if (m.fromCol == move.fromCol
                                            && m.fromRow == move.fromRow
                                            && m.toCol == move.toCol
                                            && m.toRow == move.toRow) {
                                        message = printFile(fromCol) + printRank(fromRow) + "-" +
                                                printFile(toCol) + printRank(toRow);
                                        legal = true;
                                        m.AI = false;
                                        System.out.println("You made a move");
                                        position.executeMove(m);
                                        moved = true;
                                        break;
                                    }
                                }
                                if (!legal) {
                                    message = "Illegal move!";
                                    fromCol = 0xF;
                                }
                            }
                        } else {
                            message = "Make a move like e2-e4";
                            fromCol = 0xF;
                        }
                    } else message = "AI is thinking";

                    Canvas canvas = surfaceHolder.lockCanvas();
                    updateBoard(canvas);
                    paint.setColor(Color.RED);
                    canvas.drawText(message, 10, boardSide * 1.25f, paint);
                    // Paint the touch point
                    canvas.drawCircle(event.getX(), event.getY(), boardSide * 0.02f, paint);
                    surfaceHolder.unlockCanvasAndPost(canvas);

                    // Turn changes.
                    if (moved) {
                        position.changeTurn();
                        gameThread = new GameThread();
                        gameThread.run();
                    }
                }
            }
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class GameThread extends Thread {
        public GameThread() {
        }

        public void run() {
            boolean moved = false;
            if (position.isDraw()) {
                // cout << "DRAW!" << endl;
                return;
            }
            moves = new LinkedList<Move>();
            IntRef result = new IntRef();
            // cout << "Legal moves: " <<
            position.generateLegalMoves(moves, result);
            //if(result.value != 0) cout << "Check!" << endl;
            if (moves.isEmpty()) {
                //cout << "GAME ENDED!" << endl;
                return;
            }
            position.showSpecialInfo();

            if (Owner.BLACK == position.tellTurn()) {
                Move bestMove = position.selectBestMove(moves);
                //printMove(bestMove);
                System.out.println("AI made a move");
                position.executeMove(bestMove);
                moved = true;
            }

            surf.invalidate();

            if (moved) {
                position.changeTurn();
                gameThread = new GameThread();
                gameThread.run();
            }
        }
    }

    public String printFile(int file) {
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

    public String printRank(int rank) {
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
}
