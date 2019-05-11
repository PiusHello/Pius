package com.example.food.OtherActivitiesClass;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.food.Model.Cart;
import com.example.food.R;
import com.example.food.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button proceedButton;
    private TextView totalAmount;
    private ImageView cartFoodPicture;
    String json;
    DatabaseReference cartList;
    ArrayList<Double> cartprice = new ArrayList();
    ArrayList<Integer> cartquantity = new ArrayList();
    final ArrayList<String> items = new ArrayList<>();
    FirebaseAuth auth;
    Button checkout;
    private TextView cartStatus;
    final ArrayList<String> location = new ArrayList<>();
    final ArrayList<String> phoneNumber = new ArrayList<>();
    private String user_id;

    private double OverRawTotalPrice = 0;
    EditText user_location;
    EditText user_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        checkout = findViewById(R.id.checkout);
        recyclerView = findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cartStatus = (TextView) findViewById(R.id.CurrentCartStatus);

        checkout();
        //proceedButton = (Button) findViewById(R.id.proceedButton);
        totalAmount = (TextView) findViewById(R.id.totalprice);
        cartFoodPicture = (ImageView) findViewById(R.id.cartFoodImage);

        //This will execute when the Proceed button in the cart activity is Clicked.


        cartList = FirebaseDatabase.getInstance().getReference("CartList");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (cartList == null){
            cartStatus.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            cartStatus.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        totalAmount.setText("Total Price : " + String.valueOf(OverRawTotalPrice));
                final FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartList.child("UsersView")
                                .child(user_id).child("FoodList"), Cart.class)
                        .build();

     final FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                holder.foodName.setText(" Food : " + model.getName());
                holder.foodQuantity.setText(" Quantity : " + model.getQuantity());
                holder.foodPrice.setText(" Price : " + model.getPrice());
                holder.date.setText(" Date :" + model.getDate());
                holder.time.setText(" Time : " + model.getTime());
                Picasso.get().load(model.getImage()).into(holder.image);



                //users will be able to view the option on each every cart
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Array list of options
                        CharSequence[] options;
                        options = new CharSequence[]
                                {
                                        //Lists of items in the option menu
                                        "Edit Cart",
                                        "Delete Cart Item"
                                };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //when a user select edit cart from the option he / she will be
                                //able to increase or decrease the cart quantity
                                if (which == 0) {
                                    //Will direct the user from the cart activity to SelectedFoodActivity.class
                                    //java file, using the id of that food
                                    Intent intent = new Intent(CartActivity.this, SelectedFoodDetails.class);
                                    intent.putExtra("FoodID", model.getFoodID());
                                    startActivity(intent);
                                    finish();

                                }
                                if (which == 1) {
                                    //Again when a user select Delete from the option, will be able to delete
                                    //item from the cart.
                                    cartList.child("UsersView")//At the database level from the CartList
                                            .child(user_id)
                                            .child("FoodList")//From the child of Cart List will be updated
                                            .child(model.getFoodID())
                                            .removeValue()//Method for removing the item from the cart
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //If the task was successful print a Toast message to the user
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(CartActivity.this, "One Item has Been Remove From The Cart", Toast.LENGTH_LONG).show();
                                                        //After the which, user will be directed to the CartActivity class
                                                        Intent intent = new Intent(CartActivity.this, CartActivity.class);
                                                        startActivity(intent);
                                                        finish();
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
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_list_layout, viewGroup, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public void checkout() {


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getUid();
        final String email = firebaseAuth.getCurrentUser().getEmail();
        cartList = FirebaseDatabase.getInstance().getReference("CartList");
        cartList.child("UsersView")
                .child(user_id).child("FoodList").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                float total = 0;
                //clear the arraylist
                cartprice.clear();
                cartquantity.clear();
                items.clear();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Cart mycart = snapshot.getValue(Cart.class);
                    items.add(mycart.getName() + ":" + " Qty " + mycart.getQuantity());

                    json = new Gson().toJson(mycart);
                    cartquantity.add(Integer.valueOf(mycart.getQuantity()));
                    cartprice.add(Double.valueOf(mycart.getPrice()));


                }

                //A for loop that will increase the quantity and also the price as well
                //That is,if quantity increase, the number wil multiplied by the item price
                for (int i = 0; i < cartquantity.size(); i++) {

                    total += cartprice.get(i) * cartquantity.get(i);

                }
                //Print the total price for user to view.
                checkout.setText("total: " + total + " Checkout");
                final float finalTotal = total;
                //Here,users will be sent to the payment section of the app.

                checkout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        View view = LayoutInflater.from(CartActivity.this).inflate(R.layout.small_dialog_for_order_details,null);

                          user_location = (EditText) findViewById(R.id.users_location);
                          user_phone = (EditText) findViewById(R.id.users_phone);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setView(view)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

//                                String area = user_location.getText().toString().trim();
//                                String phone = user_phone.getText().toString().trim();
//
//                                 if (area.isEmpty())
//                                 {
//                                    Toast.makeText(CartActivity.this, "Your location is mandatory", Toast.LENGTH_SHORT).show();
//                                 }
//
//                                 else if (phone.isEmpty()) {
//                                    Toast.makeText(CartActivity.this, "Your phone number is mandatory", Toast.LENGTH_SHORT).show();
//                                }
//


//                                location.clear();
//                                location.add(user_location.getText().toString());
//
//                                phoneNumber.clear();
//                                phoneNumber.add(user_phone.getText().toString());


                                        new RavePayManager(CartActivity.this).setAmount(finalTotal)
                                                .setCountry("GH")
                                                .setCurrency("GHS")
                                                .setEmail(email)
                                                .setfName(userNameFromEmail(email))
                                                //.setlName("billa")
                                                //.setNarration("")

                                                //replace with your public key from the flutterwave dashboard
                                                .setPublicKey("FLWPUBK-bfe00f7d1b2c64566bbea939c946063d-X")
                                                //replace with your ecncryption key from the flutterwave dashboard
                                                .setEncryptionKey("75d50f227fec111a9adbf45d")
                                                //here a random string is generated for txref
                                                .setTxRef(generateTXREF())
                                                .acceptAccountPayments(true)
                                                .acceptCardPayments(true)
                                                .acceptMpesaPayments(false)
                                                .acceptAchPayments(false)
                                                .acceptGHMobileMoneyPayments(true)
                                                .acceptUgMobileMoneyPayments(false)
                                                .onStagingEnv(true)
                                                .allowSaveCardFeature(true)
                                                //.setMeta(List<Meta>)
                                                .withTheme(R.style.AppTheme)
                                                .isPreAuth(false)

                                                //  .setSubAccounts(List<SubAccount>)
                                                //.shouldDisplayFee(true)
                                                .initialize();
                                    }
                                });

