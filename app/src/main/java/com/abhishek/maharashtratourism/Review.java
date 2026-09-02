package com.abhishek.maharashtratourism;

import java.util.List;

public class Review {
    private String reviewId;
    private String userId;
    private String userName;
    private String comment;
    private float rating;
    private List<String> photoUrls;
    private long timestamp;

    public Review() {
        // Required for Firebase
    }

    // Getters and Setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
