package com.example.scanovatedemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    boolean isCameraApproved;
    int MY_PERMISSIONS_REQUEST_CAMERA = 1111;
    private static final String TAG = "MainActivity";
    private static final int requestCode = 51;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // ask permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            isCameraApproved = true;
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            // Received permission result for camera permission.
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                isCameraApproved = true;
                Intent intent = new Intent(this, WebViewActivity.class);
                startActivityForResult(intent, requestCode);

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                isCameraApproved = false;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    boolean cbNeverShowDialog = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
                    if(!cbNeverShowDialog){

                        //alert dialog - message the user to allow permission
                        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                        ab.setTitle("The app needs access to your camera");
                        ab.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alert = ab.create();
                        alert.show();
                    }
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCode) {
            if(resultCode == Activity.RESULT_OK){

                String token =data.getStringExtra("token");

                Log.i(TAG, "Token: "+token);

                // Use token for getting the result from the server
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result

            }
        }
    }
}