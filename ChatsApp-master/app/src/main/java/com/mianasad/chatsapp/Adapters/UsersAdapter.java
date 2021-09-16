package com.mianasad.chatsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mianasad.chatsapp.Activities.ChatActivity;
import com.mianasad.chatsapp.R;
import com.mianasad.chatsapp.Models.User;
import com.mianasad.chatsapp.databinding.RowConversationBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersVewHolder> {
    Context context;
    ArrayList<User> users;
    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersVewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);
        return new UsersVewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersVewHolder holder, int position) {
        User user = users.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + user.getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                            holder.binding.msgTime.setText(dateFormat.format(new Date(time)));
                            if(lastMsg.length() > 40)
                                lastMsg = lastMsg.substring(0, 40) + "...";
                            holder.binding.lastMsg.setText(lastMsg);
                        } else {
                            holder.binding.lastMsg.setText("Tap To Chat");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        String userDisplayName;
        if(user.getName().length() > 20)
            userDisplayName = user.getName().substring(0,20) + "...";
        else
            userDisplayName = user.getName();
        holder.binding.username.setText(userDisplayName);

        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("image", user.getProfileImage());
                intent.putExtra("uid", user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class  UsersVewHolder extends RecyclerView.ViewHolder {
        RowConversationBinding binding;

        public UsersVewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
