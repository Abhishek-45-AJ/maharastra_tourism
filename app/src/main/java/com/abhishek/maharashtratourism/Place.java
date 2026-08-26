package com.abhishek.maharashtratourism;

public class Place {

    String name,city,description,category,rating,entryFee,latitude,longitude,website,contact,imageUrl;

    public Place() {
    }

    public Place(String name, String city, String description, String category, String rating, String entryFee,String latitude,String longitude, String website, String contact, String imageUrl) {
        this.name = name;
        this.city = city;
        this.description = description;
        this.category = category;
        this.rating = rating;
        this.entryFee = entryFee;
        this.latitude=latitude;
        this.longitude=longitude;
        this.website = website;
        this.contact = contact;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getRating() {
        return rating;
    }

    public String getEntryFee() {
        return entryFee;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getWebsite() {
        return website;
    }

    public String getContact() {
        return contact;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
