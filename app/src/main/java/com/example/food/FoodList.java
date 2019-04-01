package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.food.Model.Food;
import com.example.food.Prevalent.Prevalent;
import com.example.food.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference mDatabase,userData;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    String categoryID;
   // TextView food_name,food_description,food_price;
    TextView FoodName,FoodPrice,FoodDescription;
    Button ToCart;
    ElegantNumberButton IncrementalButton;
    ImageView FoodImage;
    Query query;
    String foodkey = null;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        categoryID = getIntent().getStringExtra("CategoryID");
        Log.v("foodkeylist",categoryID);
        //DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("FoodList");

       // mDatabase = FirebaseDatabase.getInstance().getReference("FoodList").orderByChild("FoodID").getRef();
        //Query query = mDatabase.orderByChild("FoodID").equalTo("BreakFast");
        mDatabase = FirebaseDatabase.getInstance().getReference("FoodList");
         query = mDatabase.orderByChild("MenuID").equalTo(categoryID);

        foodkey = getIntent().getStringExtra("FoodID");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("FoodList");

        // mAuth = FirebaseAuth.getInstance();
        // currentUser = mAuth.getCurrentUser();

        FoodName = (TextView) findViewById(R.id.fn);
        FoodDescription = (TextView) findViewById(R.id.fd);
        FoodPrice = (TextView) findViewById(R.id.fp);
        FoodImage = (ImageView) findViewById(R.id.fimage);
       // ToCart = (Button) findViewById(R.id.addFood);
        IncrementalButton = (ElegantNumberButton) findViewById(R.id.IncrementalButton);


        //This will happen if the button at the FoodList Level is clicked

//        ToCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FoodToCart();
//            }
//        });


        recyclerView = (RecyclerView) findViewById(R.id.food_recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));

    }

    //This method will be called when a user click on the buuton at the FoodList Level
//                               private void FoodToCart()
//                            {
//                                Calendar calendarForDate = Calendar.getInstance();
//                                SimpleDateFormat currentDate =new SimpleDateFormat("MM dd, yyyy");
//                                String saveCurrentDate = currentDate.format(calendarForDate.getTime());
//
//                                SimpleDateFormat currentTime =new SimpleDateFormat("MM dd, yyyy");
//                                String saveCurrentTime = currentDate.format(calendarForDate.getTime());
//
//                                final DatabaseReference cartList = FirebaseDatabase.getInstance().getReference().child("Cart List");
//
//                                final HashMap<String,Object> CartMap = new HashMap<>();
//
//                                CartMap.put("FoodID",foodkey);
//                                CartMap.put("Name",FoodName.getText().toString());
//                                CartMap.put("Description",FoodDescription.getText().toString());
//                                CartMap.put("Price",FoodPrice.getText().toString());
//                                CartMap.put("Date",saveCurrentDate);
//                                CartMap.put("Time",saveCurrentTime);
//                                // CartMap.put("Image",food_image);
//                                CartMap.put("Quantity",IncrementalButton.getNumber());
//
//                                cartList.child("Users View").child(Prevalent.currentOnLineUser).child("Food_List")
//                                        .child(foodkey).updateChildren(CartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task)
//                                    {
//                                        if(task.isSuccessful())
//                                        {
//                                            cartList.child("Admin View").child(Prevalent.currentOnLineUser).child("Food_List").child(foodkey)
//                                                    .updateChildren(CartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task)
//                                                {
//
//                                                    if(task.isSuccessful())
//                                                    {
//                                                        Toast.makeText(FoodList.this,"Food Has Being Added To Cart",Toast.LENGTH_LONG).show();
//                                                        Intent intent = new Intent(FoodList.this,FoodList.class);
//                                                        startActivity(intent);
//                                                    }
//                                                }
//                                            });
//                                        }
//
//                                    }
//                                });
//                            }

                            @Override
                            protected void onStart() {
                            super.onStart();

                            FirebaseRecyclerOptions<Food> options=
                                    new FirebaseRecyclerOptions.Builder<Food>()
                                            .setQuery(query,Food.class)
                                            .build();

                            FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter=
                                    new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
                                        @Override
                                        protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model)
                                        {

                                            holder.foodName.setText(model.getName());
                                            holder.foodDescription.setText(model.getDescription());
                                            holder.foodPrice.setText(" Price ¢ " + model.getPrice());
                                            Picasso.get().load(model.getImage()).into(holder.foodImage);


                            //Sends the user to the FoodDetails when any food item is clicked under an
                            //category.
                            final String food_key =getRef(position).getKey().toString();
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent = new Intent(FoodList.this,SelectedFoodDetails.class);
                                    intent.putExtra("FoodID",food_key);
                                    startActivity(intent);
                                }
                            });

                        }

                        @NonNull
                        @Override
                        public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                        {
                            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_of_food,viewGroup,false);
                            FoodViewHolder holder = new FoodViewHolder(view);
                            return holder;


                        }
                    };

            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }




//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//              @Override
//              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                  for(DataSnapshot piusfood: dataSnapshot.getChildren()){
//                      String foodName = (String) dataSnapshot.child("Name").getValue();
//                      String foodDescription = (String) dataSnapshot.child("Description").getValue();
//                      String foodPrice = (String) dataSnapshot.child("Price").getValue();
//                      String foodImage = (String) dataSnapshot.child("Image").getValue();
//
//                      FoodName.setText(foodName);
//                      FoodDescription.setText(foodDescription);
//                      FoodPrice.setText(foodPrice);
//                      Picasso.with(FoodList.this).load(foodImage).into(FoodImage);
//
//
//
//
//                  recyclerView.setAdapter(adapter);
//                  adapter.startListening();
//
//              }
//
//              }
//              @Override
//              public void onCancelled(@NonNull DatabaseError databaseError) {
//
//              }
//          });
//
//
//
//
//
//




    }


