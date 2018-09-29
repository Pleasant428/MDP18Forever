package com.mdp18.group18android2018;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    PixelGridView mPGV;
    ImageButton forwardButton, leftRotateButton, rightRotateButton, reverseButton;
    TextView tv_status, tv_string;
    EditText et_status, et_stringcmd;


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
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("BLUETOOTH DISCONNECTED");
                    alertDialog.setMessage("Connection with device: '" + myBTConnectionDevice.getName() + "' has ended. Do you want to reconnect?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    //START BT CONNECTION SERVICE
                                    Intent connectIntent = new Intent(MainActivity.this, BluetoothConnectionService.class);
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
                Toast.makeText(MainActivity.this, "Connection Established: " + myBTConnectionDevice.getName(),
                        Toast.LENGTH_SHORT).show();
            }

            //BLUETOOTH CONNECTION FAILED
            else if (connectionStatus.equals("connectionFail")) {
                Toast.makeText(MainActivity.this, "Connection Failed: " + myBTConnectionDevice.getName(),
                        Toast.LENGTH_SHORT).show();
            }

        }
    };



}
