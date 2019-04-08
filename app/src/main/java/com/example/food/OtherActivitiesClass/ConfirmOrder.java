package com.example.food.OtherActivitiesClass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.food.R;

public class ConfirmOrder extends AppCompatActivity {
private EditText yourLocation,phoneNumber;
private Button confirmButton;

//Receiving the total price on the ConfirmOrder.
    private String totalAmount = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);


        yourLocation = (EditText) findViewById(R.id.enterLocation);
        phoneNumber = (EditText) findViewById(R.id.phone);
        confirmButton = (Button) findViewById(R.id.confirmButton);
    }
}
