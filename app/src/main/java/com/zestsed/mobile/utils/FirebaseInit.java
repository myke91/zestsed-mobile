package com.zestsed.mobile.utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mdugah on 3/14/2017.
 */

public class FirebaseInit extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
