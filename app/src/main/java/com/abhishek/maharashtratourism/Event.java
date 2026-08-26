package com.abhishek.maharashtratourism;

public class Event {

    private String name,description,category,startDate,endDate,status,city,address,entryFee,website,contact,latitude,longitude,imageUrl;

    public Event(){}

    public Event(String name, String description, String category, String startDate, String endDate, String status, String city, String address, String entryFee, String website, String contact, String latitude, String longitude, String imageUrl) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.city = city;
        this.address = address;
        this.entryFee = entryFee;
        this.website = website;
        this.contact = contact;
        this.latitude=latitude;
        this.longitude=longitude;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getEntryFee() {
        return entryFee;
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
