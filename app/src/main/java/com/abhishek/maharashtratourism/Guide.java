package com.abhishek.maharashtratourism;

import java.util.List;
import java.util.Map;

public class Guide {

    private String name,phone,email,fees,city,languages,specialization,experience,profileImageUrl,password,status;
    private boolean verified;
    private String id;
    private Map<String, String> documents;

    public Guide() {
    }

    public Guide(String id,String name, String phone, String email, String fees, String city, String languages, String specialization, String experience, String profileImageUrl,boolean verified,String password,Map<String, String> documents,String status ) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.fees = fees;
        this.city = city;
        this.languages = languages;
        this.specialization = specialization;
        this.experience = experience;
        this.profileImageUrl = profileImageUrl;
        this.verified=verified;
        this.password=password;
        this.documents=documents;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, String> getDocuments() {
        return documents;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getFees() {
        return fees;
    }

    public String getCity() {
        return city;
    }

    public String getLanguages() {
        return languages;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getExperience() {
        return experience;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
