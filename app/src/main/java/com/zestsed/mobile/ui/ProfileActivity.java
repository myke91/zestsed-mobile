package com.zestsed.mobile.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zestsed.mobile.R;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference dbReference;
    StorageReference storageRef;
    final int CHOOSE_PICTURE = 121;
    final int CROP_PICTURE = 120;

    EditText txtFullname = null;
    EditText txtOccupation = null;
    EditText txtResidentialAddress = null;
    EditText txtHometown = null;
    EditText txtHighSchool = null;
    EditText txtCollege = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtFullname = (EditText) findViewById(R.id.fullname);
        txtOccupation = (EditText) findViewById(R.id.occupation);
        txtResidentialAddress = (EditText) findViewById(R.id.residential_address);
        txtHometown = (EditText) findViewById(R.id.hometown);
        txtHighSchool = (EditText) findViewById(R.id.high_school);
        txtCollege = (EditText) findViewById(R.id.college);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        dbReference = database.getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbReference.child("clients").orderByChild("email")
                .equalTo(auth.getCurrentUser().getEmail())
                .addValueEventListener(new ValueEventListener() {
                    String key;

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            key = snapshot.getKey();
                            System.out.println("found data for client with key " + key);
                            ((TextView) findViewById(R.id.user_profile_name)).setText(snapshot.child("fullname").getValue(String.class));
                            ((TextView) findViewById(R.id.user_profile_email)).setText(snapshot.child("email").getValue(String.class));
                            txtFullname.setText(snapshot.child("fullname").getValue(String.class));
                            System.out.println("clients full name " + snapshot.child("fullname").getValue(String.class));
                            txtOccupation.setText(snapshot.child("occupation").getValue(String.class));
                            txtResidentialAddress.setText(snapshot.child("residentialAddress").getValue(String.class));
                            txtHometown.setText(snapshot.child("hometown").getValue(String.class));
                            txtHighSchool.setText(snapshot.child("highSchool").getValue(String.class));
                            txtCollege.setText(snapshot.child("college").getValue(String.class));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed " + databaseError.getMessage());
                    }
                });


            final ImageView image = (ImageView) findViewById(R.id.header_cover_image);
            final ImageButton profilePic = (ImageButton) findViewById(R.id.user_profile_photo);
            String userEmail = getApplicationContext().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).getString("email", "");
            StorageReference profile = storageRef.child("profileImages/" + userEmail);

            final long ONE_MEGABYTE = 1024 * 1024;
            profile.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap profileBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setImageBitmap(profileBitmap);
                    profilePic.setImageBitmap(profileBitmap);
                }
            });

    }

    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (user == null) {
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        }
    };

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
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(data.getData())
                        .build();
                user.updateProfile(profileUpdates).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("ZestSed", "User profile updated.");
                                }
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


                Log.d("ZestSed", "going to upload picture to firebase storage");
                String email = getApplication().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE).getString("email", "");
                System.out.println("email of user " + email);
                StorageReference imageStorage = storageRef.child("profileImages/" + email);
                imageStorage.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("ZestSed", "picture upload successful " + taskSnapshot.getDownloadUrl());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ZestSed", "picture upload failed " + e.getLocalizedMessage());
                    }
                });
            }
        }
    }

    public void enableInput(EditText input) {
        input.setEnabled(true);
        input.requestFocus();
    }

    public void updateProfileInfo(View view) {

        final EditText txtFullname = (EditText) findViewById(R.id.fullname);
        final EditText txtOccupation = (EditText) findViewById(R.id.occupation);
        final EditText txtResidentialAddress = (EditText) findViewById(R.id.residential_address);
        final EditText txtHometown = (EditText) findViewById(R.id.hometown);
        final EditText txtHighSchool = (EditText) findViewById(R.id.high_school);
        final EditText txtCollege = (EditText) findViewById(R.id.college);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(txtFullname.getText().toString())
                .build();

        user.updateProfile(profileUpdates).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ZestSed", "User profile updated.");
                        }
                    }
                });

        dbReference.child("clients").orderByChild("email")
                .equalTo(auth.getCurrentUser().getEmail())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String key = snapshot.getKey();
                            System.out.println("updating data for client with key " + key);

                            DatabaseReference client = dbReference.child("clients").child(key);
                            HashMap<String, Object> values = new HashMap<String, Object>();
                            values.put("fullname", txtFullname.getText().toString());
                            values.put("occupation", txtOccupation.getText().toString());
                            values.put("residentialAddress", txtResidentialAddress.getText().toString());
                            values.put("hometown", txtHometown.getText().toString());
                            values.put("highSchool", txtHighSchool.getText().toString());
                            values.put("college", txtCollege.getText().toString());
                            client.updateChildren(values);
                            System.out.println("updated children");
                            System.out.println("with full name " + values.get("fullname"));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed " + databaseError.getMessage());
                    }
                });
        txtFullname.setEnabled(false);
        txtOccupation.setEnabled(false);
        txtResidentialAddress.setEnabled(false);
        txtHometown.setEnabled(false);
        txtHighSchool.setEnabled(false);
        txtCollege.setEnabled(false);

    }

    public void cancelProfileUpdate(View view) {
        findViewById(R.id.fullname).setEnabled(false);
        findViewById(R.id.occupation).setEnabled(false);
        findViewById(R.id.residential_address).setEnabled(false);
        findViewById(R.id.hometown).setEnabled(false);
        findViewById(R.id.high_school).setEnabled(false);
        findViewById(R.id.college).setEnabled(false);
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
            auth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
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
