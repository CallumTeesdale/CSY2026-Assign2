package com.example.assignment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class TcpChatActivity extends AppCompatActivity
{
    public Socket sender;
    public BufferedReader br;
    public PrintStream bw;

    EditText e;
    Button b;
    WebView w;

    SQLiteDatabase TcpDB;

    int portNumber;
    String ipAddress;

    class SocketListener implements Runnable
    {
        public void run()
        {
            try
            {
                sender = new Socket (ipAddress, portNumber);
                br = new BufferedReader (new InputStreamReader(sender.getInputStream()));
                bw = new PrintStream (sender.getOutputStream());
                updateDB("Server> ","---Connected---");

                while (true)
                {
                    String s =  br.readLine ();
                    updateDB("",""+s);

                    w.post(new Runnable()
                           {
                               public void run()
                               {
                                   loadDB();
                               }
                           }
                    );
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
        setContentView(R.layout.activity_tcp_chat);


        e = (EditText) findViewById(R.id.editText);


        portNumber = variables.port; //4455
        ipAddress = variables.ip; // Change this to the IP address of your computer OR “10.0.2.2”(Gateway to 127.0.0.1 of host)


        File dbFile = new File(getFilesDir() + "/TcpDB.db");

        if (!dbFile.exists())
        {
            Log.i("SqlLiteExample", "File doesn't exist");

            TcpDB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/TcpDB.db", null);
            TcpDB.execSQL("create table chat (username text, messages text)");
        }else{
            Log.i("SqlLiteExample", "File does exist");
            TcpDB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/TcpDB.db", null);
        }

        loadDB();



        b = (Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new Thread()
                {
                    public void run()
                    {
                        String but = e.getText().toString();
                        bw.println (but);
                      //  updateDB(variables.ip+"> ","Button "+but);
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
        SQLiteDatabase TcpDB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/TcpDB.db", null);

        Cursor cursor = TcpDB.rawQuery("select * from chat", null);

        int f = cursor.getColumnIndex("username");
        int l = cursor.getColumnIndex("messages");

        String records = "";

        if (cursor.moveToFirst()) {
            do {
                records = records + "<tr><td>" + cursor.getString(f) + "</td><td>" + cursor.getString(l) + "</td></tr>";
            }
            while (cursor.moveToNext());
        }

        TcpDB.close();

        w = (WebView) findViewById(R.id.webView);
        w.setVerticalScrollBarEnabled(true);
        w.loadData("<!DOCTYPE html><html><body><table>" + records + "</table></body></html>", "text/html", "UTF-8");
    }
    void updateDB (String username, String message)
    {
        CharSequence l = message;
        CharSequence f = username;
        TcpDB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/TcpDB.db", null);
        TcpDB.execSQL("insert into chat values ('" + f + "','" + l + "')");
        TcpDB.close();
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
                try {
                    sender.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Intent i1 = new Intent(this, MainActivity.class);
                startActivity(i1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}