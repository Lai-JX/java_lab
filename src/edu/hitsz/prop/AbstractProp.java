package edu.hitsz.prop;

import edu.hitsz.basic.FlyingObject;

public abstract class AbstractProp extends FlyingObject{

    public AbstractProp(int locationX, int locationY, int speedX, int speedY){
        super(locationX,locationY,speedX,speedY);
    }

    public abstract void propWork();
}
