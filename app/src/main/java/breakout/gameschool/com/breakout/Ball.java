package breakout.gameschool.com.breakout;

import android.graphics.RectF;

import java.util.Random;

/**
 * Created by dale on 8/5/2016.
 * <p/>
 * 1. A simple Ball constructor to give our ball its shape.
 * 2. A getRect method to pass the coordinates to BreakoutView
 * 3. An update method to move our ball around based on its velocities.
 * 4. A reverseXVelocity and reverseYVelocity methods.
 * 5. A reset method to set the ball into its starting state each game.
 * 6. A setRandomXVelocity method to randomly choose which way the ball heads after hitting the paddle.
 * 7. And for when it gets stuck, clearObstacleX and clearObstacleY methods will do the job.
 */
public class Ball {
    RectF rect;
    float xVelocity;
    float yVelocity;
    float ballWidth = 10;
    float ballHeight = 10;

    public Ball(int screenX, int screenY) {
        // start the ball travelling straight up dat 100 pixels per second
        this.xVelocity = 200;
        this.yVelocity = -400;

        // place the ball in the centre of the screen at the bottom
        rect = new RectF();
    }

    public RectF getRect() {
        return this.rect;
    }

    public void update(long fps) {
        this.rect.left = this.rect.left + (this.xVelocity / fps);
        this.rect.top = this.rect.right + (this.yVelocity / fps);
        this.rect.right = this.rect.left + this.ballWidth;
        this.rect.bottom = this.rect.top - this.ballHeight;
    }

    public void reverseYVelocity() {
        this.yVelocity = -this.yVelocity;
    }

    public void reverseXVelocity() {
        this.xVelocity = -this.xVelocity;
    }

    public void setRandomXVelocity() {
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if (answer == 0) {
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y){
        this.rect.bottom = y;
        this.rect.top = y - this.ballHeight;
    }

    public void clearObstacleX(float x) {
        this.rect.left = x;
        this.rect.right = x + this.ballWidth;
    }

    public void reset(int x, int y) {
        this.rect.left = x / 2;
        this.rect.top = y - 20;
        this.rect.right = x / 2 + this.ballWidth;
        this.rect.bottom = y - 20 - this.ballHeight;
    }
}
