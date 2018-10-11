package com.mdp18.group18android2018;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.nio.charset.Charset;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "MainActivity";

    PixelGridView mPGV;
    ImageButton forwardButton, leftRotateButton, rightRotateButton, reverseButton;
    Button btn_update, btn_sendToAlgo, btn_calibrate;
    TextView tv_status, tv_map_exploration, tv_mystatus, tv_mystringcmd;
    ToggleButton tb_setWaypointCoord, tb_setStartCoord, tb_autoManual, tb_fastestpath, tb_exploration;


    // FOR TILT SENSOR
    private SensorManager sensorManager;
    private Sensor sensor;
    boolean tiltNavi;
    Switch tiltBtn;


    // For bluetooth connection status
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice myBTConnectionDevice;
    static String connectedDevice;
    boolean connectedState;
    boolean currentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectedDevice = null;
        connectedState = false;
        currentActivity = true;

        //REGISTER BROADCAST RECEIVER FOR INCOMING MSG
        LocalBroadcastManager.getInstance(this).registerReceiver(btConnectionReceiver, new IntentFilter("btConnectionStatus"));

        //REGISTER BROADCAST RECEIVER FOR IMCOMING MSG
        LocalBroadcastManager.getInstance(this).registerReceiver(incomingMessageReceiver, new IntentFilter("IncomingMsg"));

        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_map_exploration = (TextView) findViewById(R.id.tv_map_exploration);

        tv_mystatus = (TextView) findViewById(R.id.tv_mystatus);
        tv_mystatus.setText("Stop\n");
        tv_mystatus.setMovementMethod(new ScrollingMovementMethod());

        tv_mystringcmd = (TextView) findViewById(R.id.tv_mystringcmd);
        tv_mystringcmd.setMovementMethod(new ScrollingMovementMethod());

        mPGV = findViewById(R.id.map);
        mPGV.initializeMap();

        btn_update = (Button) findViewById(R.id.btn_update);
        btn_update.setEnabled(false);


        //TILT
        tiltNavi = false;
        tiltBtn = findViewById(R.id.tiltSwitch);
        //declaring Sensor Manager and sensor type
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        // Forward button
        forwardButton = (ImageButton) findViewById(R.id.fwd_btn);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check BT connection If not connected to any bluetooth device
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    //If already connected to a bluetooth device
                    String navigate = "And|Ard|0|";
                    byte[] bytes = navigate.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    Log.d(TAG, "Android Controller: Move Forward sent");
                    tv_mystatus.append("Moving\n");
                    tv_mystringcmd.append("Android Controller: Move Forward\n");
                    mPGV.moveForward();
                }
            }
        });

        leftRotateButton = (ImageButton) findViewById(R.id.left_btn);
        leftRotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check BT connectionIf not connected to any bluetooth device
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    //If already connected to a bluetooth device
                    String navigate = "And|Ard|1|";
                    byte[] bytes = navigate.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    Log.d(TAG, "Android Controller: Turn Left sent");
                    tv_mystatus.append("Moving\n");
                    tv_mystringcmd.append("Android Controller: Turn Left\n");
                    mPGV.rotateLeft();
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
                    tv_mystatus.append("Moving\n");
                    tv_mystringcmd.append("Android Controller: Turn Right\n");
                    mPGV.rotateRight();
                }
            }
        });

        reverseButton = (ImageButton) findViewById(R.id.rev_btn);
        reverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check BT connectionIf not connected to any bluetooth device
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    //If already connected to a bluetooth device
                    String navigate = "And|Ard|3|";
                    byte[] bytes = navigate.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    Log.d(TAG, "Android Controller: Move Backwards sent");
                    tv_mystatus.append("Moving\n");
                    tv_mystringcmd.append("Android Controller: Move Backwards\n");
                    mPGV.moveBackwards();
                }
            }
        });

        tb_setWaypointCoord = (ToggleButton) findViewById(R.id.tb_setWaypointCoord);
        tb_setWaypointCoord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Check BT connectionIf not connected to any bluetooth device
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    if (isChecked) {
                        // The toggle is enabled : To select waypoint on map
                        mPGV.selectWayPoint();
                        tb_setWaypointCoord.toggle();
                    }
                }
            }
        });
        tb_setStartCoord = (ToggleButton) findViewById(R.id.tb_setStartCoord);
        tb_setStartCoord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Check BT connectionIf not connected to any bluetooth device
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        // The toggle is enabled: To select start point on map
                        mPGV.selectStartPoint();
                        setStartDirection();
                        tb_setStartCoord.toggle();
                    }
                }
            }
        });


        // To send start coordinates and waypoint coordinates to Algo
        btn_sendToAlgo = (Button) findViewById(R.id.btn_sendToAlgo);
        btn_sendToAlgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check BT connectionIf not connected to any bluetooth device
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    //If already connected to a bluetooth device
                    // Send both coordinates to Algo as one string
                    int convertDirection = mPGV.getRobotDirection();
