package com.example.assignment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpChatActivity extends AppCompatActivity {

    DatagramSocket socket;
    EditText e;
    Button b;
    WebView w;

    SQLiteDatabase UdpDB;


    class SocketListener implements Runnable
    {
        public void run()
        {
            DatagramPacket packet;
            byte[] buf = new byte[256];

            try
            {
                while (true)
                {
                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String s = new String(packet.getData(), 0,packet.getLength());
                    Log.i("Log", ""+s);
                    updateDB("J>", ""+s+ "\n");
                    runOnUiThread (new Thread(new Runnable() {
                        public void run() {
                            loadDB();
                        }
                    }));
                }
            }
            catch (IOException e)
            {
                Log.e(getClass().getName(), e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_chat);


        File dbFile = new File(getFilesDir() + "/UdpDB.db");

        if (!dbFile.exists())
        {
            Log.i("SqlLiteExample", "File doesn't exist");

            UdpDB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/UdpDB.db", null);
            UdpDB.execSQL("create table chat (username text, messages text)");
        }else{
            Log.i("SqlLiteExample", "File does exist");
            UdpDB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/UdpDB.db", null);
        }

        loadDB();


        try
        {
            socket = new DatagramSocket();
        }
        catch (SocketException e1) {}

        e = (EditText) findViewById(R.id.editText);
        b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            String s = e.getText().toString();
                            updateDB(variables.username+"> ", ""+s);
                            byte[] buf = new byte[256];
                            buf = s.getBytes();

                            InetAddress address = InetAddress.getByName(variables.ip); // Change this to the IP address of your computer OR “10.0.2.2”(Gateway to 127.0.0.1 of host)
                            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, variables.port); //5678

                            socket.send(packet);
                        }
                        catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                        runOnUiThread (new Thread(new Runnable() {
                            public void run() {
                                loadDB();
                            }
                        }));
                    }
                }.start();

            }
        });

        Thread t = new Thread (new SocketListener ());
        t.start();

    }
    void loadDB(){
        SQLiteDatabase UdpDB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/UdpDB.db", null);

        Cursor cursor = UdpDB.rawQuery("select * from chat", null);

        int f = cursor.getColumnIndex("username");
        int l = cursor.getColumnIndex("messages");

        String records = "";

        if (cursor.moveToFirst()) {
            do {
                records = records + "<tr><td>" + cursor.getString(f) + "</td><td>" + cursor.getString(l) + "</td></tr>";
            }
            while (cursor.moveToNext());
        }

        UdpDB.close();

        w = (WebView) findViewById(R.id.webView);
        w.setVerticalScrollBarEnabled(true);
        w.loadData("<!DOCTYPE html><html><body><table>" + records + "</table></body></html>", "text/html", "UTF-8");
    }
   void updateDB (String username, String message)
    {
        CharSequence l = message;
        CharSequence f = username;
        UdpDB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/UdpDB.db", null);
        UdpDB.execSQL("insert into chat values ('" + f + "','" + l + "')");
        UdpDB.close();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu (menu);
        MenuItem item1 = menu.add(0, 0, Menu.NONE, "Home");

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                socket.close();
                Intent i1 = new Intent(this, MainActivity.class);
                startActivity(i1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
