package com.kartik.homework.recyclerview.pending;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pending {

    String title;
    String author;
    long time;
    String id;

    public Pending(String title, String author, String id, long time) {
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
