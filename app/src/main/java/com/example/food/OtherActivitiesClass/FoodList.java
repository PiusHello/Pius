package com.example.food.OtherActivitiesClass;

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

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.food.Model.Food;
import com.example.food.R;
import com.example.food.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference mDatabase,userData;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    String categoryID;
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

        mDatabase = FirebaseDatabase.getInstance().getReference("FoodList");
         query = mDatabase.orderByChild("MenuID").equalTo(categoryID);

        foodkey = getIntent().getStringExtra("FoodID");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("FoodList");



        FoodName = (TextView) findViewById(R.id.fn);
        FoodDescription = (TextView) findViewById(R.id.fd);
        FoodPrice = (TextView) findViewById(R.id.fp);
        FoodImage = (ImageView) findViewById(R.id.fimage);



        recyclerView = (RecyclerView) findViewById(R.id.food_recycler);
        recyclerView.setHasFixedSize(true);

        //layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

    }


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
                                            holder.foodPrice.setText(" Price " + model.getPrice());
                                            Picasso.get().load(model.getImage()).into(holder.foodImage);


                            //Sends the user to the FoodDetails when any food item is clicked under an
                            //category.
                            //send food image to be added to cart
                            final  String url = model.getImage();
                            final String food_key =getRef(position).getKey().toString();
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent = new Intent(FoodList.this,SelectedFoodDetails.class);
                                    intent.putExtra("FoodID",food_key);
                                    intent.putExtra("image",url);
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

    }


