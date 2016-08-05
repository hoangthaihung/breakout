package breakout.gameschool.com.breakout;

import android.graphics.RectF;

/**
 * Created by dale on 8/5/2016.
 */
public class Paddle {
    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // how long and high our paddle will be
    private float length;
    private float height;

    // x is the far left of the rectangle which forms our paddle
    private float x;
    // y is the top coordinate
    private float y;

    // the paddle will move with speed (the pixels per second)
    private float paddleSpeed;

    // which ways can the paddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // is the paddle moving and in which direction
    private int paddleMoving = STOPPED;

    // the Constructor, must pass the screen width and height
    public Paddle(int screenX, int screenY) {
        // 130 pixels wide and 20 pixels high
        length = 130;
        height = 20;

        // start paddle in roughly the screen centre
        x = screenX / 2;
        y = screenY - 20;

        rect = new RectF(x, y, x + length, y + height);

        // how fast is the paddle in pixels per second
        paddleSpeed = 350;
    }

    // this is a getter method to make the rectangle that defines our paddle
    // available in BreakoutView class
    public RectF getRect() {
        return this.rect;
    }

    // this method will be used to change/set if the paddle is going left,right or nowhere
    public void setMovementState(int state) {
        this.paddleMoving = state;
    }

    // this update method will be called from update in BreakoutView
    // it determines if the paddle needs to move and changes the coordinates
    public void update(long fps) {
        if (paddleMoving == LEFT) {
            x = x - paddleSpeed / fps;
        }

        if (paddleMoving == RIGHT) {
            x = x + paddleSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }
}






































