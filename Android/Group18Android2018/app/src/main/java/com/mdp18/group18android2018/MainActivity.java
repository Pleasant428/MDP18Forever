package com.mdp18.group18android2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    PixelGridView mPGV;
    ImageButton forwardButton, leftRotateButton, rightRotateButton, reverseButton;
    TextView tv_status, tv_string;
    EditText et_status, et_stringcmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPGV = findViewById(R.id.map);
        mPGV.initializeMap();

        // Forward button
        forwardButton = findViewById(R.id.fwd_btn);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPGV.moveForward();
            }
        });

        leftRotateButton = findViewById(R.id.left_btn);
        leftRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPGV.rotateLeft();
            }
        });

        rightRotateButton = findViewById(R.id.right_btn);
        rightRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPGV.rotateRight();
            }
        });

        reverseButton = findViewById(R.id.rev_btn);
        reverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPGV.moveBackwards();
            }
        });

        tv_status = findViewById(R.id.tv_status);
        tv_string = findViewById(R.id.tv_string);

        // Set un-editable EditText for robot status
        et_status = findViewById(R.id.et_status);
        et_status.setFocusable(false);
        et_status.setClickable(false);

        // Set un-editable EditText for string command
        et_stringcmd = findViewById(R.id.et_stringcmd);
        et_stringcmd.setFocusable(false);
        et_stringcmd.setClickable(false);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu if present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_bluetoothconnect:
                startActivity(new Intent(MainActivity.this,BluetoothConnect.class));
                return true;

            case R.id.btn_reconfig:
                startActivity(new Intent(MainActivity.this,ReconfigStringCommand.class));
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

    }



}
