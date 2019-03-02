package com.example.food.Model;

public class Users
{
    public String Username,Email,Password;
    public Users()
    {

    }

    public Users(String username, String email, String password) {
        Username = username;
        Email = email;
        Password = password;
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
}
