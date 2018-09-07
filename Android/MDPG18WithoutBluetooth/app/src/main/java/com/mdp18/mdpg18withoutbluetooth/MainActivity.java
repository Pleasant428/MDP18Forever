package com.mdp18.mdpg18withoutbluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    TextView tv_status, tv_stringcmd;
    ImageButton fwd_btn, left_btn, right_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    EditText et_status = (EditText) findViewById(R.id.et_status);
    EditText et_stringcmd = (EditText) findViewById(R.id.et_stringcmd) ;
    et_stringcmd.setEnabled(false);
    et_status.setEnabled(false);

}
