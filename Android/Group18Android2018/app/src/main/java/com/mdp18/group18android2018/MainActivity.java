package com.mdp18.group18android2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
