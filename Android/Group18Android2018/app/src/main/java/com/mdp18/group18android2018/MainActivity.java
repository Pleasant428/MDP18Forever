package com.mdp18.group18android2018;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.nio.charset.Charset;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    boolean updateMap = true;

    PixelGridView mPGV;
    ImageButton forwardButton, leftRotateButton, rightRotateButton, reverseButton;
    Button btn_update;
    TextView tv_status, tv_map_exploration, tv_mystatus, tv_mystringcmd;
    ToggleButton tb_setWaypointCoord, tb_setStartCoord, tb_autoManual;


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

        //REGISTER BROADCAST RECEIVER FOR INCOMING MSG
        LocalBroadcastManager.getInstance(this).registerReceiver(btConnectionReceiver, new IntentFilter("btConnectionStatus"));

        //REGISTER BROADCAST RECEIVER FOR IMCOMING MSG
        LocalBroadcastManager.getInstance(this).registerReceiver(incomingMessageReceiver, new IntentFilter("Incoming Message"));

        mPGV = findViewById(R.id.map);
        mPGV.initializeMap();

        btn_update = (Button) findViewById(R.id.btn_update);
        btn_update.setEnabled(false);

        // Forward button
        forwardButton = (ImageButton) findViewById(R.id.fwd_btn);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check BT connectionIf not connected to any bluetooth device
                if(connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    //If already connected to a bluetooth device
                    String navigate = "And|Ard|0|1";
                    byte[] bytes = navigate.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    Log.d(TAG, "Android Controller: Move Forward {once} sent");
                    tv_mystringcmd.setText(R.string.navFwd);
                }
            }
        });

        leftRotateButton = (ImageButton) findViewById(R.id.left_btn);
        leftRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check BT connectionIf not connected to any bluetooth device
                if(connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    //If already connected to a bluetooth device
                    String navigate = "And|Ard|1|";
                    byte[] bytes = navigate.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    Log.d(TAG, "Android Controller: Turn Left sent");
                    tv_mystringcmd.setText(R.string.navLeft);
                }
            }
        });

        rightRotateButton = (ImageButton) findViewById(R.id.right_btn);
        rightRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check BT connectionIf not connected to any bluetooth device
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    //If already connected to a bluetooth device
                    String navigate = "And|Ard|2|";
                    byte[] bytes = navigate.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    Log.d(TAG, "Android Controller: Turn Right sent");
                    tv_mystringcmd.setText(R.string.navRight);
                }
            }
        });

        reverseButton = (ImageButton) findViewById(R.id.rev_btn);
        reverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check BT connectionIf not connected to any bluetooth device
                if(connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    //If already connected to a bluetooth device
                    String navigate = "And|Ard|3|1";
                    byte[] bytes = navigate.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    Log.d(TAG, "Android Controller: Move Backwards sent");
                    tv_mystringcmd.setText(R.string.navRev);
                }
            }
        });

        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_map_exploration = (TextView) findViewById(R.id.tv_map_exploration);

        tv_mystatus =  (TextView) findViewById(R.id.tv_mystatus);

        tv_mystringcmd = (TextView) findViewById(R.id.tv_mystringcmd);

        tb_setWaypointCoord = (ToggleButton) findViewById(R.id.tb_setWaypointCoord);
        tb_setWaypointCoord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Toast.makeText(MainActivity.this, "Select waypoint on map.", Toast.LENGTH_SHORT).show();
                    mPGV.setWaypoint();
                    tb_setWaypointCoord.toggle();
                    Toast.makeText(MainActivity.this, "Waypoint set to ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tb_setStartCoord = (ToggleButton) findViewById(R.id.tb_setStartCoord);
        tb_setStartCoord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    Toast.makeText(MainActivity.this, "Select starting point on map.", Toast.LENGTH_SHORT).show();
                    mPGV.setStartPoint();
                    setStartDirection();
                    tb_setStartCoord.toggle();
                    Toast.makeText(MainActivity.this, "Start Point set to ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tb_autoManual = (ToggleButton) findViewById(R.id.tb_autoManual);
        tb_autoManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled; Manual Mode
                    btn_update.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Manual Mode enabled", Toast.LENGTH_SHORT).show();
                    updateMap = false;
                    Log.d(TAG, "Auto updates disabled.");

                } else {
                    // The toggle is disabled; Auto Mode
                    btn_update.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Auto Mode enabled", Toast.LENGTH_SHORT).show();
                    updateMap = true;
                    Log.d(TAG, "Auto updates enabled.");
                }
            }
        });
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
        Toast.makeText(MainActivity.this, "Please navigate with the menu on the top right corner!", Toast.LENGTH_SHORT).show();
    }

    // Broadcast Receiver for Incoming Messages
    BroadcastReceiver incomingMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String incomingMsg = intent.getStringExtra("receivingMsg");
            tv_mystringcmd.setText(incomingMsg);

            Log.d(TAG, "Receiving incoming message: " + incomingMsg);

            // Filter empty and concatenated string from receiving channel
            if(incomingMsg.length() > 9 && incomingMsg.length() < 345) {

                // Check if string is for android
                if (incomingMsg.substring(4, 7).equals("And")){

                    String[] filteredMsg = msgDelimiter(incomingMsg.replaceAll(" ", "").replaceAll("\\n", "").trim(), "\\|");

                    // Message: Action
                    Log.d(TAG, "Incoming Message filtered: " + filteredMsg[2]);
                    switch (filteredMsg[2]) {

                        // Action: FORWARD
                        case "0":
                            break;


                        // Action: TURN_LEFT
                        case "1":
                            break;


                        // Action: TURN_RIGHT
                        case "2":
                            break;


                        // Action: BACKWARDS
                        case "3":
                            break;


                        // Action: CALIBRATE
                        case "4":
                            break;


                        // Action: ERROR
                        case "5":
                            break;


                        // Action: STARTEXP
                        case "6":
                            break;


                        // Action: ENDEXP
                        case "7":
                            break;

                        // Action: STARTFAST
                        case "8":
                            break;


                        // Action: ENDFAST
                        case "9":
                            break;


                        // Action: STOP
                        case "10":
                            break;


                        // Action: ROBOT_POS
                        case "11":
                            break;


                        // Action: MD1
                        case "MD1":
                            break;


                        // Action: MD2
                        case "MD2":
                            break;
                    }
                }
            }

        }
    };

    public void setStartDirection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Robot Direction")
                .setItems(R.array.directions_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        // !!!! MATCH ROBOT DIRECTION ON ARRAY
                        mPGV.setRobotDirection(i);
                        dialog.dismiss();
                        Log.d(TAG, "Start Point Direction set.");
                    }
                });
        builder.create();
        builder.create().show();
    }

    public void onClickExploration(View view) {
        Toast.makeText(MainActivity.this, "Exploration start.", Toast.LENGTH_SHORT).show();

        // EXPLORATION CODE


        Toast.makeText(MainActivity.this, "Exploration completed.", Toast.LENGTH_SHORT).show();
    }

    public void onClickFastestPath(View view) {
        Toast.makeText(MainActivity.this, "Fastest Path start.", Toast.LENGTH_SHORT).show();

        // FASTEST PATH CODE

        Toast.makeText(MainActivity.this, "Fastest Path completed.", Toast.LENGTH_SHORT).show();
    }


    // Manual Mode; Update button
    public void onClickUpdate(View view) {

        Log.d(TAG, "Updating....");
        mPGV.refreshMap();
        Log.d(TAG, "Update completed!");
        Toast.makeText(MainActivity.this,"Update Completed.", Toast.LENGTH_SHORT).show();
    }


    // Delimiter for messages
    private String[] msgDelimiter(String message, String delimiter) {
        return (message.toLowerCase()).split(delimiter);
    }

    //BROADCAST RECEIVER FOR BLUETOOTH CONNECTION STATUS
    BroadcastReceiver btConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "Receiving btConnectionStatus Msg!!!");

            String connectionStatus = intent.getStringExtra("ConnectionStatus");
            myBTConnectionDevice = intent.getParcelableExtra("Device");
            //myBTConnectionDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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
