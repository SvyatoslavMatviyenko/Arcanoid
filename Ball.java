package hruchnik.Arcanoid;

import acm.graphics.GCanvas;
import acm.graphics.GOval;

import java.awt.*;


public class Ball extends GameObject {


    private final double BALL_RADIUS = 10;
    private final double BALL_DIAMETER = BALL_RADIUS * 2.0;

    private double vx, vy;
    private double xPosition, yPosition;
    private GCanvas parentCanvas;

    //velocity of ball
    public void setX(double inX) {
        vx = inX;
    }

    public void setY(double inY) {
        vy = inY;
    }


    @Override
    public void draw() {
        parentCanvas.add(itsGrObject);
    }


    @Override
    public void move() {

    }

    public Ball(double xStartPosition, double yStartPosition, double speedForX, double speedForY, GCanvas ParentCanv) {
        parentCanvas = ParentCanv;
        xPosition = xStartPosition;
        yPosition = yStartPosition;

        GOval ballOval = new GOval(xPosition, yPosition + 50, BALL_DIAMETER, BALL_DIAMETER);
        ballOval.setColor(Color.BLACK);
        ballOval.setFilled(true);
        itsGrObject = ballOval;

        collisionable = true;
        draw();
    }
}
