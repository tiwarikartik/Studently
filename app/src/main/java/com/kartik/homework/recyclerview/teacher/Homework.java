package com.kartik.homework.recyclerview.teacher;

public class Homework {

    String title;
    String author;
    String time;
    String id;

    public Homework(String title, String author, String time, String id) {

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
        return time;
    }

    public String getId() {
        return id;
    }
}
