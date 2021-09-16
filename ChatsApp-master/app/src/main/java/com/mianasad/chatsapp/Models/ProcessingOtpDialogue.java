package com.mianasad.chatsapp.Models;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import com.mianasad.chatsapp.R;

public class ProcessingOtpDialogue {
    Activity activity;
    AlertDialog dialog;

    public ProcessingOtpDialogue(Activity myActivity){
        activity = myActivity;
    }

    public void startProcessingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_otp_dialoguebox, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
    public void dismissDialog(){
        dialog.dismiss();
    }
}
