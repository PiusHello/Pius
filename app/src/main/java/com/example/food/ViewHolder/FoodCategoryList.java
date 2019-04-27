package com.example.food.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.Interface.ItemClickListener;
import com.example.food.R;

public class FoodCategoryList extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView FoodCategoryName;
    //FoodCategoryDescription;
    public ImageView FoodCategoryImage;
    public TextView DeliveryDays;
    public TextView DeliveryHours;
    public ItemClickListener listener;

    public FoodCategoryList(@NonNull View itemView) {
        super(itemView);

        FoodCategoryImage = (ImageView) itemView.findViewById(R.id.food_category_image);
        FoodCategoryName = (TextView) itemView.findViewById(R.id.food_category_name);
        DeliveryDays =(TextView) itemView.findViewById(R.id.deliveryDays);
        DeliveryHours =(TextView) itemView.findViewById(R.id.deliveryTime);
        //FoodCategoryDescription = (TextView) itemView.findViewById(R.id.food_category_description);
    }

    public  void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View v)
    {
        listener.onClick(v,getAdapterPosition(),false);
    }
}
