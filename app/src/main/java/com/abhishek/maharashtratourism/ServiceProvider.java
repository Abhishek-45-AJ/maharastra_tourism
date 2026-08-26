package com.abhishek.maharashtratourism;

public class ServiceProvider {
    String name, email, phone, city, languages, specialization, experience, fees, password, profileImageUrl;
    boolean verified;

    public ServiceProvider() {
    }

    public ServiceProvider(String name, String email, String phone, String city, String languages, String specialization, String experience, String fees, String password, boolean verified, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.languages = languages;
        this.specialization = specialization;
        this.experience = experience;
        this.fees = fees;
        this.password = password;
        this.verified = verified;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
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

    public String getFees() {
        return fees;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public boolean isVerified() {
        return verified;
    }
}
