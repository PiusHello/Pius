package com.example.food.Model;

public class Users {
    public String Username, Email, Password, Image, Location;

    public Users() {

    }

    public Users(String username, String email, String password, String image, String location) {
        Username = username;
        Email = email;
        Password = password;
        Image = image;
        Location = location;
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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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
}