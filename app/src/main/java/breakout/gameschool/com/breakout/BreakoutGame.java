package breakout.gameschool.com.breakout;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class BreakoutGame extends Activity {
    BreakoutView breakoutView;  // gameView will be the view of game, it will also hold the logic of game and response to screen touches as well

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
    }

    // start BreakoutView inner class
    class BreakoutView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;

        // game is paused at the start
        boolean paused = true;

        Canvas canvas;
        Paint paint;

        long fps; // tracking the game frame rate (frames per second)
        private long timeThisFrame; // calculate the fps

        int screenX;
        int screenY;

        // the players paddle
        Paddle paddle;

        // a ball
        Ball ball;

        // up to 200 bricks
        Brick[] bricks = new Brick[200];
        int numBricks = 0;

        // for sound FX
        SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;

        // the score
        int score = 0;

        // lives
        int lives = 3;

        public BreakoutView(Context context) {
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // get a dislay object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y;

            paddle = new Paddle(screenX, screenY);  // create a paddle
            ball = new Ball(screenX, screenY);       // create a ball

            // load the sounds
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            try {
                // create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // load fx in memory ready for use
                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);
            } catch (IOException e) {
                Log.e("Error Log", "failed to load sound files.");
            }

            createBricksAndRestart();
        }

        public void createBricksAndRestart() {
            // put the ball back to the start
            ball.reset(screenX, screenY);

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            // build a wall of bricks
            numBricks = 0;
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 3; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }

            // reset scores and lives
            if (lives == 0) {
                score = 0;
                lives = 3;
            }
        }

        @Override
        public void run() {
            while (playing) {
                // capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // update the frame
                if (!paused) {
                    update();
                }

                // draw the frame
                draw();

                // calculate the fps this frame, to time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        // everything that needs to be updated goes in here, movement & collision detection etc.
        public void update() {
            // move the paddle if required
            paddle.update(fps);

            ball.update(fps);

            //collision detection, check for ball colliding with a brick
            for (int i = 0; i < numBricks; i++) {
                // set the brick to be invisible; reverse the ball y direction of travel
                // add 10 to score; play the explode sound.
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score += 10;
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
            }

            // check for ball colliding with paddle
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);
                soundPool.play(beep1ID, 1, 1, 0, 0, 1);
            }

            // bounce the ball back when it hits the bottom of screen and deduct a life
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);
                lives--;        // lose a life
                soundPool.play(loseLifeID, 1, 1, 0, 0, 1);
                if (lives == 0) {
                    paused = true;
                    createBricksAndRestart();
                }
            }

            // bounce the ball back when it hits the top of screen
            if (ball.getRect().top < 0) {
                ball.reverseYVelocity();
                ball.clearObstacleY(12);
                soundPool.play(beep2ID, 1, 1, 0, 0, 1);
            }

            // if the ball hits left wall bounce
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // if the ball hits right wall bounce
            if (ball.getRect().right > screenX - 10) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // check if score = numBricks multiplied by ten --> all the blocks have been cleared, so restarting the game in the usual way.
            // pause if cleared screen
            if (score == numBricks * 10){
                paused = true;
                createBricksAndRestart();
            }
        }

        // draw the newly updated scene
        public void draw() {
            // make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();
                // draw the background color
                canvas.drawColor(Color.argb(255, 26, 128, 182));

                // choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // draw the paddle
                canvas.drawRect(paddle.getRect(), paint);

                // draw the ball
                canvas.drawRect(ball.getRect(), paint);

                // change the brush color for drawing
                paint.setColor(Color.argb(255, 249, 129, 0));

                // draw the bricks if visible
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }
                // draw HUD
                // choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // draw the score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + " Lives: " + lives, 10, 50, paint);

                // has the player cleared the screen ?
                if (score == numBricks * 10) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, screenY / 2, paint);
                }

                // has the player lost ?
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10, screenY / 2, paint);
                }

                // draw everything to screen completedly
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        // if BreakoutView instance is paused/stopped --> shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // if BreakoutView instance is started the start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // the override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    if (event.getX() > screenX / 2) {
                        paddle.setMovementState(paddle.RIGHT);
                    } else {
                        paddle.setMovementState(paddle.LEFT);
                    }
                    break;

                // player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }

            return true;
        }
    } // End of BreakoutView inner class

    @Override
    protected void onPause() {
        super.onPause();
        breakoutView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        breakoutView.resume();
    }
}







































