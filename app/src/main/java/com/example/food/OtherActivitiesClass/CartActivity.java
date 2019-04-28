package com.example.food.OtherActivitiesClass;

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
import com.squareup.picasso.Picasso;

import java.security.SecureRandom;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button proceedButton;
    private TextView totalAmount;
    private ImageView cartFoodPicture;
    DatabaseReference cartList;
    ArrayList<Double> cartprice=new ArrayList();
    ArrayList<Integer> cartquantity=new ArrayList();
    Button checkout;
    private TextView cartStatus;

    private String user_id;

    private double OverRawTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        checkout=findViewById(R.id.checkout);
        recyclerView =findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cartStatus = (TextView) findViewById(R.id.CurrentCartStatus);
checkout();
        //proceedButton = (Button) findViewById(R.id.proceedButton);
        totalAmount = (TextView) findViewById(R.id.totalprice);
        cartFoodPicture = (ImageView)findViewById(R.id.cartFoodImage);

        //This will execute when the Proceed button in the cart activity is Clicked.



        cartList = FirebaseDatabase.getInstance().getReference("Cart List");
    }

    @Override
    protected void onStart() {
        super.onStart();



        totalAmount.setText("Total Price : " + String.valueOf(OverRawTotalPrice));
        //final DatabaseReference cartList = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartList.child("Users View")
                        .child(user_id).child("Food_List"),Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
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
                    public void onClick(View v)
                    {
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
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //when a user select edit cart from the option he / she will be
                                //able to increase or decrease the cart quantity
                                if(which == 0)
                                {
                                    //Will direct the user from the cart activity to SelectedFoodActivity.class
                                    //java file, using the id of that food
                                    Intent intent = new Intent(CartActivity.this,SelectedFoodDetails.class);
                                    intent.putExtra("FoodID",model.getFoodID());
                                    startActivity(intent);
                                    finish();

                                }
                                if(which==1)
                                {
                                    //Again when a user select Delete from the option, will be able to delete
                                    //item from the cart.
                                    cartList.child("Users View")//At the database level from the CartList
                                            .child(user_id)
                                            .child("Food_List")//From the child of Cart List will be updated
                                            .child(model.getFoodID())
                                            .removeValue()//Method for removing the item from the cart
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    //If the task was successful print a Toast message to the user
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this,"One Item has Been Remove From The Cart",Toast.LENGTH_LONG).show();
                                                    //After the which, user will be directed to the CartActivity class
                                                        Intent intent = new Intent(CartActivity.this,CartActivity.class);
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
                 View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_list_layout,viewGroup,false);
                 CartViewHolder holder = new CartViewHolder(view);
                 return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public void checkout(){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        user_id = firebaseAuth.getUid();
        final String email=firebaseAuth.getCurrentUser().getEmail();
        cartList = FirebaseDatabase.getInstance().getReference("Cart List");
        cartList.child("Users View")
                .child(user_id).child("Food_List").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float total=0;
                //clear the arraylist
                cartprice.clear();
                cartquantity.clear();




                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cart mycart = snapshot.getValue(Cart.class);
                    cartquantity.add(Integer.valueOf(mycart.getQuantity()));
                    cartprice.add(Double.valueOf(mycart.getPrice()));

                }
                //A for loop that will increase the quantity and also the price as well
                //That is,if quantity increase, the number wil multiplied by the item price
                for(int i=0;i<cartquantity.size();i++){
                    total+=cartprice.get(i)*cartquantity.get(i);

                }
                //Print the total price for user to view.
                checkout.setText("total: "+total+" Checkout");
                final float finalTotal = total;
                //Here,users will be sent to the payment section of the app.

                checkout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        //An Instance from the Rave class
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
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                //if payment wasnt successfull show error page
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {

                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    public  String userNameFromEmail(String email)
    {
        if(email.contains("@"))
        {
            return email.split("@")[0];
        }
        else
        {
            return email;
        }
    }

}
