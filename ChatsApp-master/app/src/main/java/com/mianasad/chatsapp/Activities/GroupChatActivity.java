package com.mianasad.chatsapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mianasad.chatsapp.Adapters.GroupMessagesAdapter;
import com.mianasad.chatsapp.Models.Message;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {

    com.mianasad.chatsapp.databinding.ActivityGroupChatBinding binding;
    GroupMessagesAdapter adapter;
    ArrayList<Message> messages;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String senderUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.mianasad.chatsapp.databinding.ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Sarthak Start
        //camera button for sending images or gallery images
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(GroupChatActivity.this);
            }
        });

//  Set Default ToolBar
        getSupportActionBar().setTitle("Public Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//  End
        senderUid = FirebaseAuth.getInstance().getUid();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading image...");
        dialog.setCancelable(false);

        messages = new ArrayList<>();
        adapter = new GroupMessagesAdapter(this, messages);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        database.getReference().child("public")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
//                      Start Ananthu
                        binding.recyclerView.smoothScrollToPosition(binding.recyclerView.getAdapter().getItemCount());
//                      End
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//  Send Button is Clicked then we Have to send Message
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt = binding.messageBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageTxt, senderUid, date.getTime());
                binding.messageBox.setText("");

//  Store in Database
                database.getReference().child("public")
                        .push()
                        .setValue(message);
            }
        });

//  Start ---> For Sending Images, Video To the another person(Opponent)
        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 25);
            }
        });
    }
//  End

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 25) {
            if(data != null) {
                if(data.getData() != null) {
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();

                                        String messageTxt = binding.messageBox.getText().toString();

                                        Date date = new Date();
                                        Message message = new Message(messageTxt, senderUid, date.getTime());
                                        message.setMessage("Photo");
                                        message.setImageUrl(filePath);
                                        binding.messageBox.setText("");
//                                      Here we want to add/push in our "public node"
                                        database.getReference().child("public")
                                                .push()
                                                .setValue(message);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }

        //Start sarthak
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri selectedImage;
                selectedImage = result.getUri();
                Calendar calendar = Calendar.getInstance();
                StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                dialog.show();
                reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();

                                    String messageTxt = binding.messageBox.getText().toString();

                                    Date date = new Date();
                                    Message message = new Message(messageTxt, senderUid, date.getTime());
                                    message.setMessage("Photo");
                                    message.setImageUrl(filePath);
                                    binding.messageBox.setText("");
//                                      Here we want to add/push in our "public node"
                                    database.getReference().child("public")
                                            .push()
                                            .setValue(message);


                                }
                            });
                        }
                    }
                });

            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e = result.getError();
                System.out.println(e);
            }
        }
        //End Sarthak
    }
//  End

//  If Back Button is Preesed/Clicked then, Back to the Main Activity.
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}