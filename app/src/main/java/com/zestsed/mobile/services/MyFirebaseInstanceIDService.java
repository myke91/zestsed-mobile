package com.zestsed.mobile.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.zestsed.mobile.R;
import com.zestsed.mobile.data.Constants;
import com.zestsed.mobile.ui.LoginActivity;
import com.zestsed.mobile.ui.SignupActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Belal on 5/27/2016.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).edit().putString("token", token).apply();
        String email = getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).getString("email", "");
        if (!email.isEmpty()) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(this);
            JSONObject json = new JSONObject();
            try {
                json.put("token", token);
                json.put("email", email);
            } catch (JSONException e) {
                Log.d("ZestSed", e.getLocalizedMessage());
            }

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.BACKEND_BASE_URL + "/mobile/registerDevice", json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).edit().putBoolean("isTokenRegistered", true).apply();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            jsonRequest.setTag(TAG);
            mRequestQueue.add(jsonRequest);
        }
    }
}