package com.example.food;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.Model.Food;
import com.example.food.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference mDatabase,userData;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    String categoryID;
   // TextView food_name,food_description,food_price;
    TextView FoodName,FoodPrice,FoodDescription;
    ImageView FoodImage;

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        categoryID = getIntent().getStringExtra("Category");

        //DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("FoodList");

        mDatabase = FirebaseDatabase.getInstance().getReference("FoodList");
        Query query = mDatabase.orderByChild("categoryName").equalTo("BreakFast");


        // mAuth = FirebaseAuth.getInstance();
        // currentUser = mAuth.getCurrentUser();

        FoodName = (TextView) findViewById(R.id.fn);
        FoodDescription = (TextView) findViewById(R.id.fd);
        FoodPrice = (TextView) findViewById(R.id.fd);
        FoodImage = (ImageView) findViewById(R.id.fimage);

        // food_name  = (TextView) findViewById(R.id.food_name);
        // food_description =(TextView) findViewById(R.id.food_description);
        // food_price = (TextView) findViewById(R.id.food_price);
        // food_image = (ImageView) findViewById(R.id.food_image);


        recyclerView = (RecyclerView) findViewById(R.id.food_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String foodName = (String) dataSnapshot.child("Name").getValue();
                String foodDescription = (String) dataSnapshot.child("Description").getValue();
                String foodPrice = (String) dataSnapshot.child("Price").getValue();
                String foodImage = (String) dataSnapshot.child("Image").getValue();

                FoodName.setText(foodName);
                FoodDescription.setText(foodDescription);
                FoodPrice.setText(foodPrice);
                Picasso.with(FoodList.this).load(foodImage).into(FoodImage);

                recyclerView.setAdapter(adapter);
                adapter.startListening();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




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
    }

