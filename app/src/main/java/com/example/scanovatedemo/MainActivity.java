package com.example.scanovatedemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    boolean isCameraApproved = false;
    int MY_PERMISSIONS_REQUEST_CAMERA = 1111;
    private static final String TAG = "MainActivity";
    private static final int requestCode = 51;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isCameraApproved)
        {
            checkPermission();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        SharedPreferences sharedPref = getSharedPreferences("pref",Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "n/a");
        Log.d("Token on resume",token);

    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // ask permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            isCameraApproved = true;
            Intent intent = new Intent(this, WebViewActivity.class);
            startActivityForResult(intent, requestCode);

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

        Log.d(TAG, "onActivityResult");
        if (requestCode == requestCode) {
            if(resultCode == Activity.RESULT_OK){

                String token =data.getStringExtra("token");

                Log.d("Token form result", token);

                // Use token for getting the result from the server

                RequestQueue queue = Volley.newRequestQueue(this);
                String url ="https://btrust-api-snb.scanovate.com/results/"+token;

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
//                                textView.setText("Response is: "+ response.substring(0,500));
                                JSONObject reader = null;
                                try {
                                    reader = new JSONObject(response);

                                    Log.d("Response", String.valueOf(reader));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("REQUEST","That didn't work!");
                    }
                });

// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}