package com.example.food.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.Interface.ItemClickListener;
import com.example.food.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView foodName,foodDescription,foodPrice;
    public ImageView foodImage;
    public ItemClickListener itemClickListener;



    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        foodName =(TextView)itemView.findViewById(R.id.fn);
        foodPrice =(TextView)itemView.findViewById(R.id.fp);
        foodDescription =(TextView)itemView.findViewById(R.id.fd);
        foodImage =(ImageView) itemView.findViewById(R.id.fimage);

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
