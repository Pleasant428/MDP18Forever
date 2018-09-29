package com.mdp18.mdpg18;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button enter_btn;
    TextView welcome_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enter_btn = (Button) findViewById(R.id.enter_btn);
        welcome_msg = (TextView) findViewById(R.id.welcome_msg);
    }

    public void goToBluetoothList (View v) {
        Intent intent = new Intent (this, BluetoothConnecting.class);
        startActivity(intent);
    }

}