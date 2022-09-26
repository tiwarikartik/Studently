package com.kartik.homework.utils;

public class Actions {
    boolean notified;
    boolean uploaded;
    String link;

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Actions(boolean notified, boolean uploaded, String link) {
        this.notified = notified;
        this.uploaded = uploaded;
        this.link = link;
    }
}
