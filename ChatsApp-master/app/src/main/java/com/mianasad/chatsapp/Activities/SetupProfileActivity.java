package com.mianasad.chatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mianasad.chatsapp.Models.UpdatingProfileDialogue;
import com.mianasad.chatsapp.R;
import com.mianasad.chatsapp.Models.User;
import com.mianasad.chatsapp.databinding.ActivitySetupProfileBinding;
import com.theartofdev.edmodo.cropper.CropImage;

public class  SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImage;
  //  ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UpdatingProfileDialogue updatingProfileDialogue = new UpdatingProfileDialogue(SetupProfileActivity.this);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Start Sarthak
                CropImage.activity().start(SetupProfileActivity.this);
                //End Sarthak


            }
        });

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameBox.getText().toString();
                if(name.isEmpty()) {
                    binding.nameBox.setError("Please Enter Your Name",getDrawable(R.drawable.avatar));
                    return;
                }
               // dialog.show();
                updatingProfileDialogue.startProcessingDialog();
                if(selectedImage != null) {
                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        String uid = auth.getUid();
                                        String phone = auth.getCurrentUser().getPhoneNumber();
                                        String name = binding.nameBox.getText().toString();

                                        User user = new User(uid, name, phone, imageUrl);

                                        database.getReference()
                                                .child("users")
                                                .child(uid)
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //dialog.dismiss();
                                                        updatingProfileDialogue.dismissDialog();
                                                        Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                } else {
                    String uid = auth.getUid();
                    String phone = auth.getCurrentUser().getPhoneNumber();

                    User user = new User(uid, name, phone, "No Image");

                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                   // dialog.dismiss();
                                    updatingProfileDialogue.dismissDialog();
                                    Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Start sarthak
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                selectedImage = result.getUri();
                binding.imageView.setImageURI(selectedImage);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e = result.getError();
                System.out.println(e);
            }
        }
        //end sarthak


    }
}
