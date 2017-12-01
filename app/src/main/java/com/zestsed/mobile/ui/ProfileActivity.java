package com.zestsed.mobile.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestsed.mobile.R;
import com.zestsed.mobile.data.Client;
import com.zestsed.mobile.data.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "PROFILE_ACTIVITY";
    final int CHOOSE_PICTURE = 121;
    final int CROP_PICTURE = 120;
    private RequestQueue mRequestQueue;
    EditText firstname;
    EditText lastname;
    EditText othernames;
    EditText email;
    Spinner genderSpinner;
    EditText phoneNumber;
    EditText dateOfBirth;
    EditText nextOfKin;
    EditText nextOfKinPhoneNumber;
    EditText occupation;
    EditText residentialAddress;
    EditText purposeOfInvesting;
    private ImageView image;
    private ImageButton profilePic;
    private String userEmail;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = ProgressDialog.show(this, "", "Loading profile data...");
        final long ONE_MEGABYTE = 1024 * 1024;
        userEmail = getApplicationContext().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).getString("email", "");
        mRequestQueue = Volley.newRequestQueue(this);

        image = (ImageView) findViewById(R.id.header_cover_image);
        profilePic = (ImageButton) findViewById(R.id.user_profile_photo);


        firstname = (EditText) findViewById(R.id.firstName);
        lastname = (EditText) findViewById(R.id.lastName);
        othernames = (EditText) findViewById(R.id.otherNames);
        email = (EditText) findViewById(R.id.email);
        genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        phoneNumber = (EditText) findViewById(R.id.phonenumber);
        dateOfBirth = (EditText) findViewById(R.id.dateOfBirth);
        nextOfKin = (EditText) findViewById(R.id.nextOfKin);
        nextOfKinPhoneNumber = (EditText) findViewById(R.id.nextOfKinTelephone);
        occupation = (EditText) findViewById(R.id.occupation);
        residentialAddress = (EditText) findViewById(R.id.residentialAddress);
        purposeOfInvesting = (EditText) findViewById(R.id.purposeOfInvesting);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        progressDialog.show();
        final ObjectMapper mapper = new ObjectMapper();
        String url = Constants.BACKEND_BASE_URL + "/mobile/profile?email=" + userEmail;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {

                    Client client = mapper.readValue(response.toString(), Client.class);
                    ((TextView) findViewById(R.id.user_profile_name)).setText(client.getFullName());
                    ((TextView) findViewById(R.id.user_profile_email)).setText(client.getEmail());

                    firstname.setText(client.getFirstName());
                    lastname.setText(client.getLastName());
                    othernames.setText(client.getOtherNames());
                    email.setText(client.getEmail());
                    genderSpinner.setSelection(1);
                    phoneNumber.setText(client.getPhoneNumber());
                    dateOfBirth.setText(client.getDateOfBirth());
                    nextOfKin.setText(client.getNextOfKin());
                    nextOfKinPhoneNumber.setText(client.getNextOfKinTelephone());
                    occupation.setText(client.getOccupation());
                    residentialAddress.setText(client.getResidentialAddress());
                    purposeOfInvesting.setText(client.getPurposeOfInvesting());
                } catch (Exception ex) {
                    Log.d(TAG, "Error occurred while retrieving properties from json object " + ex.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setMessage(volleyError.getMessage());
                    builder.setTitle(R.string.app_name);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setMessage(error.getMessage());
                    builder.setTitle(R.string.app_name);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });
        jsonRequest.setTag(TAG);
        mRequestQueue.add(jsonRequest);
    }


    public void enableInput(View view) {
        String viewTag = (String) view.getTag();
        int id = getResources().getIdentifier(viewTag, "id", getPackageName());
        this.enableInput((EditText) findViewById(id));

    }

    public void updateProfilePicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose Profile Picture"), CHOOSE_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_PICTURE) {
                System.out.println("picture data from choose picture " + data);

                try {
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    // indicate image type and Uri
                    cropIntent.setDataAndType(data.getData(), "image/*");
                    // set crop properties here
                    cropIntent.putExtra("crop", true);
                    // indicate aspect of desired crop
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    // indicate output X and Y
                    cropIntent.putExtra("outputX", 256);
                    cropIntent.putExtra("outputY", 256);
                    // retrieve data on return
                    cropIntent.putExtra("return-data", true);
                    // start the activity - we handle returning in onActivityResult
                    startActivityForResult(cropIntent, CROP_PICTURE);
                } catch (ActivityNotFoundException anfe) {
                    String errorMessage = "Your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            if (requestCode == CROP_PICTURE) {
                JSONObject json = new JSONObject();
                try {
                    json.put("image", data.getData());
                    json.put("email", userEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.BACKEND_BASE_URL + "/mobile/update/image", json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                ImageView imageView = (ImageView) findViewById(R.id.header_cover_image);
                ImageButton profilePic = (ImageButton) findViewById(R.id.user_profile_photo);

                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                imageView.setImageBitmap(selectedBitmap);
                profilePic.setImageBitmap(selectedBitmap);
            }
        }
    }

    public void enableInput(EditText input) {
        input.setEnabled(true);
        input.requestFocus();
    }

    public void updateProfileInfo(View view) {

        JSONObject json = new JSONObject();

        try {
            json.put("fullname", firstname.getText().toString());
            json.put("occupation", lastname.getText().toString());
            json.put("residentialAddress", othernames.getText().toString());
            json.put("hometown", genderSpinner.getSelectedItem().toString());
            json.put("highSchool", phoneNumber.getText().toString());
            json.put("college", dateOfBirth.getText().toString());
            json.put("college", nextOfKin.getText().toString());
            json.put("college", nextOfKinPhoneNumber.getText().toString());
            json.put("college", occupation.getText().toString());
            json.put("college", residentialAddress.getText().toString());
            json.put("college", purposeOfInvesting.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.BACKEND_BASE_URL + "/mobile/update/profile", json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                firstname.setEnabled(false);
                lastname.setEnabled(false);
                othernames.setEnabled(false);
                email.setEnabled(false);
                genderSpinner.setEnabled(false);
                phoneNumber.setEnabled(false);
                dateOfBirth.setEnabled(false);
                nextOfKin.setEnabled(false);
                nextOfKinPhoneNumber.setEnabled(false);
                occupation.setEnabled(false);
                residentialAddress.setEnabled(false);
                purposeOfInvesting.setEnabled(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


    }

    public void cancelProfileUpdate(View view) {
        firstname.setEnabled(false);
        lastname.setEnabled(false);
        othernames.setEnabled(false);
        email.setEnabled(false);
        genderSpinner.setEnabled(false);
        phoneNumber.setEnabled(false);
        dateOfBirth.setEnabled(false);
        nextOfKin.setEnabled(false);
        nextOfKinPhoneNumber.setEnabled(false);
        occupation.setEnabled(false);
        residentialAddress.setEnabled(false);
        purposeOfInvesting.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            getApplicationContext().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contributions) {
            startActivity(new Intent(ProfileActivity.this, ContributionsTabActivity.class));
            finish();
        } else if (id == R.id.nav_products) {
            startActivity(new Intent(ProfileActivity.this, ProductsActivity.class));
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            finish();
        } else if (id == R.id.nav_about_us) {
            startActivity(new Intent(ProfileActivity.this, AboutUsActivity.class));
            finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
