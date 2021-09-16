package com.mianasad.chatsapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;
import com.mianasad.chatsapp.R;
import com.mianasad.chatsapp.databinding.ActivityPhoneNumberBinding;

public class PhoneNumberActivity extends AppCompatActivity {
    ActivityPhoneNumberBinding binding;


    private Spinner spinner; //Sarthak


    private CountryCodePicker ccp;
    AlertDialog.Builder builder;


FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());


        //Start Sarthak
        ccp = binding.picker;
        setContentView(binding.getRoot());
        //End Sarthak


        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            Intent intent = new Intent(PhoneNumberActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        getSupportActionBar().hide();
        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Start Sarthak
                String number = binding.phoneBox.getText().toString();
                String code = ccp.getSelectedCountryCodeWithPlus();
                String phoneNumber = code+" "+number;
                System.out.println(phoneNumber);
                //End Sarthak


                //Ananthu start
                builder = new AlertDialog.Builder(PhoneNumberActivity.this);
                builder.setMessage("We will be verifying the phone number:\n\n"+phoneNumber+"\n\nIs it OK, or would you like to edit the number?\n")
                        .setIcon(R.drawable.verified)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("VERIFICATION");
                alert.show();
                //Ananthu end

                
            }
        });
    }
}