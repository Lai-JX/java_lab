package edu.hitsz.Dao;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        System.out.println("当前时间为: " + ft.format(date));
//        System.out.println(date);
        this.time = ft.format(date);
    }

}
