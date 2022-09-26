package com.kartik.homework.utils;

import java.util.Date;
import java.util.List;

public class Homework {

    String title, content, author, className, authorId;
    Date timeStamp, dueDate;
    List<String> notSeenBy;

    public Homework(String Title, String Content, String Author, String ClassName, String AuthorID, Date TimeStamp, Date DueDate, List<String> NotSeenBy) {
        this.title = Title;
        this.content = Content;
        this.author = Author;
        this.className = ClassName;
        this.authorId = AuthorID;
        this.timeStamp = TimeStamp;
        this.dueDate = DueDate;
        this.notSeenBy = NotSeenBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public List<String> getNotSeenBy() {
        return notSeenBy;
    }

    public void setNotSeenBy(List<String> notSeenBy) {
        this.notSeenBy = notSeenBy;
    }
}
