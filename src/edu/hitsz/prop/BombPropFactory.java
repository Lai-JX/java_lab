package edu.hitsz.prop;

public class BombPropFactory  implements PropFactory {
    public AbstractProp createProp(int locationX, int locationY, int speedX, int speedY){
        return new BombProp(locationX,locationY,speedX,speedY);
    }
}
