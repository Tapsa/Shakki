package tapsa.shakki;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Position position;
    boolean cheat = false, moreInfo = false;
    GameThread gameThread = null;
    List<Move> moves;
    String gameInfo = "";

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
        gameThread = new GameThread();
        gameThread.start();
    }

    class DrawingView extends SurfaceView implements SurfaceHolder.Callback {
        private final SurfaceHolder surfaceHolder;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Bitmap boardBitmap = null;
        private int boardSide, viewWidth, viewHeight;
        private int fromCol = 0xF, fromRow, toCol, toRow, touchShowTime;
        private int[] lines;
        private DrawerThread thread = null;
        private String message = "";
        private float touchX, touchY;

        public DrawingView(Context context) {
            super(context);
            paint.setStyle(Paint.Style.FILL);
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
        }

        public void startGraphics() {
            if (null == thread) {
                thread = new DrawerThread();
                thread.startDrawing();
            }
        }

        public void stopGraphics() {
            if (null != thread) {
                thread.stopDrawing();
                boolean alive = true;
                while (alive) {
                    try {
                        thread.join();
                        alive = false;
                    } catch (InterruptedException e) {
                    }
                }
                thread = null;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //updateBoard(canvas);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            startGraphics();
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
            paint.setTextSize(boardSide * 0.08f);
            boardBitmap = null;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            stopGraphics();
        }

        class DrawerThread extends Thread {
            private boolean running = false;

            public DrawerThread() {
            }

            public void startDrawing() {
                running = true;
                super.start();
            }

            public void stopDrawing() {
                running = false;
            }

            public void run() {
                Canvas canvas;
                while (running) {
                    canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas();
                        synchronized (surfaceHolder) {
                            if (null != canvas) {
                                updateBoard(canvas);
                            }
                        }
                        sleep(300);
                    } catch (InterruptedException ie) {
                    } finally {
                        if (null != canvas) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }

        protected void updateBoard(Canvas canvas) {
            // Paint the chess board
            if (null == boardBitmap) {
                boardBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas background = new Canvas(boardBitmap);
                background.drawColor(Color.BLACK);
                paint.setColor(Color.YELLOW);
                background.drawRect(lines[0], lines[9], lines[8], lines[17], paint);
                paint.setColor(Color.rgb(128, 128, 0));
                background.drawRect(lines[1], lines[9], lines[2], lines[10], paint);
                background.drawRect(lines[3], lines[9], lines[4], lines[10], paint);
                background.drawRect(lines[5], lines[9], lines[6], lines[10], paint);
                background.drawRect(lines[7], lines[9], lines[8], lines[10], paint);
                background.drawRect(lines[0], lines[10], lines[1], lines[11], paint);
                background.drawRect(lines[2], lines[10], lines[3], lines[11], paint);
                background.drawRect(lines[4], lines[10], lines[5], lines[11], paint);
                background.drawRect(lines[6], lines[10], lines[7], lines[11], paint);
                background.drawRect(lines[1], lines[11], lines[2], lines[12], paint);
                background.drawRect(lines[3], lines[11], lines[4], lines[12], paint);
                background.drawRect(lines[5], lines[11], lines[6], lines[12], paint);
                background.drawRect(lines[7], lines[11], lines[8], lines[12], paint);
                background.drawRect(lines[0], lines[12], lines[1], lines[13], paint);
                background.drawRect(lines[2], lines[12], lines[3], lines[13], paint);
                background.drawRect(lines[4], lines[12], lines[5], lines[13], paint);
                background.drawRect(lines[6], lines[12], lines[7], lines[13], paint);
                background.drawRect(lines[1], lines[13], lines[2], lines[14], paint);
                background.drawRect(lines[3], lines[13], lines[4], lines[14], paint);
                background.drawRect(lines[5], lines[13], lines[6], lines[14], paint);
                background.drawRect(lines[7], lines[13], lines[8], lines[14], paint);
                background.drawRect(lines[0], lines[14], lines[1], lines[15], paint);
                background.drawRect(lines[2], lines[14], lines[3], lines[15], paint);
                background.drawRect(lines[4], lines[14], lines[5], lines[15], paint);
                background.drawRect(lines[6], lines[14], lines[7], lines[15], paint);
                background.drawRect(lines[1], lines[15], lines[2], lines[16], paint);
                background.drawRect(lines[3], lines[15], lines[4], lines[16], paint);
                background.drawRect(lines[5], lines[15], lines[6], lines[16], paint);
                background.drawRect(lines[7], lines[15], lines[8], lines[16], paint);
                background.drawRect(lines[0], lines[16], lines[1], lines[17], paint);
                background.drawRect(lines[2], lines[16], lines[3], lines[17], paint);
                background.drawRect(lines[4], lines[16], lines[5], lines[17], paint);
                background.drawRect(lines[6], lines[16], lines[7], lines[17], paint);

                paint.setColor(Color.rgb(192, 192, 192));
                background.drawText("a", boardSide * 0.13f, lines[20], paint);
                background.drawText("b", boardSide * 0.23f, lines[20], paint);
                background.drawText("c", boardSide * 0.33f, lines[20], paint);
                background.drawText("d", boardSide * 0.43f, lines[20], paint);
                background.drawText("e", boardSide * 0.53f, lines[20], paint);
                background.drawText("f", boardSide * 0.63f, lines[20], paint);
                background.drawText("g", boardSide * 0.73f, lines[20], paint);
                background.drawText("h", boardSide * 0.83f, lines[20], paint);
                background.drawText("8", lines[18], boardSide * 0.325f, paint);
                background.drawText("7", lines[18], boardSide * 0.425f, paint);
                background.drawText("6", lines[18], boardSide * 0.525f, paint);
                background.drawText("5", lines[18], boardSide * 0.625f, paint);
                background.drawText("4", lines[18], boardSide * 0.725f, paint);
                background.drawText("3", lines[18], boardSide * 0.825f, paint);
                background.drawText("2", lines[18], boardSide * 0.925f, paint);
                background.drawText("1", lines[18], boardSide * 1.025f, paint);
                background.drawText("a", boardSide * 0.13f, lines[21], paint);
                background.drawText("b", boardSide * 0.23f, lines[21], paint);
                background.drawText("c", boardSide * 0.33f, lines[21], paint);
                background.drawText("d", boardSide * 0.43f, lines[21], paint);
                background.drawText("e", boardSide * 0.53f, lines[21], paint);
                background.drawText("f", boardSide * 0.63f, lines[21], paint);
                background.drawText("g", boardSide * 0.73f, lines[21], paint);
                background.drawText("h", boardSide * 0.83f, lines[21], paint);
                background.drawText("8", lines[19], boardSide * 0.325f, paint);
                background.drawText("7", lines[19], boardSide * 0.425f, paint);
                background.drawText("6", lines[19], boardSide * 0.525f, paint);
                background.drawText("5", lines[19], boardSide * 0.625f, paint);
                background.drawText("4", lines[19], boardSide * 0.725f, paint);
                background.drawText("3", lines[19], boardSide * 0.825f, paint);
                background.drawText("2", lines[19], boardSide * 0.925f, paint);
                background.drawText("1", lines[19], boardSide * 1.025f, paint);
            }
            canvas.drawBitmap(boardBitmap, 0, 0, paint);

            // Paint messages
            paint.setColor(Color.RED);
            String turnText;
            if (Owner.BLACK == position.tellTurn()) {
                turnText = "AI's turn ~" + Position.completion() + " %";
            } else turnText = "Your turn";
            canvas.drawText(turnText, 10, lines[0], paint);
            if (moreInfo) {
                message = gameInfo;
                moreInfo = false;
            }
            canvas.drawText(message, 10, boardSide * 1.25f, paint);

            // Paint the pieces
            Piece[][] board = position.getBoard();
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
            // Paint the touch point
            if (0 < --touchShowTime) {
                paint.setColor(Color.RED);
                canvas.drawCircle(touchX, touchY, boardSide * 0.02f, paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchX = event.getX();
                touchY = event.getY();
                touchShowTime = 6;
                int xfile = (int) ((touchX - lines[0]) / boardSide * 10);
                int yrank = -(int) ((touchY - lines[9]) / boardSide * 10 - 8);
                boolean moved = false;

                if (Owner.WHITE == position.tellTurn()) {
                    if (0 <= xfile && xfile < 8 && 0 <= yrank && yrank < 8) {
                        if (fromCol == 0xF) {
                            fromCol = xfile;
                            fromRow = yrank;
                            message = Position.printFile(fromCol) + Position.printRank(fromRow) + "-";
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
                                    message = Position.printMove(move);
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
                            } else fromCol = 0xF;
                        }
                    } else {
                        message = "Make a move like e2-e4";
                        fromCol = 0xF;
                    }
                } else message = "AI is thinking";

                // Turn changes.
                if (moved) {
                    position.changeTurn();
                    gameThread = new GameThread();
                    gameThread.start();
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
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        switch (item.getItemId()) {
            case R.id.action_new_game:
                Toast.makeText(context, "Starting a new game", duration).show();
                position.start();
                gameThread = new GameThread();
                gameThread.start();
                return true;
            case R.id.action_moves2:
                Position.levelOfAI = 1;
                Toast.makeText(context, "AI now thinks 2 moves ahead", duration).show();
                return true;
            case R.id.action_moves4:
                Position.levelOfAI = 3;
                Toast.makeText(context, "AI now thinks 4 moves ahead", duration).show();
                return true;
            case R.id.action_moves6:
                Position.levelOfAI = 5;
                Toast.makeText(context, "AI now thinks 6 moves ahead", duration).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class GameThread extends Thread {
        public GameThread() {
        }

        public void run() {
            boolean moved = false;
            if (position.isDraw()) {
                gameInfo = "DRAW";
                moreInfo = true;
                return;
            }
            moves = new LinkedList<Move>();
            List<Piece> threats = new LinkedList<Piece>();
            System.out.println("Legal moves: " + position.generateLegalMoves(moves, threats));
            if (!threats.isEmpty()) {
                gameInfo = "Check!";
                for (Piece t : threats) {
                    gameInfo += "  @ " + Position.printFile(t.col.ordinal())
                            + Position.printRank(t.row.ordinal());
                }
            }
            if (moves.isEmpty()) {
                gameInfo = "GAME ENDED";
                moreInfo = true;
                return;
            }
            System.out.println(position.showSpecialInfo());

            if (Owner.BLACK == position.tellTurn()) {
                Move bestMove = position.selectBestMove(moves);
                gameInfo = "AI made a move " + Position.printMove(bestMove);
                position.executeMove(bestMove);
                // Play notification sound
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                    mediaPlayer.prepare();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                    mediaPlayer.start();
                } catch (IllegalArgumentException e) {
                } catch (SecurityException e) {
                } catch (IllegalStateException e) {
                } catch (IOException e) {
                }
                moved = true;
            }

            moreInfo = true;

            if (moved) {
                position.changeTurn();
                gameThread = new GameThread();
                gameThread.start();
            }
        }
    }
}
