package edu.hitsz.prop;

public class BulletPropFactory  implements PropFactory {
    public AbstractProp createProp(int locationX, int locationY, int speedX, int speedY){
        return new BulletProp(locationX,locationY,speedX,speedY);
    }
}
