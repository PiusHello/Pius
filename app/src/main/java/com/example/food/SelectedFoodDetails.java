package com.example.food;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.food.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SelectedFoodDetails extends AppCompatActivity {

    TextView food_name,food_description,food_price;
    ImageView food_image;
    Button addToCartButton;
    ElegantNumberButton NumberButton;
    FloatingActionButton cart_number_button;
    String foodkey = null;

    DatabaseReference mDatabase,userData;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String foodPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_food_details);

        foodkey = getIntent().getStringExtra("FoodID");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("FoodList");


        food_name  = (TextView) findViewById(R.id.FoodName);
        food_description =(TextView) findViewById(R.id.FoodDescription);
        food_price = (TextView) findViewById(R.id.FoodPrice);
        food_image = (ImageView) findViewById(R.id.FoodImage);
        addToCartButton = (Button) findViewById(R.id.addToCart);
        NumberButton =(ElegantNumberButton) findViewById(R.id.NumberButton);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //userData = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getEmail());

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodToCart();
            }
        });

        mDatabase.child(foodkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String foodName = (String) dataSnapshot.child("Name").getValue();
                String foodDescription = (String) dataSnapshot.child("Description").getValue();
                 foodPrice = (String) dataSnapshot.child("Price").getValue();
                String foodImage = (String) dataSnapshot.child("Image").getValue();

                food_name.setText(foodName);
                food_description.setText(foodDescription);
                food_price.setText( " Price ¢ " + foodPrice);
                Picasso.get().load(foodImage).into(food_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addFoodToCart()
    {
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate =new SimpleDateFormat("MM dd, yyyy");
        String saveCurrentDate = currentDate.format(calendarForDate.getTime());

        SimpleDateFormat currentTime =new SimpleDateFormat("MM dd, yyyy");
        String saveCurrentTime = currentDate.format(calendarForDate.getTime());

       final DatabaseReference cartList = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> CartMap = new HashMap<>();
        CartMap.put("FoodID",foodkey);
        CartMap.put("Name",food_name.getText().toString());
        CartMap.put("Description",food_description.getText().toString());
        //dont do this.. because it will include the cedi sign and the string "Price". this will prevent you from doing arithemetic operations
       // CartMap.put("Price",food_price.getText().toString());


        //do this
        CartMap.put("Price",foodPrice);
        CartMap.put("Date",saveCurrentDate);
        CartMap.put("Time",saveCurrentTime);
       // CartMap.put("Image",food_image);
        CartMap.put("Quantity",NumberButton.getNumber());

        cartList.child("Users View").child(Prevalent.currentOnLineUser).child("Food_List")
                .child(foodkey).updateChildren(CartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    cartList.child("Admin View").child(Prevalent.currentOnLineUser).child("Food_List").child(foodkey)
                            .updateChildren(CartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(SelectedFoodDetails.this,"Food Has Being Added To Cart",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(SelectedFoodDetails.this,HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }

            }
        });
    }
}