//                    int convertedDirection = mPGV.convertRobotDirectionForAlgo(convertDirection);
                    String sendAlgoCoord = "And|Alg|10|".concat(Integer.toString(mPGV.getStartCoord()[0])).concat(",").concat(Integer.toString(mPGV.getStartCoord()[1])).concat(",").concat(Integer.toString(convertDirection)).concat(",").concat(Integer.toString(mPGV.getWayPoint()[0])).concat(",").concat(Integer.toString(mPGV.getWayPoint()[1]));
                    byte[] bytes = sendAlgoCoord.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    Log.d(TAG, "Sent Start and Waypoint Coordinates to Algo");
                    Toast.makeText(MainActivity.this, "Start & Waypoint coordinates sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // To send start coordinates and waypoint coordinates to Algo
        btn_calibrate = (Button) findViewById(R.id.btn_calibrateRobot);
        btn_calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check BT connectionIf not connected to any bluetooth device
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {

                    //If already connected to a bluetooth device
                    String navigate = "And|Alg|C|";
                    byte[] bytes = navigate.getBytes(Charset.defaultCharset());
                    BluetoothChat.writeMsg(bytes);
                    Log.d(TAG, "Android Controller: Calibrate sent");

                    tv_mystatus.append("Calibrating robot...\n");
                    tv_mystringcmd.append("Calibrating robot...\n");
                }
            }
        });

        tb_autoManual = (ToggleButton) findViewById(R.id.tb_autoManual);
        tb_autoManual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        // The toggle is enabled; Manual Mode
                        btn_update.setEnabled(true);
                        forwardButton.setEnabled(false);
                        leftRotateButton.setEnabled(false);
                        rightRotateButton.setEnabled(false);
                        reverseButton.setEnabled(false);
                        Toast.makeText(MainActivity.this, "Manual Mode enabled", Toast.LENGTH_SHORT).show();
