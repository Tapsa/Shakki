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

public class MainActivity extends AppCompatActivity {
    Position position;
    Piece[][] board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout layout = (FrameLayout) findViewById(R.id.frame001);
        layout.addView(new DrawingView(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    class DrawingView extends SurfaceView {

        private final SurfaceHolder surfaceHolder;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int viewWidth, viewHeight;

        public DrawingView(Context context) {
            super(context);
            surfaceHolder = getHolder();
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            viewWidth = getWidth();
            viewHeight = getHeight();
            int boardSide = Math.min(viewWidth, viewHeight);
            paint.setTextSize(boardSide * 0.1f);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (surfaceHolder.getSurface().isValid()) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.BLACK);
                    paint.setColor(Color.RED);
                    canvas.drawText("Status " + viewWidth + " " + viewHeight, 10, boardSide * 0.1f, paint);

                    // Paint the chess board
                    paint.setColor(Color.YELLOW);
                    canvas.drawRect(boardSide * 0.1f, boardSide * 0.25f, boardSide * 0.9f, boardSide * 1.05f, paint);
                    paint.setColor(Color.rgb(128, 128, 0));
                    canvas.drawRect(boardSide * 0.2f, boardSide * 0.25f, boardSide * 0.3f, boardSide * 0.35f, paint);
                    canvas.drawRect(boardSide * 0.4f, boardSide * 0.25f, boardSide * 0.5f, boardSide * 0.35f, paint);
                    canvas.drawRect(boardSide * 0.6f, boardSide * 0.25f, boardSide * 0.7f, boardSide * 0.35f, paint);
                    canvas.drawRect(boardSide * 0.8f, boardSide * 0.25f, boardSide * 0.9f, boardSide * 0.35f, paint);
                    canvas.drawRect(boardSide * 0.1f, boardSide * 0.35f, boardSide * 0.2f, boardSide * 0.45f, paint);
                    canvas.drawRect(boardSide * 0.3f, boardSide * 0.35f, boardSide * 0.4f, boardSide * 0.45f, paint);
                    canvas.drawRect(boardSide * 0.5f, boardSide * 0.35f, boardSide * 0.6f, boardSide * 0.45f, paint);
                    canvas.drawRect(boardSide * 0.7f, boardSide * 0.35f, boardSide * 0.8f, boardSide * 0.45f, paint);
                    canvas.drawRect(boardSide * 0.2f, boardSide * 0.45f, boardSide * 0.3f, boardSide * 0.55f, paint);
                    canvas.drawRect(boardSide * 0.4f, boardSide * 0.45f, boardSide * 0.5f, boardSide * 0.55f, paint);
                    canvas.drawRect(boardSide * 0.6f, boardSide * 0.45f, boardSide * 0.7f, boardSide * 0.55f, paint);
                    canvas.drawRect(boardSide * 0.8f, boardSide * 0.45f, boardSide * 0.9f, boardSide * 0.55f, paint);
                    canvas.drawRect(boardSide * 0.1f, boardSide * 0.55f, boardSide * 0.2f, boardSide * 0.65f, paint);
                    canvas.drawRect(boardSide * 0.3f, boardSide * 0.55f, boardSide * 0.4f, boardSide * 0.65f, paint);
                    canvas.drawRect(boardSide * 0.5f, boardSide * 0.55f, boardSide * 0.6f, boardSide * 0.65f, paint);
                    canvas.drawRect(boardSide * 0.7f, boardSide * 0.55f, boardSide * 0.8f, boardSide * 0.65f, paint);
                    canvas.drawRect(boardSide * 0.2f, boardSide * 0.65f, boardSide * 0.3f, boardSide * 0.75f, paint);
                    canvas.drawRect(boardSide * 0.4f, boardSide * 0.65f, boardSide * 0.5f, boardSide * 0.75f, paint);
                    canvas.drawRect(boardSide * 0.6f, boardSide * 0.65f, boardSide * 0.7f, boardSide * 0.75f, paint);
                    canvas.drawRect(boardSide * 0.8f, boardSide * 0.65f, boardSide * 0.9f, boardSide * 0.75f, paint);
                    canvas.drawRect(boardSide * 0.1f, boardSide * 0.75f, boardSide * 0.2f, boardSide * 0.85f, paint);
                    canvas.drawRect(boardSide * 0.3f, boardSide * 0.75f, boardSide * 0.4f, boardSide * 0.85f, paint);
                    canvas.drawRect(boardSide * 0.5f, boardSide * 0.75f, boardSide * 0.6f, boardSide * 0.85f, paint);
                    canvas.drawRect(boardSide * 0.7f, boardSide * 0.75f, boardSide * 0.8f, boardSide * 0.85f, paint);
                    canvas.drawRect(boardSide * 0.2f, boardSide * 0.85f, boardSide * 0.3f, boardSide * 0.95f, paint);
                    canvas.drawRect(boardSide * 0.4f, boardSide * 0.85f, boardSide * 0.5f, boardSide * 0.95f, paint);
                    canvas.drawRect(boardSide * 0.6f, boardSide * 0.85f, boardSide * 0.7f, boardSide * 0.95f, paint);
                    canvas.drawRect(boardSide * 0.8f, boardSide * 0.85f, boardSide * 0.9f, boardSide * 0.95f, paint);
                    canvas.drawRect(boardSide * 0.1f, boardSide * 0.95f, boardSide * 0.2f, boardSide * 1.05f, paint);
                    canvas.drawRect(boardSide * 0.3f, boardSide * 0.95f, boardSide * 0.4f, boardSide * 1.05f, paint);
                    canvas.drawRect(boardSide * 0.5f, boardSide * 0.95f, boardSide * 0.6f, boardSide * 1.05f, paint);
                    canvas.drawRect(boardSide * 0.7f, boardSide * 0.95f, boardSide * 0.8f, boardSide * 1.05f, paint);

                    // Paint the pieces
                    paint.setTextSize(boardSide * 0.08f);
                    paint.setColor(Color.rgb(192, 192, 192));
                    canvas.drawText("a", boardSide * 0.13f, boardSide * 0.225f, paint);
                    canvas.drawText("b", boardSide * 0.23f, boardSide * 0.225f, paint);
                    canvas.drawText("c", boardSide * 0.33f, boardSide * 0.225f, paint);
                    canvas.drawText("d", boardSide * 0.43f, boardSide * 0.225f, paint);
                    canvas.drawText("e", boardSide * 0.53f, boardSide * 0.225f, paint);
                    canvas.drawText("f", boardSide * 0.63f, boardSide * 0.225f, paint);
                    canvas.drawText("g", boardSide * 0.73f, boardSide * 0.225f, paint);
                    canvas.drawText("h", boardSide * 0.83f, boardSide * 0.225f, paint);
                    canvas.drawText("8", boardSide * 0.03f, boardSide * 0.325f, paint);
                    canvas.drawText("7", boardSide * 0.03f, boardSide * 0.425f, paint);
                    canvas.drawText("6", boardSide * 0.03f, boardSide * 0.525f, paint);
                    canvas.drawText("5", boardSide * 0.03f, boardSide * 0.625f, paint);
                    canvas.drawText("4", boardSide * 0.03f, boardSide * 0.725f, paint);
                    canvas.drawText("3", boardSide * 0.03f, boardSide * 0.825f, paint);
                    canvas.drawText("2", boardSide * 0.03f, boardSide * 0.925f, paint);
                    canvas.drawText("1", boardSide * 0.03f, boardSide * 1.025f, paint);
                    canvas.drawText("a", boardSide * 0.13f, boardSide * 1.125f, paint);
                    canvas.drawText("b", boardSide * 0.23f, boardSide * 1.125f, paint);
                    canvas.drawText("c", boardSide * 0.33f, boardSide * 1.125f, paint);
                    canvas.drawText("d", boardSide * 0.43f, boardSide * 1.125f, paint);
                    canvas.drawText("e", boardSide * 0.53f, boardSide * 1.125f, paint);
                    canvas.drawText("f", boardSide * 0.63f, boardSide * 1.125f, paint);
                    canvas.drawText("g", boardSide * 0.73f, boardSide * 1.125f, paint);
                    canvas.drawText("h", boardSide * 0.83f, boardSide * 1.125f, paint);
                    canvas.drawText("8", boardSide * 0.93f, boardSide * 0.325f, paint);
                    canvas.drawText("7", boardSide * 0.93f, boardSide * 0.425f, paint);
                    canvas.drawText("6", boardSide * 0.93f, boardSide * 0.525f, paint);
                    canvas.drawText("5", boardSide * 0.93f, boardSide * 0.625f, paint);
                    canvas.drawText("4", boardSide * 0.93f, boardSide * 0.725f, paint);
                    canvas.drawText("3", boardSide * 0.93f, boardSide * 0.825f, paint);
                    canvas.drawText("2", boardSide * 0.93f, boardSide * 0.925f, paint);
                    canvas.drawText("1", boardSide * 0.93f, boardSide * 1.025f, paint);
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

                    // Paint the touch point
                    paint.setColor(Color.RED);
                    canvas.drawCircle(event.getX(), event.getY(), boardSide * 0.02f, paint);
                    surfaceHolder.unlockCanvasAndPost(canvas);
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

    @Override
    public void onStart() {
        super.onStart();

        // Do whatever you want when the app launches
        position = new Position();
        position.start();
        Move move = new Move();
        boolean cheat = false, moved;
    }
}
