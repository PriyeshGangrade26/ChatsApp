package com.mianasad.chatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mianasad.chatsapp.Activities.MainActivity;
import com.mianasad.chatsapp.Models.User;
import com.mianasad.chatsapp.databinding.ActivityUserSettingsBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class UserSettingsActivity extends AppCompatActivity {
    ActivityUserSettingsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();


        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettingsActivity.this , MainActivity.class);
                startActivity(intent);
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = binding.etStatus.getText().toString();
                String username = binding.etUserName.getText().toString();

                HashMap<String , Object> obj = new HashMap<>();
                obj.put("name" , username);
                obj.put("status" , status);

                database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                        .updateChildren(obj);
                Toast.makeText(UserSettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfileImage())
                                .placeholder(R.drawable.avatar)
                                .into(binding.profileImage);

                        binding.etStatus.setText(user.getStatus());
                        binding.etUserName.setText(user.getName());

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,25);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null){
            Uri sFile = data.getData();
            binding.profileImage.setImageURI(sFile);
            final StorageReference reference = storage.getReference().child("profile_pictures")
                    .child(FirebaseAuth.getInstance().getUid());
            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                       database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                               .child("profileImage").setValue(uri.toString());
                            Toast.makeText(UserSettingsActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });

        }
    }
}