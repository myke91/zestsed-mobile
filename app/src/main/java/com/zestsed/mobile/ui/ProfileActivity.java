package com.zestsed.mobile.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.system.ErrnoException;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    EditText genderSpinner;
    EditText phoneNumber;
    EditText dateOfBirth;
    EditText nextOfKin;
    EditText nextOfKinPhoneNumber;
    EditText occupation;
    EditText residentialAddress;
    EditText purposeOfInvesting;
    private ImageView coverImage;
    private ImageButton profilePic;
    private String userEmail;
    ProgressDialog progressDialog;
    private Uri mCropImageUri;
    Intent cropImageIntent;

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

        coverImage = (ImageView) findViewById(R.id.header_cover_image);
        profilePic = (ImageButton) findViewById(R.id.user_profile_photo);


        firstname = (EditText) findViewById(R.id.firstName);
        lastname = (EditText) findViewById(R.id.lastName);
        othernames = (EditText) findViewById(R.id.otherNames);
        email = (EditText) findViewById(R.id.email);
        genderSpinner = (EditText) findViewById(R.id.gender);
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
                    genderSpinner.setText(client.getGender());
                    phoneNumber.setText(client.getPhoneNumber());
                    dateOfBirth.setText(client.getDateOfBirth());
                    nextOfKin.setText(client.getNextOfKin());
                    nextOfKinPhoneNumber.setText(client.getNextOfKinTelephone());
                    occupation.setText(client.getOccupation());
                    residentialAddress.setText(client.getResidentialAddress());
                    purposeOfInvesting.setText(client.getPurposeOfInvesting());

                    byte[] decodedString = Base64.decode(client.getImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    coverImage.setImageBitmap(decodedByte);
                    profilePic.setImageBitmap(decodedByte);
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
//        Intent intent = new Intent();
//        intent.setType("coverImage/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Choose Profile Picture"), CHOOSE_PICTURE);
        startActivityForResult(getPickImageChooserIntent(), CHOOSE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_PICTURE) {
                Uri imageUri = getPickImageResultUri(data);

                // For API >= 23 we need to check specifically that we have permissions to read external storage,
                // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
                boolean requirePermissions = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        isUriRequiresPermissions(imageUri)) {

                    mCropImageUri = imageUri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }

                cropImageIntent = new Intent(this, CropImageActivity.class);
                cropImageIntent.putExtra("image", imageUri); //Optional parameters{
                startActivityForResult(cropImageIntent, CROP_PICTURE);
            }
        }
        if (requestCode == CROP_PICTURE) {
            JSONObject json = new JSONObject();
             Bitmap bm = null;
            if (data.getData() != null) {
                try {
                    bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                json.put("image", encodeImage(bm));
                json.put("email", userEmail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.setMessage("Updating coverImage...");
            progressDialog.show();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.BACKEND_BASE_URL + "/mobile/update/coverImage", json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    progressDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                }
            });
            mRequestQueue.add(request);

            if (bm != null) {
                ImageView imageView = (ImageView) findViewById(R.id.header_cover_image);
                ImageButton profilePic = (ImageButton) findViewById(R.id.user_profile_photo);
                imageView.setImageBitmap(bm);
                profilePic.setImageBitmap(bm);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void enableInput(EditText input) {
        input.setEnabled(true);
        input.requestFocus();
    }

    public void updateProfileInfo(View view) {

        JSONObject json = new JSONObject();

        try {
            json.put("firstName", firstname.getText().toString());
            json.put("lastName", lastname.getText().toString());
            json.put("otherNames", othernames.getText().toString());
            json.put("genter", genderSpinner.getText().toString());
            json.put("phoneNumber", phoneNumber.getText().toString());
            json.put("dateOfBirth", dateOfBirth.getText().toString());
            json.put("nextOfKin", nextOfKin.getText().toString());
            json.put("nextOfKinPhoneNumber", nextOfKinPhoneNumber.getText().toString());
            json.put("occupation", occupation.getText().toString());
            json.put("residentialAddress", residentialAddress.getText().toString());
            json.put("purposeOfInvesting", purposeOfInvesting.getText().toString());
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


    /**
     * Create a chooser intent to select the  source to get coverImage from.<br/>
     * The source can be camera's  (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the  intent chooser.
     */
    public Intent getPickImageChooserIntent() {

// Determine Uri of camera coverImage to  save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

// collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

// collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("coverImage/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

// the main intent is the last in the  list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

// Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

// Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to coverImage received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected coverImage from  {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera  and gallery coverImage.
     *
     * @param data the returned data of the  activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
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
