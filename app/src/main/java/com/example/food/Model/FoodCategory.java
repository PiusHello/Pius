package com.example.food.Model;

public class FoodCategory {
    public String categoryName;
    public String categoryImage;
    public String description;
    public String deliveryDays;
    public String deliveryHours;

    public FoodCategory() {
    }

    public FoodCategory(String categoryName, String categoryImage, String description, String deliveryDays, String deliveryHours) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.description = description;
        this.deliveryDays = deliveryDays;
        this.deliveryHours = deliveryHours;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(String deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public String getDeliveryHours() {
        return deliveryHours;
    }

    public void setDeliveryHours(String deliveryHours) {
        this.deliveryHours = deliveryHours;
    }
}