// Set up the input






// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

// Set up the buttons
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.show();
                            }
                        });

                        builder.show();

                        //An Instance from the Rave class

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {

                //if payment successfull show a success page containing the order details
                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();

                confirmedOrder();

                auth = FirebaseAuth.getInstance();
                String email = auth.getCurrentUser().getEmail();
                ArrayList<String> email_data = new ArrayList<>();
                email_data.clear();
                email_data.add(email);
                final RequestQueue queue = Volley.newRequestQueue(CartActivity.this);
                final String url = "https://imartgh.com/food/v1/sendmail";
                queue.start();

                Map<String, ArrayList<String>> map = new HashMap<>();
                Map<String, EditText> map1 = new HashMap<>();
                map.put("param", items);
                map1.put("location",user_location);
                map.put("items", items);
                map.put("email", email_data);


                Log.v("tested", map.toString());
                JsonObjectRequest jsObjRequest = new
                        JsonObjectRequest(Request.Method.POST,
                        url,
                        new JSONObject(map),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(final JSONObject response) {
                                Log.v("tested_response", response.toString());


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

                queue.add(jsObjRequest);

            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                //if payment wasnt successfull show error page
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {

                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void confirmedOrder()
    {
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate =new SimpleDateFormat("MM dd, yyyy");
        String saveCurrentDate = currentDate.format(calendarForDate.getTime());

        SimpleDateFormat currentTime =new SimpleDateFormat("HH:mm:ss a");
        String saveCurrentTime = currentTime.format(calendarForDate.getTime());

        final DatabaseReference cartList = FirebaseDatabase.getInstance().getReference().child("Orders");

        final HashMap<String , Object> orderMap = new HashMap<>();
        orderMap.put("Items",items);
        orderMap.put("Quantity",cartquantity);
        orderMap.put("Price",cartprice);
        orderMap.put("Date",saveCurrentDate);
        orderMap.put("Time",saveCurrentTime);
        orderMap.put("Phone",user_phone);
        orderMap.put("Location",user_location);
        orderMap.put("OrderStatus","Not Delived");


        cartList.child("UsersView").child(user_id).child("FoodList")
                .updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    cartList.child(user_id).child("FoodList")
                            .updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if(task.isSuccessful())
                            {

                                FirebaseDatabase.getInstance().getReference().child("CartList")
                                        .child("UsersView")
                                        .child(user_id)
                                        .removeValue();
                                cartState();


                                Toast.makeText(CartActivity.this,"Your new order has been placed",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }

            }
        });


    }

    private void cartState()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(CartActivity.this);
        builder1.setTitle("Cart State");

        final TextView textView = new TextView(CartActivity.this);
        textView.setText("Your cart is empty");
        builder1.setView(textView);
    }


    // this function will generate a random transaction reference
    public static String generateTXREF() {
        String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
        SecureRandom RANDOM = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; ++i) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public String userNameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }


}