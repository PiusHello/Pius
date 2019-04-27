package com.example.food.OtherActivitiesClass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName,userEmail,userPassword;
    private Button signUp;
    private TextView hasAccount;


    private FirebaseAuth mAuth;
    private DatabaseReference myDatabase;

    private ProgressDialog loadingBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = (EditText) findViewById(R.id.username);
        userEmail = (EditText) findViewById(R.id.user_email);
        userPassword = (EditText) findViewById(R.id.user_password);
        signUp = (Button) findViewById(R.id.sign_up_button);
        hasAccount = (TextView) findViewById(R.id.already_have_account);

        mAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        loadingBox = new ProgressDialog(this);

        hasAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToLoginActivity();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateNewAccount();
            }
        });
    }

    private void GoToLoginActivity()
    {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private void CreateNewAccount()
    {
        final String name = userName.getText().toString().trim();
        final String email = userEmail.getText().toString().trim();
        final String password = userPassword.getText().toString().trim();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(RegisterActivity.this,"Username is Required",Toast.LENGTH_SHORT).show();
        }

        else if(name.length()<3)
        {
            Toast.makeText(RegisterActivity.this,"Username Must Be More Than 3 Characters",Toast.LENGTH_LONG).show();
        }

        else if(name.contains("¬!£$%^&&*()_+=-<>:@~/.,0123456789/*-+"))
        {
            Toast.makeText(RegisterActivity.this,"Username Is Invalid, Remove Spacial Characters ",Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(email))
        {
            Toast.makeText(RegisterActivity.this,"Email Is Required",Toast.LENGTH_SHORT).show();

        }

        else if(!email.contains("@"))
        {
            Toast.makeText(RegisterActivity.this,"Email Is Invalid, Add The @ Sign",Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(RegisterActivity.this,"Password Is Required",Toast.LENGTH_SHORT).show();
        }

        else if(password.length()<6)
        {
            Toast.makeText(RegisterActivity.this,"Minimum Password Required Is 6 Characters",Toast.LENGTH_SHORT).show();
        }

        else
        {

            loadingBox.setTitle("Create Account");
            loadingBox.setMessage("Pleas Wait While We Create Your Account");
            loadingBox.show();
            loadingBox.setCanceledOnTouchOutside(false);

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {



                            if(task.isSuccessful())
                            {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUser = myDatabase.child(user_id);

                                currentUser.child("Username").setValue(name);
                                currentUser.child("Email").setValue(email);
                               // currentUser.child("Password").setValue(password);

                                loadingBox.dismiss();
                                Toast.makeText(RegisterActivity.this,"Registration Was Successful",Toast.LENGTH_SHORT).show();

                                loadingBox.dismiss();
                                Intent homeIntent = new Intent(RegisterActivity.this,HomeActivity.class);
                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(homeIntent);
                                finish();

                            }


                            else
                            {
                                loadingBox.dismiss();
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this," Error Occurred " + errorMessage ,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }
}