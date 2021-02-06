package com.example.sharath.chatbox;

public class Messages {

    private String message, type;
    private long time;
    private Boolean isseen;

    private String from;
    public Messages(String from){ this.from = from;}

    public Messages(String message, String type, long time, Boolean isseen) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.isseen = isseen;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Boolean getIsseen() {
        return isseen;
    }

    public void setIsseen(Boolean isseen) {
        this.isseen = isseen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Messages(){

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


}
