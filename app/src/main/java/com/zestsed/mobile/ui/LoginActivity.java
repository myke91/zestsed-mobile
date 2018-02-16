package com.zestsed.mobile.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zestsed.mobile.data.Contribution;
import com.zestsed.mobile.services.MyFirebaseInstanceIDService;
import com.zestsed.mobile.services.MyFirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {


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
    private static int LOGIN_ACTION = 543;

    String url = Constants.BACKEND_BASE_URL + "/mobile/login";
    RequestQueue mRequestQueue;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mRequestQueue = Volley.newRequestQueue(this);
        pref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        if (getIntent().getExtras() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Your registration is saved successfully and pending approval. \n You will receive a mail and notification after approval");
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
                if (id == LOGIN_ACTION || id == EditorInfo.IME_NULL) {
                    Log.d("ZestSed", "email put into shared preferences is " + mEmailView.getText().toString());

                    SharedPreferences.Editor editor = pref.edit();
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

                SharedPreferences.Editor editor = pref.edit();
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
                    String message = "";

                    try {
                        message = response.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    boolean firstTimeLogin = message.equalsIgnoreCase("FIRST TIME LOGIN");
                    if (firstTimeLogin) {
                        showSetPasswordDialog();
                    } else {
                        pref.edit().putString("email", email).apply();
                        Intent intent = new Intent(LoginActivity.this, ContributionsTabActivity.class);
                        startActivity(intent);
                        finish();
                    }
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
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("SERVER ERROR");
                        builder.setTitle(R.string.app_name);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }
            });
            JSONObject deviceJson = new JSONObject();
            String token = FirebaseInstanceId.getInstance().getToken();
            if (token == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Kindly check your connection to the internet!");
                builder.setTitle("LOGIN ERROR");
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
            try {
                deviceJson.put("email", mEmailView.getText().toString());
                deviceJson.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final JsonObjectRequest deviceRegisterRequest = new JsonObjectRequest(Request.Method.POST, Constants.BACKEND_BASE_URL + "/mobile/registerDevice", deviceJson, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error occurred while registering device");

                }
            });

            mRequestQueue.add(deviceRegisterRequest);
            mRequestQueue.add(jsonRequest);
        }
    }



    private boolean isEmailValid(String email) {
        Matcher matcher = Constants.VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public void showSetPasswordDialog() {
        showProgress(false);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_set_password, (ViewGroup) findViewById(R.id.activity_dialog_parent));
        final TextInputEditText txtPassword = (TextInputEditText) layout.findViewById(R.id.password_1);
        final TextInputEditText txtPassword2 = (TextInputEditText) layout.findViewById(R.id.password_2);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!txtPassword.getText().toString().equals(txtPassword2.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Password and Repeat Password not equal", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

                final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "", "Processing request...");
                progressDialog.show();
                JSONObject data = new JSONObject();
                try {
                    data.put("password", txtPassword.getText().toString());
                    data.put("email", mEmailView.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.BACKEND_BASE_URL + "/mobile/setPassword", data, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Request successful. \n Proceed to login with new password", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                            Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "SERVER ERROR", Toast.LENGTH_LONG).show();
                        }

                    }
                });
                jsonRequest.setTag(TAG);
                mRequestQueue.add(jsonRequest);

            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialogSetPassword = builder.create();
        dialogSetPassword.show();
        dialogSetPassword.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
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

