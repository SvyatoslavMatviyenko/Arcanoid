package hruchnik.Arcanoid;

public interface Constants {
    //Dimensions of the paddle
    public static final int PADDLE_WIDTH = 60;
    public static final int PADDLE_HEIGHT = 10;

    //Offset of the paddle up from the bottom
    public static final int PADDLE_Y_OFFSET = 30;

    //Number of bricks per row
    public static final int NBRICKS_PER_ROW = 10;

    // Number of rows of bricks
    public static final int NBRICK_ROWS = 10;

    //Separation between bricks
    public static final int BRICK_SEP = 4;

    //Height of a brick
    public static final int BRICK_HEIGHT = 8;

    // Radius of the ball in pixels
    public static final int BALL_RADIUS = 10; //can be deleted

    //Offset of the top brick row from the top
    public static final int BRICK_Y_OFFSET = 70;

    // Number of turns
    public static final int NTURNS = 3;

    // Pause between frames.
    public static final int PAUSE_TIME = 8;

    // Y velocity of ball.
    public static final double VELOCITY_Y = 2;
}
