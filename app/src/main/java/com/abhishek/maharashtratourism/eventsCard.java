package com.abhishek.maharashtratourism;

public class eventsCard {
    String name,date,location,description;
    int imageResource;

    public eventsCard(String name, String date,String location, int imageResource, String description) {
        this.name = name;
        this.date = date;
        this.location = location;

        this.imageResource = imageResource;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResource() {
        return imageResource;
    }

}