//                        updateMap = false;
                        mPGV.setAutoUpdate(false);
                        Log.d(TAG, "Auto updates disabled.");

                    } else {
                        // The toggle is disabled; Auto Mode
                        mPGV.refreshMap(true);
                        btn_update.setEnabled(false);
                        forwardButton.setEnabled(true);
                        leftRotateButton.setEnabled(true);
                        rightRotateButton.setEnabled(true);
                        reverseButton.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Auto Mode enabled", Toast.LENGTH_SHORT).show();
                        mPGV.setAutoUpdate(true);
                        Log.d(TAG, "Auto updates enabled.");
                    }
                }
            }
        });

        tb_exploration = (ToggleButton) findViewById(R.id.tb_exploration);
        tb_exploration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        // The toggle is enabled; Start Exploration Mode
                        startExploration();
                    }
                }
            }
        });

        tb_fastestpath = (ToggleButton) findViewById(R.id.tb_fastestpath);
        tb_fastestpath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Check BT connectionIf not connected to any bluetooth device
                if (connectedDevice == null) {
                    Toast.makeText(MainActivity.this, "Please connect to bluetooth device first!", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        // The toggle is enabled; Start Fastest Path Mode
                        startFastestPath();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu if present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_bluetoothconnect:
                startActivity(new Intent(MainActivity.this, BluetoothConnect.class));
                return true;

            case R.id.btn_reconfig:
                startActivity(new Intent(MainActivity.this, ReconfigStringCommand.class));
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

            Log.d(TAG, "Receiving incoming message: " + incomingMsg);
            tv_mystringcmd.append(incomingMsg + "\n");

            // Filter empty and concatenated string from receiving channel
            if (incomingMsg.length() > 8 && incomingMsg.length() < 345) {

                // Check if string is for android
                if (incomingMsg.substring(4, 7).equals("And")) {

                    String[] filteredMsg = msgDelimiter(incomingMsg.replaceAll("\\,", "\\|").trim(), "\\|");

                    // Message: Action
                    Log.d(TAG, "Incoming Message filtered: " + filteredMsg[2]);


                    switch (filteredMsg[2]) {

                        // Action: FORWARD
                        case "0":
                            for (int counter = Integer.parseInt(filteredMsg[3]); counter >= 1; counter--) {
                                mPGV.moveForward();
                                tv_mystringcmd.append("Move Forward\n");
                                tv_mystatus.append("Moving\n");
                            }
                            break;


                        // Action: TURN_LEFT
                        case "1":

                            for (int counter = Integer.parseInt(filteredMsg[3]); counter >= 1; counter--) {
                                mPGV.rotateLeft();
                                tv_mystringcmd.append("Turn Left\n");
                                tv_mystatus.append("Moving\n");
                            }
                                break;


                        // Action: TURN_RIGHT
                        case "2":
                            for (int counter = Integer.parseInt(filteredMsg[3]); counter >= 1; counter--) {
                                mPGV.rotateRight();
                                tv_mystringcmd.append("Turn Right\n");
                                tv_mystatus.append("Moving\n");
                            }
                            break;


                        // Action: BACKWARDS
                        case "3":
                            for (int counter = Integer.parseInt(filteredMsg[3]); counter >= 1; counter--) {
                                mPGV.moveBackwards();
                                tv_mystringcmd.append("Move Backwards\n");
                                tv_mystatus.append("Moving\n");
                            }
                            break;


                        // Action: CALIBRATE : ALIGN_FRONT
                        case "4":
                        case "ALIGN_FRONT":
                            tv_mystatus.append("Calibrating robot...\n");
                            tv_mystringcmd.append("Calibrating robot...\n");
                            break;



                        // Action: CALIBRATE : ALIGN_RIGHT
                        case "5":
                            tv_mystatus.append("Calibrating robot...\n");
                            tv_mystringcmd.append("Calibrating robot...\n");
                            break;

                        // Action: ENDEXP
                        case "8":
                        case "ENDEXP":
                            endExploration();
                            break;


                        // Action: ENDFAST
                        case "9":
                        case "ENDFAST":
                            endFastestPath();

                            break;

                        // Action: MD1
                        case "md1":
                            String mapDes1 = filteredMsg[3];
                            mPGV.mapDescriptorExplored(mapDes1);
                            break;


                        // Action: MD2
                        case "md2":
                            String mapDes2 = filteredMsg[3];
                            mPGV.mapDescriptorObstacle(mapDes2);
                            break;

                        case "s":
                            // Sent by Arduino: robot stop
                            tv_mystatus.append("Stop\n");
                            tv_mystringcmd.append(" \n");
                            break;

                        case "a":
                            // Sent by Algo: Upwards arrow on coordinate
                            // Format: Rpi|And|A|
                            tv_mystatus.append("Upwards arrow detected\n");
                            mPGV.setArrowImageCoord();
                            mPGV.refreshMap(mPGV.getAutoUpdate());
                            break;

                        default:
                            Log.d(TAG, "Switch Case default! String command not recognised.");
                            break;
                    }

                    if (filteredMsg.length >= 5){

                        Log.d(TAG, "Incoming Message 2: " + filteredMsg[4] + "|" + filteredMsg[5] + "|" + filteredMsg[6] + "|" + filteredMsg[7]);

                        if (filteredMsg[5].equals("and")) {

                            Log.d(TAG, "Incoming Message 2 FOR ANDROID (went past IF2): " + filteredMsg[5]);

                            Log.d(TAG, "Incoming Message 2 filtered: " + filteredMsg[6]);

                            switch (filteredMsg[6]) {

                                // Action: FORWARD
                                case "0":
                                    for (int counter = Integer.parseInt(filteredMsg[7]); counter >= 1; counter--) {
                                        mPGV.moveForward();
                                        tv_mystringcmd.append("Move Forward\n");
                                        tv_mystatus.append("Moving\n");
                                    }
                                    break;


                                // Action: TURN_LEFT
                                case "1":

                                    for (int counter = Integer.parseInt(filteredMsg[7]); counter >= 1; counter--) {
                                        mPGV.rotateLeft();
                                        tv_mystringcmd.append("Turn Left\n");
                                        tv_mystatus.append("Moving\n");
                                    }
                                    break;


                                // Action: TURN_RIGHT
                                case "2":
                                    for (int counter = Integer.parseInt(filteredMsg[7]); counter >= 1; counter--) {
                                        mPGV.rotateRight();
                                        tv_mystringcmd.append("Turn Right\n");
                                        tv_mystatus.append("Moving\n");
                                    }
                                    break;


                                // Action: BACKWARDS
                                case "3":
                                    for (int counter = Integer.parseInt(filteredMsg[7]); counter >= 1; counter--) {
                                        mPGV.moveBackwards();
                                        tv_mystringcmd.append("Move Backwards\n");
                                        tv_mystatus.append("Moving\n");
                                    }
                                    break;


                                // Action: CALIBRATE : ALIGN_FRONT
                                case "4":
                                case "ALIGN_FRONT":
                                    tv_mystatus.append("Calibrating robot...\n");
                                    tv_mystringcmd.append("Calibrating robot...\n");
                                    break;



                                // Action: CALIBRATE : ALIGN_RIGHT
                                case "5":
                                    tv_mystatus.append("Calibrating robot...\n");
                                    tv_mystringcmd.append("Calibrating robot...\n");
                                    break;

                                // Action: ENDEXP
                                case "8":
                                case "ENDEXP":
                                    endExploration();
                                    break;


                                // Action: ENDFAST
                                case "9":
                                case "ENDFAST":
                                    endFastestPath();
                                    break;

                                // Action: MD1
                                case "md1":
                                    String mapDes1 = filteredMsg[7];
                                    mPGV.mapDescriptorExplored(mapDes1);
                                    break;


                                // Action: MD2
                                case "md2":
                                    String mapDes2 = filteredMsg[7];
                                    mPGV.mapDescriptorObstacle(mapDes2);
                                    break;

                                case "s":
                                    // Sent by Arduino: robot stop
                                    tv_mystatus.append("Stop\n");
                                    tv_mystringcmd.append(" \n");
                                    break;

                                case "a":
                                    // Sent by Algo: Upwards arrow on coordinate
                                    // Format: Rpi|And|A|
                                    tv_mystatus.append("Upwards arrow detected\n");
                                    mPGV.setArrowImageCoord();
                                    mPGV.refreshMap(mPGV.getAutoUpdate());
                                    break;

                                default:
                                    Log.d(TAG, "Switch Case default! String command not recognised.");
                                    break;
                            }
                   }

                    }
                }

                // The following is for clearing checklist commands only.

                // For receiving AMD robotPosition and grid
                if (incomingMsg.substring(0, 1).equals("{")) {
                    Log.d(TAG, "Incoming Message from AMD: " + incomingMsg);
                    String[] filteredMsg = msgDelimiter(incomingMsg.replaceAll(" ", "").replaceAll(",", "\\|").replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\:", "\\|").replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").trim(), "\\|");
                    Log.d(TAG, "filteredMsg: " + filteredMsg);
                    if (filteredMsg[0].equals("robotposition")) {
                        int robotPosCol = Integer.parseInt(filteredMsg[1]) + 1;
                        int robotPosRow = 19 - (Integer.parseInt(filteredMsg[2]) + 1);
                        int robotPosDeg = Integer.parseInt(filteredMsg[3]);
                        int robotPosDir = 0;
                        // Up
                        if (robotPosDeg == 0)
                            robotPosDir = 0;
                        //Right
                        else if (robotPosDeg == 90)
                            robotPosDir = 3;
                        //Down
                        else if (robotPosDeg == 180)
                            robotPosDir = 2;
                        // Left
                        else if (robotPosDeg == 270)
                            robotPosDir = 1;
                        // For setting robot start position from AMD
                        mPGV.setCurPos(robotPosRow, robotPosCol);
                        mPGV.setRobotDirection(robotPosDir);
                    } else if (filteredMsg[0].equals("grid")) {
                        String mdAMD = filteredMsg[1];
                        mPGV.mapDescriptorChecklist(mdAMD);
                        mPGV.refreshMap(mPGV.getAutoUpdate());
                        Log.d(TAG, "mdAMD: " + mdAMD);

                        // For setting up map from received AMD MDF String, use mdAMD
                        Log.d(TAG, "Processing mdAMD...");
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
                        mPGV.setRobotDirection(i);
                        dialog.dismiss();
                        Log.d(TAG, "Start Point Direction set");
                    }
                });
        builder.create();
        builder.create().show();
    }

    public void startExploration() {
        Toast.makeText(MainActivity.this, "Exploration started", Toast.LENGTH_SHORT).show();
        String startExp = "And|Alg|11|";
        byte[] bytes = startExp.getBytes(Charset.defaultCharset());
        BluetoothChat.writeMsg(bytes);
        Log.d(TAG, "Android Controller: Start Exploration");
        tv_mystringcmd.append("Start Exploration\n");
        tv_mystatus.append("Moving\n");
    }

    public void endExploration() {
        Log.d(TAG, "Algorithm: End Exploration");
        tv_mystringcmd.append("End Exploration\n");
        tv_mystatus.append("Stop\n");
        Toast.makeText(MainActivity.this, "Exploration ended", Toast.LENGTH_SHORT).show();
    }

    public void startFastestPath() {
        Toast.makeText(MainActivity.this, "Fastest Path started", Toast.LENGTH_SHORT).show();
        String startFP = "And|Alg|12|";
        byte[] bytes = startFP.getBytes(Charset.defaultCharset());
        BluetoothChat.writeMsg(bytes);
        Log.d(TAG, "Android Controller: Start Fastest Path");
        tv_mystringcmd.append("Start Fastest Path\n");
        tv_mystatus.append("Moving\n");
    }

    public void endFastestPath() {
        Log.d(TAG, "Algorithm: Fastest Path Ended.");
        tv_mystringcmd.append("End Fastest Path\n");
        tv_mystatus.append("Stop\n");
        Toast.makeText(MainActivity.this, "Fastest Path ended", Toast.LENGTH_SHORT).show();
    }

    // Manual Mode; Update button
    public void onClickUpdate(View view) {
        Log.d(TAG, "Updating....");
        mPGV.refreshMap(true);
        Log.d(TAG, "Update completed!");
        Toast.makeText(MainActivity.this, "Update completed", Toast.LENGTH_SHORT).show();
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
            if (connectionStatus.equals("disconnect")) {

                Log.d("MainActivity:", "Device Disconnected");
                connectedDevice = null;
                connectedState = false;

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



    // For checklist only
    // EXTENSION BEYOND THE BASICS
    // Tilt Control

    /*
     ONCLICKLISTENER FOR TILT BUTTON
    */
    public void onClickTiltSwitch(View view) {
        tiltBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tiltNavi = true;
                    Log.d(TAG, "Tilt Switch On!");

                } else {
                    tiltNavi = false;
                    Log.d(TAG, "Tilt Switch Off!");


                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        if (tiltNavi == true) {

        if (Math.abs(x) > Math.abs(y)) {
            if (x < 0) {
                Log.d("MainActivity:", "RIGHT TILT!!");

                tv_mystatus.append("Moving\n");
                tv_mystringcmd.append("Android Controller: Turn Right\n");
                mPGV.rotateRight();
            }
            if (x > 0) {
                Log.d("MainActivity:", "LEFT TILT!!");

                tv_mystatus.append("Moving\n");
                tv_mystringcmd.append("Android Controller: Turn Left\n");
                mPGV.rotateLeft();
            }
        } else {
            if (y < 0) {
                Log.d("MainActivity:", "UP TILT!!");

                tv_mystatus.append("Moving\n");
                tv_mystringcmd.append("Android Controller: Move Forward\n");
                mPGV.moveForward();
            }
            if (y > 0) {
                Log.d("MainActivity:", "DOWN TILT!!");

                tv_mystatus.append("Moving\n");
                tv_mystringcmd.append("Android Controller: Move Backwards\n");
                mPGV.moveBackwards();
            }
        }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister Sensor listener
        sensorManager.unregisterListener(this);
    }

}

