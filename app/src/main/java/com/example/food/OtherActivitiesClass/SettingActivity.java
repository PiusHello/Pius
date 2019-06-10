package com.example.food.OtherActivitiesClass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.Prevalent.Prevalent;
import com.example.food.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private TextView closeSetting, updateSetting, profileChange;
    private EditText phone, fullName, location;
    private CircleImageView profileImage;

    String currentUserID, currentUserEmail;

    final static int PICK_FROMGALLERY = 1;
    private Uri ImageUri;
    private String myUri = null;
    private String checker = null;


    private StorageReference myStorage;
    private DatabaseReference myDatabaseRef;
    private StorageTask myUploadTask;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        myStorage = FirebaseStorage.getInstance().getReference().child("Profile_Pictures");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        user_id = currentUser.getUid();

        profileImage = (CircleImageView) findViewById(R.id.setting_profile_image);
        closeSetting = (TextView) findViewById(R.id.close_setting_button);
        profileChange = (TextView) findViewById(R.id.profile_change);
        updateSetting = (TextView) findViewById(R.id.update_setting_button);
        phone = (EditText) findViewById(R.id.setting_phone);
        fullName = (EditText) findViewById(R.id.setting_full_name);
        location = (EditText) findViewById(R.id.setting_location);


        displayUserInfo(profileImage, phone, location, fullName);

        closeSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checker.equals("clicked")) {
                    updateUser();
                } else {
                    saveUserInfo();

                }
            }
        });

        profileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity(ImageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROMGALLERY && requestCode == RESULT_OK && data != null) {

            ImageUri = data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                ImageUri = result.getUri();
                profileImage.setImageURI(ImageUri);
            }
        } else {
            Toast.makeText(SettingActivity.this, "There Was An Error While Selecting Profile Image", Toast.LENGTH_LONG).show();

            startActivity(new Intent(SettingActivity.this, SettingActivity.class));
            finish();
        }


    }

    private void updateUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("Full_Name", fullName.getText().toString());
        userMap.put("Phone", phone.getText().toString());
        userMap.put("Location", fullName.getText().toString());
        ref.child(user_id).updateChildren(userMap);


        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
        Toast.makeText(SettingActivity.this, "Profile Was Updated Successfully", Toast.LENGTH_SHORT);
        finish();
    }

    private void saveUserInfo() {
        if (TextUtils.isEmpty(phone.getText().toString())) {
            Toast.makeText(SettingActivity.this, "Phone Is Mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullName.getText().toString())) {
            Toast.makeText(SettingActivity.this, "FullName Is Mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(location.getText().toString())) {
            Toast.makeText(SettingActivity.this, "Provide Your Location", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("checked")) {
            updateImage();
        }
    }

    private void updateImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please Wait While While Your Profile Is Being Updated");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (ImageUri != null) {
            final StorageReference fileRef = myStorage
                    .child(user_id);
            myUploadTask = fileRef.putFile(ImageUri);
            myUploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                myUri = downloadUrl.toString();
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("Full_Name", fullName.getText().toString());
                                userMap.put("Phone", phone.getText().toString());
                                userMap.put("Location", fullName.getText().toString());
                                userMap.put("Image", myUri);
                                ref.child(user_id).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                                Toast.makeText(SettingActivity.this, "Profile Was Updated Successfully", Toast.LENGTH_SHORT);
                                finish();
                            } else {
                                Toast.makeText(SettingActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayUserInfo(final CircleImageView profileImage, final EditText phone, final EditText location, final EditText fullName) {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("Image").exists()) {
                        String image = dataSnapshot.child("Image").getValue().toString();
                        String name = dataSnapshot.child("Username").getValue().toString();
                        String phoneNumber = dataSnapshot.child("Phone").getValue().toString();
                        String Location = dataSnapshot.child("Location").getValue().toString();

                        Picasso.get().load(image).into(profileImage);
                        fullName.setText(name);
                        phone.setText(phoneNumber);
                        location.setText(Location);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}