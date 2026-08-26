package com.abhishek.maharashtratourism;

public class GuideNotificationClass {

    private String type; // "call" or "message"
    private String userId;
    private String userName;
    private long timestamp;

    public GuideNotificationClass() {
    }

    public GuideNotificationClass(String type, String userId, String userName, long timestamp) {
        this.type = type;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
