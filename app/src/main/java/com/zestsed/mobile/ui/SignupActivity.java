package com.zestsed.mobile.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.zestsed.mobile.R;
import com.zestsed.mobile.data.Client;
import com.zestsed.mobile.data.Constants;
import com.zestsed.mobile.utils.CONSTANTS;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SIGNUP_ACTIVITY";

    TextInputEditText firstname;
    TextInputEditText lastname;
    TextInputEditText othernames;
    TextInputEditText email;
    Spinner genderSpinner;
    TextInputEditText phoneNumber;
    TextInputEditText dateOfBirth;
    TextInputEditText nextOfKin;
    TextInputEditText nextOfKinPhoneNumber;
    TextInputEditText occupation;
    TextInputEditText residentialAddress;
    TextInputEditText purposeOfInvesting;

    RequestQueue mRequestQueue;
    String url = Constants.BACKEND_BASE_URL + "/mobile/register";
    SharedPreferences pref;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        pref = getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        mRequestQueue = Volley.newRequestQueue(this);

        //get widget references
        firstname = (TextInputEditText) findViewById(R.id.firstName);
        lastname = (TextInputEditText) findViewById(R.id.lastName);
        othernames = (TextInputEditText) findViewById(R.id.otherNames);
        email = (TextInputEditText) findViewById(R.id.email);
        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        phoneNumber = (TextInputEditText) findViewById(R.id.phonenumber);
        dateOfBirth = (TextInputEditText) findViewById(R.id.dateOfBirth);
        nextOfKin = (TextInputEditText) findViewById(R.id.nextOfKin);
        nextOfKinPhoneNumber = (TextInputEditText) findViewById(R.id.nextOfKinTelephone);
        occupation = (TextInputEditText) findViewById(R.id.occupation);
        residentialAddress = (TextInputEditText) findViewById(R.id.residentialAddress);
        purposeOfInvesting = (TextInputEditText) findViewById(R.id.purposeOfInvesting);

        final CharSequence[] GENDER_OPTIONS = {"----", "Male", "Female"};
        ArrayAdapter<CharSequence> genderAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, GENDER_OPTIONS);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        setDateTimeField();
    }

    private void setDateTimeField() {
        dateOfBirth.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateOfBirth.setInputType(InputType.TYPE_NULL);
        dateOfBirth.setFocusable(false);
        dateOfBirth.setClickable(true);
        dateOfBirth.requestFocus();

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateOfBirth.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    public boolean isEmail(String str) {
        Matcher matcher = Constants.VALID_EMAIL_ADDRESS_REGEX.matcher(str);
        return matcher.find();
    }

    public void registerClientAction(View view) {
        boolean error = false;
        if (TextUtils.isEmpty(firstname.getText().toString())) {
            firstname.setError("Enter First Name");
            error = true;
        }
        if (TextUtils.isEmpty(lastname.getText().toString())) {
            lastname.setError("Enter Last Name");
            error = true;
        }

        if (!isEmail(email.getText().toString())) {
            email.setError("Invalid Email");
            error = true;
        }

        if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
            phoneNumber.setError("Enter Phone Number");
            error = true;
        }

        if (genderSpinner.getSelectedItem().toString().equalsIgnoreCase("-")) {
            TextView v = (TextView) genderSpinner.getSelectedView();
            v.setError("Select a gender");
            error = true;
        }
        if (TextUtils.isEmpty(dateOfBirth.getText().toString())) {
            dateOfBirth.setError("Enter Date Of Birth");
            error = true;
        }

        if (TextUtils.isEmpty(nextOfKin.getText().toString())) {
            nextOfKin.setError("Enter Next Of Kin Name");
            error = true;
        }
        if (TextUtils.isEmpty(nextOfKinPhoneNumber.getText().toString())) {
            nextOfKinPhoneNumber.setError("Enter Next Of Kin Phone Number");
            error = true;
        }
        if (TextUtils.isEmpty(occupation.getText().toString())) {
            occupation.setError("Enter Occupation");
            error = true;
        }
        if (TextUtils.isEmpty(residentialAddress.getText().toString())) {
            residentialAddress.setError("Enter Residential Address");
            error = true;
        }
        if (TextUtils.isEmpty(purposeOfInvesting.getText().toString())) {
            purposeOfInvesting.setError("Enter Purpose of Investing");
            error = true;
        }

        if (error) {
            return;
        }

        registerClient();
    }

    private void registerClient() {
        final ProgressDialog progressDialog = ProgressDialog.show(SignupActivity.this, "", "Registering Client...");
        progressDialog.show();
        String fname = firstname.getText().toString();
        String lname = lastname.getText().toString();
        String oname = othernames.getText().toString();
        String mail = email.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String phone = phoneNumber.getText().toString();
        String date = dateOfBirth.getText().toString();
        String kin = nextOfKin.getText().toString();
        String kinPhone = nextOfKinPhoneNumber.getText().toString();
        String occu = occupation.getText().toString();
        String rAddress = residentialAddress.getText().toString();
        String purpose = purposeOfInvesting.getText().toString();


        Client client = Client.load(fname, lname, oname, mail, gender, phone, date, kin, kinPhone, occu, rAddress, purpose);
        JsonObjectRequest clientRegisterRequest = new JsonObjectRequest(Request.Method.POST, url, client, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("message", "Registration Successful. Proceed to Login!");
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage(volleyError.getMessage());
                    builder.setTitle(R.string.app_name);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage("SERVER ERROR");
                    builder.setTitle(R.string.app_name);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

        });


        JSONObject json = new JSONObject();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (token == null) {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
            builder.setMessage("Kindly check your connection to the internet!");
            builder.setTitle("REGISTRATION FAILED");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        try {
            json.put("token", token);
            json.put("email", mail);
        } catch (JSONException e) {
            Log.d("ZestSed", e.getLocalizedMessage());
        }

        JsonObjectRequest deviceRegisterRequest = new JsonObjectRequest(Request.Method.POST, Constants.BACKEND_BASE_URL + "/mobile/registerDevice", json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error occured while registrating device");
            }
        });
        deviceRegisterRequest.setTag(TAG);
        mRequestQueue.add(deviceRegisterRequest);

        clientRegisterRequest.setTag(TAG);
        mRequestQueue.add(clientRegisterRequest);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == dateOfBirth) {
            datePickerDialog.show();
        }
    }
}
