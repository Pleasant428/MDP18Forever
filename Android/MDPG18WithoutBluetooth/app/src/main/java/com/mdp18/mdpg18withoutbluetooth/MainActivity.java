package com.mdp18.mdpg18withoutbluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    PixelGridView mPGV;
    ImageButton forwardButton;
    ImageButton leftRotateButton;
    ImageButton rightRotateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//

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

    }





}
