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
    private Position position;
    private List<Move> moves;
    private List<Piece> threats;
    private boolean cheat = false, moreInfo = false, playing = false, waiter = false;
    private String gameInfo = "", gameInfo2 = "";
    private Move bestMove = null;
    private DrawingView drawingView;

    static int colorBackGround = Color.rgb(178, 174, 160);// BLACK
    static int colorBoardLight = Color.rgb(235, 195, 139);// 233, 215, 0
    static int colorBoardDark = Color.rgb(180, 122, 81);// 191, 155, 0
    static int colorBoardLabel = Color.rgb(131, 79, 48);// 192
    static int colorMessage = Color.rgb(25, 25, 21);// RED
    static int colorTouchPoint = Color.rgb(200, 10, 21);
    static int colorBlackPiece = Color.rgb(22, 20, 15);// BLACK
    static int colorWhitePiece = Color.rgb(68, 87, 194);// WHITE
    static int colorLastMove = colorBoardLabel;
    static int colorThreat = colorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        drawingView = new DrawingView(this);
        ((FrameLayout) findViewById(R.id.frame001)).addView(drawingView);

        position = new Position();
        Position.translator = getApplicationContext().getResources();
        position.start();
        playing = true;
        new Thread(new GameThread()).start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        playing = true;
        if (waiter) new Thread(new GameThread()).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        drawingView.startGraphics();
    }

    @Override
    protected void onPause() {
        super.onPause();
        drawingView.stopGraphics();
    }

    @Override
    protected void onStop() {
        super.onStop();
        playing = false;
    }

    class DrawingView extends SurfaceView implements SurfaceHolder.Callback {
        private final SurfaceHolder surfaceHolder;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Bitmap boardBitmap = null;
        private int boardSide, viewWidth, viewHeight;
        private int fromCol = 0xF, fromRow, toCol, toRow, touchShowTime;
        private int[] lines;
        private DrawerThread thread = null;
        private String message = "", message2 = "";
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
                    } catch (InterruptedException ignored) {
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
            //startGraphics();
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
                    } catch (InterruptedException ignored) {
                    } finally {
                        if (null != canvas) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        }

        protected void updateBoard(Canvas canvas) {
            paint.setTextAlign(Paint.Align.LEFT);
            // Paint the chess board
            if (null == boardBitmap) {
                boardBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas background = new Canvas(boardBitmap);
                background.drawColor(colorBackGround);
                paint.setColor(colorBoardDark);
                background.drawRect(0, 0.15f * boardSide, boardSide, boardSide * 1.15f, paint);
                paint.setColor(colorBoardLight);
                background.drawRect(lines[0], lines[9], lines[1], lines[10], paint);
                background.drawRect(lines[2], lines[9], lines[3], lines[10], paint);
                background.drawRect(lines[4], lines[9], lines[5], lines[10], paint);
                background.drawRect(lines[6], lines[9], lines[7], lines[10], paint);
                background.drawRect(lines[1], lines[10], lines[2], lines[11], paint);
                background.drawRect(lines[3], lines[10], lines[4], lines[11], paint);
                background.drawRect(lines[5], lines[10], lines[6], lines[11], paint);
                background.drawRect(lines[7], lines[10], lines[8], lines[11], paint);
                background.drawRect(lines[0], lines[11], lines[1], lines[12], paint);
                background.drawRect(lines[2], lines[11], lines[3], lines[12], paint);
                background.drawRect(lines[4], lines[11], lines[5], lines[12], paint);
                background.drawRect(lines[6], lines[11], lines[7], lines[12], paint);
                background.drawRect(lines[1], lines[12], lines[2], lines[13], paint);
                background.drawRect(lines[3], lines[12], lines[4], lines[13], paint);
                background.drawRect(lines[5], lines[12], lines[6], lines[13], paint);
                background.drawRect(lines[7], lines[12], lines[8], lines[13], paint);
                background.drawRect(lines[0], lines[13], lines[1], lines[14], paint);
                background.drawRect(lines[2], lines[13], lines[3], lines[14], paint);
                background.drawRect(lines[4], lines[13], lines[5], lines[14], paint);
                background.drawRect(lines[6], lines[13], lines[7], lines[14], paint);
                background.drawRect(lines[1], lines[14], lines[2], lines[15], paint);
                background.drawRect(lines[3], lines[14], lines[4], lines[15], paint);
                background.drawRect(lines[5], lines[14], lines[6], lines[15], paint);
                background.drawRect(lines[7], lines[14], lines[8], lines[15], paint);
                background.drawRect(lines[0], lines[15], lines[1], lines[16], paint);
                background.drawRect(lines[2], lines[15], lines[3], lines[16], paint);
                background.drawRect(lines[4], lines[15], lines[5], lines[16], paint);
                background.drawRect(lines[6], lines[15], lines[7], lines[16], paint);
                background.drawRect(lines[1], lines[16], lines[2], lines[17], paint);
                background.drawRect(lines[3], lines[16], lines[4], lines[17], paint);
                background.drawRect(lines[5], lines[16], lines[6], lines[17], paint);
                background.drawRect(lines[7], lines[16], lines[8], lines[17], paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(boardSide * 0.005f);
                paint.setColor(colorBlackPiece);
                background.drawRect(lines[0], lines[9], lines[8], lines[17], paint);
                paint.setStyle(Paint.Style.FILL);

                paint.setColor(colorBoardLabel);
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

            // Highlight last move
            if (null != bestMove) {
                int r2 = 7 - bestMove.toRow;
                int c2 = bestMove.toCol;
                int r1 = 7 - bestMove.fromRow;
                int c1 = bestMove.fromCol;
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(colorLastMove);
                canvas.drawRect(lines[c2], lines[r2 + 9], lines[c2 + 1], lines[r2 + 10], paint);
                canvas.drawRect(lines[c1], lines[r1 + 9], lines[c1 + 1], lines[r1 + 10], paint);

                for (Piece t : threats) {
                    int r3 = 7 - t.row.ordinal();
                    int c3 = t.col.ordinal();
                    paint.setColor(colorThreat);
                    canvas.drawRect(lines[c3], lines[r3 + 9], lines[c3 + 1], lines[r3 + 10], paint);
                }
                paint.setStyle(Paint.Style.FILL);
            }

            // Paint messages
            paint.setColor(colorMessage);
            String turnText;
            if (Owner.BLACK == position.tellTurn()) {
                turnText = getString(R.string.ais_turn) + " ~" + Position.completion() + " %";
            } else turnText = getString(R.string.your_turn);
            canvas.drawText(turnText, 10, lines[0], paint);
            if (moreInfo) {
                message = gameInfo;
                message2 = gameInfo2;
                moreInfo = false;
            }
            canvas.drawText(message, 10, boardSide * 1.25f, paint);
            canvas.drawText(message2, 10, boardSide * 1.35f, paint);

            // Paint the pieces
            Piece[][] board = position.getBoard();
            paint.setTextAlign(Paint.Align.CENTER);
            for (int row = 0; row < 8; ++row) {
                for (int col = 0; col < 8; ++col) {
                    if (null != board[row][col]) {
                        paint.setColor(Owner.BLACK == board[row][col].owner ? colorBlackPiece : colorWhitePiece);
                        int imgrow = row - 7;
                        canvas.drawText(board[row][col].debugPrint(), boardSide * (col * 0.1f + 0.15f), boardSide * (-imgrow * 0.1f + 0.325f), paint);
                    }
                }
            }
            // Paint the touch point
            if (0 < --touchShowTime) {
                paint.setColor(colorTouchPoint);
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
                        if (fromCol == 0xF || position.isMyPiece(xfile, yrank)) {
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
                                message = getString(R.string.illegal_move);
                            } else fromCol = 0xF;
                        }
                    } else {
                        message = getString(R.string.example_move);
                        fromCol = 0xF;
                    }
                } else message = getString(R.string.ai_is_thinking);

                // Turn changes.
                if (moved) {
                    position.changeTurn();
                    new Thread(new GameThread()).start();
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
                Toast.makeText(context, getString(R.string.starting_new_game), duration).show();
                position.start();
                new Thread(new GameThread()).start();
                return true;
            case R.id.action_moves2:
                Position.levelOfAI = 1;
                Toast.makeText(context, getString(R.string.ai_thinks_2_moves), duration).show();
                return true;
            case R.id.action_moves3:
                Position.levelOfAI = 2;
                Toast.makeText(context, getString(R.string.ai_thinks_3_moves), duration).show();
                return true;
            case R.id.action_moves4:
                Position.levelOfAI = 3;
                Toast.makeText(context, getString(R.string.ai_thinks_4_moves), duration).show();
                return true;
            case R.id.action_moves5:
                Position.levelOfAI = 4;
                Toast.makeText(context, getString(R.string.ai_thinks_5_moves), duration).show();
                return true;
            case R.id.action_moves6:
                Position.levelOfAI = 5;
                Toast.makeText(context, getString(R.string.ai_thinks_6_moves), duration).show();
                return true;
            case R.id.action_AIvsAI:
                cheat = !cheat;
                item.setChecked(cheat);
                if (cheat) {
                    Toast.makeText(context, getString(R.string.ai_plays_for_you), duration).show();
                } else {
                    Toast.makeText(context, getString(R.string.play_yourself), duration).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class GameThread implements Runnable {
        public GameThread() {
        }

        public void run() {
            boolean moved = false;
            if (position.isDraw()) {
                gameInfo = getString(R.string.draw);
                moreInfo = true;
                return;
            }
            moves = new LinkedList<Move>();
            threats = new LinkedList<Piece>();
            System.out.println(getString(R.string.legal_moves) + ": " + position.generateLegalMoves(moves, threats));
            if (!threats.isEmpty()) {
                gameInfo = getString(R.string.check);
                for (Piece t : threats) {
                    gameInfo += "  @ " + Position.printFile(t.col.ordinal())
                            + Position.printRank(t.row.ordinal());
                }
            }
            if (moves.isEmpty()) {
                gameInfo = getString(R.string.game_ended);
                gameInfo2 = getString(Owner.BLACK == position.tellTurn() ? R.string.you_won : R.string.ai_won);
                moreInfo = true;
                return;
            }
            System.out.println(position.showSpecialInfo());

            if (Owner.BLACK == position.tellTurn() || cheat) {
                bestMove = position.selectBestMove(moves);
                gameInfo = getString(R.string.ai_made_move) + " " + Position.printMove(bestMove);
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
                } catch (IllegalArgumentException | SecurityException | IOException | IllegalStateException ignored) {
                }
                moved = true;
            }

            moreInfo = true;

            if (moved) {
                position.changeTurn();
                if (playing) new Thread(new GameThread()).start();
                else waiter = true;
            }
        }
    }
}
