package hruchnik.Arcanoid;
/*
 * By Svyatoslav Matviyenko
 */
//rf

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Arcanoid extends WindowProgram implements Constants {

    //Width and height of application window in pixels
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    //Dimensions of game board (usually the same)
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;



    //Width of a brick
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;



    /* Variables, that cant be set to local. Program using paddle and ball in
     * many functions, and dx and dy - as move vectors for ball (in many
     * functions too.
     */
    GRect paddle;
    GOval ball;
    double vx;
    double vy = VELOCITY_Y;



    public void run() {

       Ball ball2 = new Ball(WIDTH / 2.0 - BALL_RADIUS,
                              HEIGHT / 2.0 - BALL_RADIUS,
                              2,5,
                            getGCanvas());

        addMouseListeners();
        int bricks = setupGame();
        playGame(NTURNS, bricks);

    }

    /* Making bricks for arcanoid. Starts from BRICK_Y_OFFSET, and centered by x
     * Count for cycle using constants
     */
    private void makeBricks() {
        double x = WIDTH / 2.0 -
                ((BRICK_WIDTH * NBRICKS_PER_ROW) +
                        (BRICK_SEP) * NBRICKS_PER_ROW - 1) / 2.0;
        double y = BRICK_Y_OFFSET;
        for (int j = 0; j < NBRICK_ROWS; j++) {
            for (int i = 0; i < NBRICKS_PER_ROW; i++) {
                addOneBrick(x + i * (BRICK_WIDTH + BRICK_SEP),
                        y + j * (BRICK_HEIGHT + BRICK_SEP),
                        j);
            }
        }
    }

    /* Adding bricks in top of our screen. Method takes x and y parameters,
     * and rowNumber parameter, to understand which row number is it, as we
     * paint them in different colors.
     */
    private void addOneBrick(double x, double y, int rowNumber) {
        GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
        if (rowNumber == 0 || rowNumber == 1) {
            fillBrick(brick, Color.RED);
        } else if (rowNumber == 2 || rowNumber == 3) {
            fillBrick(brick, Color.ORANGE);
        } else if (rowNumber == 4 || rowNumber == 5) {
            fillBrick(brick, Color.YELLOW);
        } else if (rowNumber == 6 || rowNumber == 7) {
            fillBrick(brick, Color.GREEN);
        } else if (rowNumber == 8 || rowNumber == 9) {
            fillBrick(brick, Color.CYAN);
        }
        add(brick);
    }

    // Just simple fill brick in needed color
    private void fillBrick(GRect obj, Color color) {
        obj.setFilled(true);
        obj.setColor(color);
    }

    // Setup a game by adding a ball, and play it by the next method
    private int setupGame() {
        int bricksLeft = NBRICK_ROWS * NBRICKS_PER_ROW;
        makePaddle();
        makeBricks();
        makeBall();
        return bricksLeft;
    }

    // Program waits for click mouse, and begins to move ball in some direction
    private void playGame(int life, int bricksLeft) {
        GLabel livesLeftLabel = livesLeft(life);
        add(livesLeftLabel);
        waitForClick();
        remove(livesLeftLabel);
        playOneRoundOfGame(ball, life, bricksLeft);
    }

    /* Moving a ball. It starts with random vector x, and has a 50% chance to
     * fall left or right. Method checks collisions with elements of game
     * such as walls, bricks and paddle. It also checks if user loose
     */
    private void playOneRoundOfGame(GOval ball, int life, int bricksLeft) {

        RandomGenerator rgen = RandomGenerator.getInstance();
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;

        while (bricksLeft > 0) {
            ball.move(vx, vy);

            collisionWithWall();
            bricksLeft = collisionWithBrick(bricksLeft);
            collisionWithPaddle();
            looseCondition(life, bricksLeft);

            pause(PAUSE_TIME);
        }
        userWin();
    }

    //Checks loose condition for user (when ball falls to bottom line)
    private void looseCondition(int life, int bricks) {
        if (ball.getY() >= HEIGHT - ball.getHeight()) {
            userLoose(life, bricks);
        }
    }

    /* Check collisions with paddle, and change vector of vertical
     * or horizontal velocity (if touched left or right side of paddle)
     */
    private void collisionWithPaddle() {

        GObject colliderY = getTopOrBottomObject();
        if (colliderY == paddle && vy > 0 && ballIsNotTooLow(ball)) {
            vy = -vy;
        }

        GObject colliderX = getLeftOrRightObject();
        double paddleXCenter = paddle.getX() + paddle.getWidth() / 2;
        if (colliderX == paddle && vy > 0) {
            if (ball.getX() < paddleXCenter && vx > 0) {
                vx = -vx;
            }
            if (ball.getX() > paddleXCenter && vx < 0) {
                vx = -vx;
            }
        }
    }

    //Method for checking if ball is not too much under the paddle
    private boolean ballIsNotTooLow(GOval ball) {
        double bottomOfBall = ball.getY() + BALL_RADIUS * 2;
        double edge = paddle.getY() + paddle.getHeight();
        return bottomOfBall < edge;
    }

    /* Check collision with brick, and change vector of vertical or horizontal
     * velocity if ball touches brick, and remove that brick.
     */
    private int collisionWithBrick(int brick) {
        GObject colliderY = getTopOrBottomObject();
        if (colliderY != null && colliderY != paddle) {
            brick--;
            remove(colliderY);
            vy = -vy;
        }
        GObject colliderX = getLeftOrRightObject();
        if (colliderX != null && colliderX != paddle) {
            brick--;
            remove(colliderX);
            vx = -vx;
        }
        return brick;
    }

    //When user wins, remove all from screen, and add win words!
    private void userWin() {
        removeAll();
        printWinWords();
    }

    // Simply print words if user wins. It is centered in window
    private void printWinWords() {
        GLabel winWords = new GLabel("YOU WIN!!!");
        winWords.setFont("Verdana-50");
        winWords.setLocation(((WIDTH / 2.0) - winWords.getWidth() / 2),
                (HEIGHT / 2.0) - winWords.getHeight() / 2);
        add(winWords);
    }

    /* What to do if user loose one round. Remove old ball from screen, and
     * play game again. User loose one life.
     */
    private void userLoose(int life, int bricks) {
        remove(ball);
        life--;
        if (life <= 0) {
            removeAll();
            gameOver();
        } else {
            makeBall();
            playGame(life, bricks);
        }
    }

    // Making new label with amount of lives left. It is centered in the window
    private GLabel livesLeft(int l) {
        GLabel livesLeft = new GLabel("Lives left: " + l);
        livesLeft.setFont("Verdana-20");
        livesLeft.setLocation(((WIDTH / 2.0) - livesLeft.getWidth() / 2),
                (HEIGHT / 2.0) - livesLeft.getHeight() / 2);
        return livesLeft;
    }

    // Print text if user looses all his lives, and game ends.
    private void gameOver() {
        GLabel gameOver = new GLabel("GAME OVER");
        gameOver.setFont("Verdana-50");
        gameOver.setLocation(((WIDTH / 2.0) - gameOver.getWidth() / 2),
                (HEIGHT / 2.0) - gameOver.getHeight() / 2);
        add(gameOver);
    }

    /* Method for checking if any wall is touched. If so, change direction of
     * ball otherwise.
     */
    private void collisionWithWall() {
        if (ball.getX() <= 0 || ball.getX() >= WIDTH - ball.getWidth())
            vx = -vx;
        if (ball.getY() <= 0)
            vy = -vy;
    }

    // Checking of a ball (top, and bottom) if it is touching anything
    private GObject getTopOrBottomObject() {
        GObject result;
        double xTop = ball.getX() + BALL_RADIUS;
        double yTop = ball.getY() - 1;
        double xBottom = ball.getX() + BALL_RADIUS;
        double yBottom = (ball.getY() + BALL_RADIUS * 2) + 1;

        double[] arrayX = {xTop, xBottom};
        double[] arrayY = {yTop, yBottom};

        for (int i = 0; i < 2; i++) {
            result = checkCollidingOnePoint(arrayX[i], arrayY[i]);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    // Checking of a ball (left and right) if it is touching anything
    private GObject getLeftOrRightObject() {
        GObject result;
        double xLeft = ball.getX() - 1;
        double yLeft = ball.getY() + BALL_RADIUS;
        double xRight = (ball.getX() + BALL_RADIUS * 2) + 1;
        double yRight = ball.getY() + BALL_RADIUS;

        double[] arrayX = {xRight, xLeft};
        double[] arrayY = {yRight, yLeft};

        for (int i = 0; i < 2; i++) {
            result = checkCollidingOnePoint(arrayX[i], arrayY[i]);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    // Method for getting touched element
    private GObject checkCollidingOnePoint(double x, double y) {
        if (getElementAt(x, y) != null) {
            return getElementAt(x, y);
        }
        return null;
    }

    /* Making a ball, centered in window. Size of ball - in constant RADIUS.
     * It must be multiplied by 2 to get diameter
     */
    private void makeBall() {
        ball = new GOval(WIDTH / 2.0 - BALL_RADIUS, HEIGHT / 2.0 - BALL_RADIUS,
                BALL_RADIUS * 2, BALL_RADIUS * 2);
        ball.setFilled(true);
        ball.setColor(Color.BLACK);
        add(ball);
    }

    /* Making a paddle. It is centered in window x coordinate, and y is always
     * the same. Sizes of paddle is constant
     */
    private void makePaddle() {
        paddle = new GRect(WIDTH / 2.0 - PADDLE_WIDTH / 2.0,
                HEIGHT - PADDLE_Y_OFFSET * 2,
                PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setColor(Color.BLACK);
        add(paddle);
    }

    /* Moving the paddle. It must not to move out of window, so we use some
     * if's to protect paddle from moving out.
     */
    public void mouseMoved(MouseEvent e) {
        double yForPaddle = HEIGHT - PADDLE_Y_OFFSET * 2;
        if (e.getX() >= PADDLE_WIDTH / 2 && e.getX() <= WIDTH - PADDLE_WIDTH / 2) {
            paddle.setLocation(e.getX() - paddle.getWidth() / 2,    //new x
                    yForPaddle);            //Y is static
        } else if (e.getX() < PADDLE_WIDTH / 2) {
            paddle.setLocation(0,                                   //new x
                    yForPaddle);         //Y is static
        } else if (e.getX() > (WIDTH - PADDLE_WIDTH / 2)) {
            paddle.setLocation(WIDTH - PADDLE_WIDTH,           //new x
                    yForPaddle);         //Y is static
        }
    }
}