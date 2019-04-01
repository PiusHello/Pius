package com.example.food;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.Model.Cart;
import com.example.food.Prevalent.Prevalent;
import com.example.food.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button proceedButton;
    private TextView totalAmount;
    private ImageView cartFoodPicture;
    DatabaseReference cartList;

    private double OverRawTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView =findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        proceedButton = (Button) findViewById(R.id.proceedButton);
        totalAmount = (TextView) findViewById(R.id.totalprice);
        cartFoodPicture = (ImageView)findViewById(R.id.cartFoodImage);

        //This will execute when the Proceed button in the cart activity is Clicked.
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


             Intent intent = new Intent(CartActivity.this,ConfirmOrder.class);
             intent.putExtra("Total Price ", String.valueOf(OverRawTotalPrice));
             startActivity(intent);
                finish();
            }
        });


        cartList = FirebaseDatabase.getInstance().getReference("Cart List");
    }

    @Override
    protected void onStart() {
        super.onStart();

        totalAmount.setText("Total Price : " + String.valueOf(OverRawTotalPrice));
        //final DatabaseReference cartList = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartList.child("Users View")
                        .child(Prevalent.currentOnLineUser).child("Food_List"),Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                                holder.foodName.setText(" Food : " + model.getName());
                                holder.foodQuantity.setText(" Quantity : " + model.getQuantity());
                                holder.foodPrice.setText(" Price : " + model.getPrice());
                                holder.date.setText(" Date :" + model.getDate());
                                holder.time.setText(" Time : " + model.getTime());


                                Picasso.get().load(model.getImage()).into(holder.image);

                                //Calculations for each food at the cart level.
                                //int onlyOneItemPrice = (Integer.valueOf(model.getPrice())) * Integer.valueOf(model.getQuantity());
                                //OverRawTotalPrice = OverRawTotalPrice + onlyOneItemPrice;

                             //  double onlyOneItemPrice = ((Double.valueOf(model.getPrice()))) * Double.parseDouble(model.getQuantity());
                              //  OverRawTotalPrice = OverRawTotalPrice + onlyOneItemPrice;

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        CharSequence options[] = new CharSequence[]
                                                {
                                                        "Edit Cart",
                                                        "Delete Cart Item"
                                                };
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                        builder.setTitle("Options");
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                if(which == 0)
                                                {
                                                    Intent intent = new Intent(CartActivity.this,SelectedFoodDetails.class);
                                                     intent.putExtra("FoodID",model.getFoodID());
                                                     startActivity(intent);

                                                }
                                                if(which==1)
                                                {
                                                    cartList.child("Users View")
                                                            .child(Prevalent.currentOnLineUser)
                                                            .child("FoodList")
                                                            .child(model.getFoodID())
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        Toast.makeText(CartActivity.this,"One Item has Been Remove From The Cart",Toast.LENGTH_LONG).show();

                                                                        Intent intent = new Intent(CartActivity.this,HomeActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            });

                                                }

                                            }
                                        });
                                        builder.show();
                                    }
                                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                 View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_list_layout,viewGroup,false);
                 CartViewHolder holder = new CartViewHolder(view);
                 return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
