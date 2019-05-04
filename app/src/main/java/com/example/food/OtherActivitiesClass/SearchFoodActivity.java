package com.example.food.OtherActivitiesClass;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.food.Model.Food;

import com.example.food.R;
import com.example.food.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class SearchFoodActivity extends AppCompatActivity {

    private EditText searchFoodName;
    private Button searchFoodButton;
    private RecyclerView searchFoodRecyclerView;
    private String SearchInput;
    DatabaseReference mDatabase;
    RecyclerView.LayoutManager layoutManager;
    Query query;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        searchFoodName = (EditText) findViewById(R.id.search_food_name);
        searchFoodButton = (Button) findViewById(R.id.searchButton);
        searchFoodRecyclerView = (RecyclerView) findViewById(R.id.searchFoodList);

         mDatabase = FirebaseDatabase.getInstance().getReference().child("FoodList");



         searchFoodRecyclerView.setHasFixedSize(true);

         layoutManager = new LinearLayoutManager(this);
         searchFoodRecyclerView.setLayoutManager(layoutManager);
         //searchFoodRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
         query = mDatabase.orderByChild("Name");

        searchFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchInput = searchFoodName.getText().toString();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FoodList");
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query.startAt(SearchInput).endAt(SearchInput),Food.class)
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
                                Intent intent = new Intent(SearchFoodActivity.this, SelectedFoodDetails.class);
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

        searchFoodRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}
