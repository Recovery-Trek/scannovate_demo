package com.example.scanovatedemo;

import androidx.annotation.NonNull;
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

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
                String url ="https://btrust-api-snb.scanovate.com/results/"+token;
                runOnUiThread(() -> sendRequest(url));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void sendRequest( String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();


        okhttp3.Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.get();
        requestBuilder.url(url);
        requestBuilder.addHeader("Authorization", "Bearer d00980c5-17b9-4d37-86fd-bd16929028eb");
        Call call = client.newCall(requestBuilder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call != null)
                    Log.e("onFailure: request",call.toString());

                if (e != null) {
                    String errorMessage = e.getLocalizedMessage();
                    Log.e("onFailure: exception",errorMessage);
                }
                else {
                    Log.e("error","Unable to reach MobileTrek servers. Error code: 100");
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    String responseStr = response.body().string();


                    Log.d("onResponse", responseStr);
                    if (responseStr.equals("\"success\"")) {
                        Log.d("Response","success");

                    }
                    else {
                        Log.e("Response"," Error code: 102");
                    }
                }
                else {
                    Log.d(TAG, "onResponse: reponse = null");
                }
            }
        });
    }
}