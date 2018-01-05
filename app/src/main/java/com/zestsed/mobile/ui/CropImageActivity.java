package com.zestsed.mobile.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.ErrnoException;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.zestsed.mobile.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class CropImageActivity extends AppCompatActivity {
    private CropImageView mCropImageView;
    private Uri mCropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        Bundle extras = getIntent().getExtras();
        Uri imageUri = (Uri) extras.get("image");


        // For API >= 23 we need to check specifically that we have permissions to read external storage,
        // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
        boolean requirePermissions = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                isUriRequiresPermissions(imageUri)) {

            // request permissions and handle the result in onRequestPermissionsResult()
            requirePermissions = true;
            mCropImageUri = imageUri;
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        if (!requirePermissions) {
            mCropImageView.setImageUriAsync(imageUri);
        }

        Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
        if (cropped != null) {
            mCropImageView.setImageBitmap(cropped);
        }

    }

    public void onCropImageClick(View view) {
        inImage = mCropImageView.getCroppedImage(500, 500);

        Intent _result = new Intent();
        _result.setData(getImageUri());
        setResult(Activity.RESULT_OK, _result);
        finish();
    }

    Uri croppedImageUri;
    Bitmap inImage;

    public Uri getImageUri() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 11);
        }else{
            String path = MediaStore.Images.Media.insertImage(getApplication().getContentResolver(), inImage, "Title", null);
            croppedImageUri = Uri.parse(path);
        }
        return croppedImageUri;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 0) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCropImageView.setImageUriAsync(mCropImageUri);
            } else {
                Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 11) {
            String path = MediaStore.Images.Media.insertImage(getApplication().getContentResolver(), inImage, "Title", null);
            croppedImageUri = Uri.parse(path);
        }

    }

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
}
