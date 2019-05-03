package com.example.food.OtherActivitiesClass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.Prevalent.Prevalent;
import com.example.food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button signup,signin;
    private TextView signInWithPhoneNumber;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myDatabase;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signin = (Button) findViewById(R.id.sign_in_button);
        signup = (Button) findViewById(R.id.sign_up_button);
        //signInWithPhoneNumber = (TextView) findViewById(R.id.phoneNumber);

        firebaseAuth =FirebaseAuth.getInstance();
        myDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        Paper.init(this);

        progressDialog = new ProgressDialog(this);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(loginIntent);

            }
        });

//        signInWithPhoneNumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                phoneRegistrationActivity();
//            }
//        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        String UserEmail = Paper.book().read(Prevalent.UserEmailKey);
        String UserPassword = Paper.book().read(Prevalent.UserPasswordKey);

        if(UserEmail != null && UserPassword != null)
        {
            if(!TextUtils.isEmpty(UserEmail) && !TextUtils.isEmpty(UserPassword))
            {
                progressDialog.setTitle("Welcome Back");
                progressDialog.setMessage("Pleas Wait...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);


                AllowUserUserAccess(UserEmail,UserPassword);
            }

        }
    }

    private void phoneRegistrationActivity()
    {
        Intent intent = new Intent(MainActivity.this,RegisterWithPhone.class);
        startActivity(intent);
    }

    private void AllowUserUserAccess(final String userEmail, String userPassword)
    {
        firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {

                            final String user_id = firebaseAuth.getCurrentUser().getUid();
                            myDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id))
                                    {
                                        progressDialog.dismiss();

                                        Intent homeIntent = new Intent(MainActivity.this,HomeActivity.class);
                                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        Prevalent.currentOnLineUser = user_id;
                                        startActivity(homeIntent);
                                        finish();

                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this,"Welcome Back Red Dot Restaurant",Toast.LENGTH_SHORT).show();
                                        Toast.makeText(MainActivity.this," Welcome Back " + userEmail,Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                        else
                        {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(MainActivity.this,"An Error Occurred",Toast.LENGTH_LONG).show();

                            Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                        }
                    }
                });

    }


}