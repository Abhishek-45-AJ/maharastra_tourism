package com.abhishek.maharashtratourism;

import java.io.Serializable;

public class Hotel implements Serializable {
    private String hotelId;
    private String name;
    private String city;
    private String type; // "Hotel", "Homestay", or "Resort"
    private String pricePerNight;
    private String image;
    private String description;
    private String contact;
    private String website;
    private double averageRating;
    private int totalReviews;

    public Hotel() {
        // Required for Firebase
    }

    // Getters and Setters
    public String getHotelId() { return hotelId; }
    public void setHotelId(String hotelId) { this.hotelId = hotelId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(String pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
}
