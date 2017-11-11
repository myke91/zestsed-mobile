package com.zestsed.mobile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.zestsed.mobile.R;
import com.zestsed.mobile.data.Constants;
import com.zestsed.mobile.services.MyFirebaseInstanceIDService;
import com.zestsed.mobile.services.MyFirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //  private UserLoginTask mAuthTask = null;

    // UI references.
    private TextInputEditText mEmailView;
    private TextInputEditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private static final String TAG = "LOGIN_ACTIVITY";

    String url = Constants.BACKEND_BASE_URL + "/mobile/login";
    RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mRequestQueue = Volley.newRequestQueue(this);

        if (getIntent().getExtras() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Your registration is saved succesfully and pending approval. \n You will receive a mail and notification after approval");
            builder.setTitle(R.string.app_name);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        if (getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).getBoolean("authentication", false)) {
            startActivity(new Intent(LoginActivity.this, ContributionsTabActivity.class));
            finish();
        }


        // Set up the login form.
        mEmailView = (TextInputEditText) findViewById(R.id.email);

        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    Log.d("ZestSed", "email put into shared preferences is " + mEmailView.getText().toString());

                    SharedPreferences.Editor editor = getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).edit();
                    editor.putString("email", mEmailView.getText().toString());
                    editor.apply();
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ZestSed", "email put into shared preferences is " + mEmailView.getText().toString());

                SharedPreferences.Editor editor = getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).edit();
                editor.putString("email", mEmailView.getText().toString());
                editor.apply();
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    public void register(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            JSONObject json = new JSONObject();
            try {
                json.put("email", email);
                json.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Intent intent = new Intent(LoginActivity.this, ContributionsTabActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(volleyError.getMessage());
                        builder.setTitle(R.string.app_name);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }
            });
            boolean isTokenRegistered = getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).getBoolean ("isTokenRegistered", false);

            if(!isTokenRegistered) {
                String token = getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).getString("token", "");
                JSONObject tokenData = new JSONObject();
                try {
                    tokenData.put("token", token);
                    tokenData.put("email",email);
                } catch (JSONException e) {
                    Log.d("ZestSed", e.getLocalizedMessage());
                }

                JsonObjectRequest deviceRegisterRequest = new JsonObjectRequest(Request.Method.POST, Constants.BACKEND_BASE_URL + "/mobile/registerDevice", tokenData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG,"Device registration done");
                        getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).edit().putBoolean("isTokenRegistered", true).apply();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"Device registration failed");
                    }
                });
                deviceRegisterRequest.setTag(TAG);
                mRequestQueue.add(deviceRegisterRequest);
            }
            mRequestQueue.add(jsonRequest);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}

