package hruchnik.Arcanoid;

import acm.graphics.GObject;

public abstract class GameObject {
    GObject itsGrObject;
    boolean collisionable;

    abstract void draw();


    abstract void move();

}
