package com.kartik.homework.recyclerview.missed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Missed {

    String title;
    String author;
    String id;
    long time;

    public Missed(String title, String author, String id, long time) {
        this.title = title;
        this.author = author;
        this.time = time;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        return formatter.format(date);
    }

    public String getId() {
        return id;
    }
}
