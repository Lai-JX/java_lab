package edu.hitsz.Dao;

import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable {
    private String name;
    private int score;
    private String time;

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getTime() {
        return time;
    }

    public Record(String name, int score){
        this.name = name;
        this.score = score;
        Date date = new Date();
//        System.out.println(date);
        this.time = String.valueOf(date);
    }

}
