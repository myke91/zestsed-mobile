package com.zestsed.mobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.zestsed.mobile.R;
import com.zestsed.mobile.data.Client;
import com.zestsed.mobile.utils.CONSTANTS;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    DatabaseReference dbReference;
    TextInputEditText fullname;
    TextInputEditText email;
    TextInputEditText password;
    Spinner genderSpinner;
    TextInputEditText phoneNumber;
    TextInputEditText dateOfBirth;
    TextInputEditText nextOfKin;
    TextInputEditText nextOfKinPhoneNumber;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //set database reference
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbReference = db.getReference("clients");

        //set up firebase auth
        auth = FirebaseAuth.getInstance();

        //add value listener
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                Log.d(CONSTANTS.TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(CONSTANTS.TAG, "Failed to read value.", error.toException());
            }
        });

        //get widget references
        fullname = (TextInputEditText) findViewById(R.id.fullname);
        email = (TextInputEditText) findViewById(R.id.email);
        password = (TextInputEditText) findViewById(R.id.password);
        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        phoneNumber = (TextInputEditText) findViewById(R.id.phonenumber);
        dateOfBirth = (TextInputEditText) findViewById(R.id.dateOfBirth);
        nextOfKin = (TextInputEditText) findViewById(R.id.nextOfKin);
        nextOfKinPhoneNumber = (TextInputEditText) findViewById(R.id.nextOfKinPhoneNumber);

        final CharSequence[] GENDER_OPTIONS = {"-- GENDER --", "Male", "Female"};
        ArrayAdapter<CharSequence> genderAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, GENDER_OPTIONS);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

    }

    static Client existingClient;

    public void registerClientAction(View view) {
        System.out.println(email.getText().toString());
        boolean error = false;
        if (TextUtils.isEmpty(fullname.getText().toString())) {
            fullname.setError("Enter Full Name");
            error = true;
        }
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Enter Email");
            error = true;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Enter Password");
            error = true;
        }

        if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
            phoneNumber.setError("Enter Phone Number");
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

        if (error) {
            return;
        }

        Query query = dbReference.orderByChild("email").equalTo(email.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Client> result = (HashMap<String, Client>) dataSnapshot.getValue();
                if (result != null) {
                    existingClient = result.get(0);
                    System.out.println("existing client... " + existingClient);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (existingClient != null) {
            email.setError("E-Mail is already taken");
            return;
        }


        RegisterAsyncTask task = new RegisterAsyncTask();
        task.execute();
    }

    private String registerClient() {
        String name = fullname.getText().toString();
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        String gender = genderSpinner.getSelectedItem().toString();
        String phone = phoneNumber.getText().toString();
        String date = dateOfBirth.getText().toString();
        String kin = nextOfKin.getText().toString();
        String kinPhone = nextOfKinPhoneNumber.getText().toString();

        auth.createUserWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            System.out.println("Task not Successful!");
                        } else {
                            startActivity(new Intent(SignupActivity.this, ContributionsTabActivity.class));
                            finish();
                        }
                    }
                });


        Client client = new Client(name, mail, gender, phone, date, kin, kinPhone);
        String key = dbReference.push().getKey();
        dbReference.child(key).setValue(client);
        return key;
    }

    private class RegisterAsyncTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SignupActivity.this, "", "Registering Client...");
        }

        @Override
        protected String doInBackground(String... strings) {
            return registerClient();
        }

        @Override
        protected void onPostExecute(String result) {

            progressDialog.dismiss();
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putString("message", "Registration Successful. Proceed to Login!");
            intent.putExtras(mBundle);
            startActivity(intent);
            finish();
        }

    }


}
