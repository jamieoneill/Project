package com.mismatched.nowyouretalking;

/**
 * Created by jamie on 10/03/2017.
 */

public class MessageModel {

    private String from;
    private String text;
    private String to;
    private String date;


    public MessageModel() {
    }

    public MessageModel(String from, String text, String to, String date) {
        this.from = from;
        this.text = text;
        this.to = to;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
