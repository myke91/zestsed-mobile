package com.zestsed.mobile.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zestsed.mobile.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        String message = getIntent().getStringExtra("notificationMessage");

        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationActivity.this);
        builder.setMessage(message);
        builder.setTitle(R.string.app_name);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       NotificationActivity.this.finish();
                    }
                }
        );
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
