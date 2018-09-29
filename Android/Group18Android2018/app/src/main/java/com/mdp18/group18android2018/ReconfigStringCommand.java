package com.mdp18.group18android2018;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ReconfigStringCommand extends AppCompatActivity{

    private static final String TAG = "ReconfigStringCommand";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Reconfigurable String Commands");
        setContentView(R.layout.activity_reconfig);

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu if present
        getMenuInflater().inflate(R.menu.menu_reconfig, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()){
            case R.id.btn_bluetoothconnect:
                startActivity(new Intent(ReconfigStringCommand.this,BluetoothConnect.class));
                return true;

            case R.id.btn_mainscreen:
                startActivity(new Intent(ReconfigStringCommand.this,MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {

    }
}
