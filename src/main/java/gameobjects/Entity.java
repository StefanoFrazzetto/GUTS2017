package gameobjects;

import java.awt.*;
import java.io.Serializable;

/**
 * Entity
 *
 * @author stefano
 * @version 1.0.0
 */
public class Entity implements Serializable {
    private int xCoordinate;

    private int yCoordinate;

    public void setCoordinates(int x, int y) {
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public Point getCoordinates() {
        return new Point(xCoordinate, yCoordinate);
    }
}
