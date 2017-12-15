package com.qiscus.streamingviewersample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qiscus.streamingviewersample.util.AsyncHttpUrlConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText txtRtmpUrl = (EditText) findViewById(R.id.txt_rtmp_url);
        Button btnStart = (Button) findViewById(R.id.b_start_stop);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpUrlConnection httpUrlConnection = new AsyncHttpUrlConnection("GET", "/watch/mobile/" + txtRtmpUrl.getText().toString(), "", new AsyncHttpUrlConnection.AsyncHttpEvents() {
                    @Override
                    public void onHttpError(String errorMessage) {
                        Log.e(TAG, "API connection error: " + errorMessage);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Can not get streaming metadata.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onHttpComplete(String response) {
                        Log.d(TAG, "API connection success: " + response);
                        try {
                            JSONObject objStream = new JSONObject(response);
                            JSONObject data = new JSONObject(objStream.getString("data"));
                            Intent i = new Intent(MainActivity.this, ViewerActivity.class);
                            i.putExtra("STREAM_ID", txtRtmpUrl.getText().toString());
                            i.putExtra("STREAM_HLS", data.getString("hls_url"));
                            i.putExtra("STREAM_RTMP", data.getString("play_url"));
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, e.getMessage());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Can not get streaming metadata.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                httpUrlConnection.setContentType("application/json");
                httpUrlConnection.send();
            }
        });
    }
}
