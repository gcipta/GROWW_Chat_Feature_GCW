package models;

public class CompassDirection {

    float direction;

    public CompassDirection(int direction){

        setDirection(direction);
    }

    public void setDirection(int direction) {

        this.direction = direction;
    }

    public float getDirection() {

        return direction;
    }
}
