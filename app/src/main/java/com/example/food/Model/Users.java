package com.example.food.Model;

public class Users {
    public String Username, Email, Image, Location, Phone;

    public Users() {

    }

    public Users(String username, String email, String image, String location, String phone) {
        Username = username;
        Email = email;
        Image = image;
        Location = location;
        Phone = phone;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}