package com.mad.web_socket_client.app;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.JSONObject;
import org.json.JSONException;






public class MainActivity extends ActionBarActivity {


    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CreateSocketTask().execute();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* AsyncTask that runs as a worker thread,simultaneously with main thread
       we do this because we cannot run a NetworkIO (like a socket connection)
       in the main thread, so we create a AsycnTask extended class to do it for
       us in the background...
     */
    public class CreateSocketTask extends AsyncTask<String, Integer, Integer> {

        // method that we run in the background
        protected Integer doInBackground(String... strings) {
            String name = "191.238.232.145";
            int port = 5000;
            try {

                // create new socket //
                Socket socket = new Socket(name, port);

                // get mainActivity Context for toast, TextView to print output string
                Context context = MainActivity.this;
                TextView text = (TextView) findViewById(R.id.text);

                // create JSONObject
                JSONObject json = new JSONObject();
                // try::catch put a message in JSONObject
                try {
                    json.put("A", "Hello, Josh!");
                }
                catch(JSONException a) {
                    // value of 2 means Toast reads JSONException
                    return 2;
                }
                //-- try::catch sending JSON via OutputStreamWriter --//
                try{
                    // create OutputStreamWriter to read JSON string into
                    OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);

                    // Parse JSON to string
                    String json_string = json.toString();
                    // add padding, this is how Server obtains length of JSON String
                    String sho = String.format("%04d", json_string.length()) + json_string;
                    // put JSON string w/ padding into output stream
                    output.write(sho);
                    text.setText(sho);
                    // flush and close to send message to server
                    output.flush();
                }
                catch(IOException a) {
                    Toast.makeText(context, "No Json Sent", Toast.LENGTH_LONG).show();
                }

                // -- try:catch recieving JSON via InputStream --//
                try {

                    // create a DataInputStream to read in bytes
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    int length = inputStream.readInt();
                    int pid = inputStream.readInt();
                    byte[] json_string = new byte[length];
                    int buffer_length = inputStream.read(json_string);

                    // Set the diff textViews so we can see what we got
                    TextView text1 = (TextView) findViewById(R.id.text1);
                    text1.setText(((Integer) length).toString());
                    TextView text2 = (TextView) findViewById(R.id.text2);
                    text2.setText(((Integer) pid).toString());
                    TextView text3 = (TextView) findViewById(R.id.text3);
                    text3.setText(json_string.toString());



                } catch (IOException a) {
                    return 0;
                }
                // close the socket
                socket.close();
                // 1 for connection was successful
                return 1;

            } catch (IOException a) {
                // 0 for connection unsuccessful
                return 0;
            }
        }

        // a few toast messages based on the result of our background thread to let us know
        // what went wrong, if something went wrong.
        protected void onPostExecute(Integer result) {
            if(result == 0) {
                Context context = MainActivity.this;
                Toast.makeText(context, "No Connection", Toast.LENGTH_LONG).show();
            }
            else if(result == 1) {
                Context context = MainActivity.this;
                Toast.makeText(context, "YES Connection", Toast.LENGTH_LONG).show();
            }
            else if(result == 2) {
                Context context = MainActivity.this;
                Toast.makeText(context, "JSON Exception thrown", Toast.LENGTH_LONG).show();
            }
        }
    }
}
