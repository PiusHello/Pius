package com.example.food.OtherActivitiesClass;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.food.Model.FoodCategory;
import com.example.food.Prevalent.Prevalent;
import com.example.food.R;
import com.example.food.ViewHolder.FoodCategoryList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class

HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String currentUserID,currentUserEmail;

    public NotificationBadge badge;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);

       //recyclerView.setLayoutManager(new GridLayoutManager(this,2));
       layoutManager = new LinearLayoutManager(this);
       recyclerView.setLayoutManager(layoutManager);

        Paper.init(this);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        currentUserEmail = firebaseAuth.getCurrentUser().getEmail();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Category");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(HomeActivity.this,CartActivity.class);
//                startActivity(intent);
//
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        String username = userNameFromEmail(currentUserEmail);

        View headerView = navigationView.getHeaderView(0);
        final TextView profileName = headerView.findViewById(R.id.user_profile_name);
        final CircleImageView profileImage = headerView.findViewById(R.id.profile_image);

        profileName.setText(username);
        Picasso.get().load(Prevalent.UserEmailKey).placeholder(R.drawable.background).into(profileImage);

        databaseReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String fullname = dataSnapshot.child("Username").getValue().toString();
                    Log.v("username",""+fullname+"");
                    // profileName.setText(fullname);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<FoodCategory>options=
                new FirebaseRecyclerOptions.Builder<FoodCategory>()
                .setQuery(databaseReference,FoodCategory.class)
                .build();

        FirebaseRecyclerAdapter<FoodCategory, FoodCategoryList> adapter=
                new FirebaseRecyclerAdapter<FoodCategory, FoodCategoryList>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FoodCategoryList holder, int position, @NonNull FoodCategory model)
                    {
                          holder.FoodCategoryName.setText(model.getCategoryName());
                          holder.DeliveryDays.setText(model.getDeliveryDays());
                          holder.DeliveryHours.setText(model.getDeliveryHours());
                         // holder.FoodCategoryName.setText(model.getCategoryDescription());
                        Picasso.get().load(model.getCategoryImage()).into(holder.FoodCategoryImage);

                        final String food_key =getRef(position).getKey().toString();
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this,FoodList.class);
                                intent.putExtra("CategoryID",food_key);
                                startActivity(intent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FoodCategoryList onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.food_category_list,viewGroup,false);
                        FoodCategoryList holder = new FoodCategoryList(view);
                        return holder;


                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        View view = menu.findItem(R.id.cart_menu).getActionView();
       // badge = (NotificationBadge) view.findViewById(R.id.badge);
      //  NotificationBadge  badge = (NotificationBadge) view.findViewById(R.id.badge);
        //updateCartCount();
        return true;
    }

    //This method will be called when the cart is been updated
//    private void updateCartCount()
//    {
//        if(badge == null) return;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run()
//            {
//                if(Common.cartRepository.countCartItems() == 0)
//                    badge.setVisibility(View.INVISIBLE);
//
//                else
//                {
//                    badge.setVisibility(View.VISIBLE);
//                    badge.setText(String.valueOf(Common.cartRepository.countCartItem()));
//                }
//            }
//
//
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart_menu) {
            Intent intent = new Intent(HomeActivity.this,CartActivity.class);
            startActivity(intent);
//            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();




        if (id == R.id.nav_cart)
        {
            Intent intent = new Intent(HomeActivity.this,CartActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_order)
        {

        }

        else if (id == R.id.nav_setting)
        {
             Intent settingIntent = new Intent(HomeActivity.this,SettingActivity.class);
             startActivity(settingIntent );
        }

        else if (id == R.id.nav_about)
        {
            Intent aboutIntent = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(aboutIntent );
        }

        else if (id == R.id.nav_logout)
        {
            Paper.book().destroy();
            Intent loginIntent = new Intent(HomeActivity.this,LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        }

        else if (id == R.id.nav_fb)
        {

        }

        else if (id == R.id.nav_twitter)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Adding onResume method


    @Override
    protected void onPostResume() {
        super.onPostResume();
       // updateCartCount();
    }
}
