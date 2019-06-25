package com.example.assignment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    Button button;
    Button button2;

    EditText ipText;
    EditText portText;
    EditText usernameText;
    AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        ipText = findViewById(R.id.ipText);
        portText = findViewById(R.id.portText);
        usernameText = findViewById(R.id.usernameText);
        adb = new AlertDialog.Builder(this);



        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (saveConfig()) {
                    Intent i = new Intent(MainActivity.this, TcpChatActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (saveConfig()) {
                    Intent i = new Intent(MainActivity.this, UdpChatActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

    }

    public boolean saveConfig() {
        if (ipText.getText().toString().matches("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})") && portText.getText().toString().matches("\\d*") && !usernameText.getText().toString().equals("")) {
            variables.ip = ipText.getText().toString();
            variables.port = Integer.parseInt(portText.getText().toString());
            variables.username = usernameText.getText().toString();
            return true;

        } else {
            adb.setTitle("Error");
            adb.setMessage("Your entered ip doesn't match a valid ip address or your entered port doesn't match a valid port number or no usernmae entered.");
            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {

                public void onClick(DialogInterface dialog, int id) {

                }

            });
            adb.show();

        }
        return false;
    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu (menu);
        MenuItem item1 = menu.add(0, 0, Menu.NONE, "Help");

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                adb.setTitle("Help");
                adb.setMessage("Enter a user name, ip address and port number and select whether to use TCP or UDP protocol. \nDefault  ip: 10.0.2.2 \nDefault port: UDP 5678 \nDefault port: TCP 4455");
                adb.setPositiveButton("close", new DialogInterface.OnClickListener()

                {

                    public void onClick(DialogInterface dialog, int id) {

                    }

                });
                adb.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
