package com.abhishek.maharashtratourism;

public class User {

    String name,phone,email,password, profileImage;

    public User(){
        //default constructor
    }

    public User(String name, String phone, String email,String password,String profileImage) {
        this.name=name;
        this.phone=phone;
        this.email=email;
        this.password=password;
        this.profileImage=profileImage;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
