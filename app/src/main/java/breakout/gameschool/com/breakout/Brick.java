package breakout.gameschool.com.breakout;

import android.graphics.RectF;

/**
 * Created by dale on 8/5/2016.
 */
public class Brick {
    private RectF rect;
    private boolean isVisible;

    public Brick(int row, int column, int width, int height) {
        isVisible = true;
        int padding = 1;

        rect = new RectF(
                column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height +height - padding
        );
    }

    public RectF getRect() {
        return this.rect;
    }

    public void setInvisible() {
        this.isVisible = false;
    }

    public boolean getVisibility() {
        return this.isVisible;
    }
}
