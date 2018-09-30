package com.mdp18.group18android2018;


import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.UUID;

public class ReconfigStringCommand extends AppCompatActivity{

    private static final String TAG = "ReconfigStringCommand";

    Button btn_save, btn_reset, btn_retrieve, btn_f1, btn_f2;
    EditText et_f1, et_f2;

    SharedPreferences myPrefs;

    // For bluetooth connection status
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice myBTConnectionDevice;
    static String connectedDevice;
    boolean connectedState;
    TextView connectionStatusBox;
    boolean currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Reconfigurable String Commands");
        setContentView(R.layout.activity_reconfig);

        //REGISTER BROADCAST RECEIVER FOR IMCOMING MSG
        LocalBroadcastManager.getInstance(this).registerReceiver(btConnectionReceiver, new IntentFilter("btConnectionStatus"));

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        btn_retrieve = (Button) findViewById(R.id.btn_retrieve);
        btn_f1 = (Button) findViewById(R.id.btn_f1);
        btn_f2 = (Button) findViewById(R.id.btn_f2);

        myPrefs=getSharedPreferences("myPrefs", MODE_PRIVATE);

        init();

    }

    private void init() {
        et_f1 = (EditText) findViewById(R.id.et_f1);
        et_f2 = (EditText) findViewById(R.id.et_f2);
    }

    public void Save(View view) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("f1", et_f1.getText().toString());
        editor.putString("f2", et_f2.getText().toString());
        editor.apply();

        Toast.makeText(this, "Commands saved!", Toast.LENGTH_SHORT).show();
    }

    public void Reset(View view) {
        SharedPreferences.Editor editor = myPrefs.edit();
        et_f1.setText("");
        et_f2.setText("");
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Commands reset!", Toast.LENGTH_SHORT).show();
    }

    public void Retrieve (View view) {
        String str_f1 = myPrefs.getString("f1", "");
        et_f1.setText(str_f1);
        String str_f2 = myPrefs.getString("f2", "");
        et_f2.setText(str_f2);

        Toast.makeText(this, "Commands retrieved!", Toast.LENGTH_SHORT).show();

    }

    public void onClickF1(){

        btn_f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempF1 = myPrefs.getString("f1", "");
                byte[] bytes = tempF1.getBytes(Charset.defaultCharset());
                BluetoothChat.writeMsg(bytes);

                Log.d(TAG, "Outgoing F1 string command: " + tempF1);

                Toast.makeText(ReconfigStringCommand.this, "F1 string command sent.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onClickF2() {

        btn_f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempF2 = myPrefs.getString("f2", "");
                byte[] bytes = tempF2.getBytes(Charset.defaultCharset());
                BluetoothChat.writeMsg(bytes);

                Log.d(TAG, "Outgoing F2 string command: " + tempF2);

                Toast.makeText(ReconfigStringCommand.this, "F2 string command sent.", Toast.LENGTH_SHORT).show();
            }
        });

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
                Intent intent = new Intent(ReconfigStringCommand.this, BluetoothConnect.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;

            case R.id.btn_mainscreen:
                startActivity(new Intent(ReconfigStringCommand.this,MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ReconfigStringCommand.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }


    //BROADCAST RECEIVER FOR BLUETOOTH CONNECTION STATUS
    BroadcastReceiver btConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "Receiving btConnectionStatus Msg!!!");

            String connectionStatus = intent.getStringExtra("ConnectionStatus");
            myBTConnectionDevice = intent.getParcelableExtra("Device");

            //DISCONNECTED FROM BLUETOOTH CHAT
            if (connectionStatus.equals("disconnect")) {

                Log.d("MainActivity:", "Device Disconnected");
                connectedDevice = null;
                connectedState = false;
                connectionStatusBox.setText(R.string.btStatusOffline);

                if (currentActivity) {

                    //RECONNECT DIALOG MSG
                    AlertDialog alertDialog = new AlertDialog.Builder(ReconfigStringCommand.this).create();
                    alertDialog.setTitle("BLUETOOTH DISCONNECTED");
                    alertDialog.setMessage("Connection with device: '" + myBTConnectionDevice.getName() + "' has ended. Do you want to reconnect?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    //START BT CONNECTION SERVICE
                                    Intent connectIntent = new Intent(ReconfigStringCommand.this, BluetoothConnectionService.class);
                                    connectIntent.putExtra("serviceType", "connect");
                                    connectIntent.putExtra("device", myBTConnectionDevice);
                                    connectIntent.putExtra("id", myUUID);
                                    startService(connectIntent);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                }
            }
            //SUCCESSFULLY CONNECTED TO BLUETOOTH DEVICE
            else if (connectionStatus.equals("connect")) {

                connectedDevice = myBTConnectionDevice.getName();
                connectedState = true;
                Log.d("MainActivity:", "Device Connected " + connectedState);
                connectionStatusBox.setText(connectedDevice);
                Toast.makeText(ReconfigStringCommand.this, "Connection Established: " + myBTConnectionDevice.getName(),
                        Toast.LENGTH_SHORT).show();
            }

            //BLUETOOTH CONNECTION FAILED
            else if (connectionStatus.equals("connectionFail")) {
                Toast.makeText(ReconfigStringCommand.this, "Connection Failed: " + myBTConnectionDevice.getName(),
                        Toast.LENGTH_SHORT).show();
            }

        }
    };
}
