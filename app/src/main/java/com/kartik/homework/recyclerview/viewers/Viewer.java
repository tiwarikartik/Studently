package com.kartik.homework.recyclerview.viewers;

public class Viewer {

    String name;
    String rollNo;
    String emailId;
    String homeworkLink;
    boolean isNotified;
    boolean isFileUploaded;

    public Viewer(String name, String emailId, String homeworkLink, boolean isNotified, boolean isFileUploaded) {
        this.name = name;
        this.rollNo = rollNo;
        this.emailId = emailId;
        this.homeworkLink = homeworkLink;
        this.isNotified = isNotified;
        this.isFileUploaded = isFileUploaded;
    }
    public String getName() {
        return name;
    }
    public String getRollNo() {
        return rollNo;
    }
    public String getEmailId() {
        return emailId;
    }
    public String getHomeworkLink() {
        return homeworkLink;
    }
    public boolean isNotified() {
        return isNotified;
    }
    public boolean isFileUploaded() {
        return isFileUploaded;
    }
}
