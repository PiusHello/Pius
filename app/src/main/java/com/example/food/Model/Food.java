package com.example.food.Model;

public class Food
{
   private String Name;
    private String Image;
    private String Description;
    private String  Price;
    private String  FoodID;


    public Food()
    {

    }

    public Food(String foodid,String name, String image, String description, String price) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        FoodID = foodid;
    }
    public String getFoodID() {
        return FoodID;
    }

    public void setFoodID(String foodid) {
        FoodID = foodid;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
