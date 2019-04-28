package com.example.food.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.Interface.ItemClickListener;
import com.example.food.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView foodName;
    public TextView foodQuantity;
    public TextView foodPrice;
    public TextView date;
    public TextView time;
    public ImageView image;
    public ItemClickListener itemClickListener;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        foodName = itemView.findViewById(R.id.foodname);
        foodQuantity = itemView.findViewById(R.id.foodquantity);
        foodPrice = itemView.findViewById(R.id.foodprice);
        date = itemView.findViewById(R.id.firstdate);
        time = itemView.findViewById(R.id.firsttime);
        image = itemView.findViewById(R.id.cartFoodImage);
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.itemClickListener=listener;
    }

    @Override
    public void onClick(View v)
    {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
