package edu.vu.daytime;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class DayTimeActivity extends AppCompatActivity {

    // the logging tag is the name of the class
    private static final String TAG = DayTimeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate was invoked");

        setContentView(R.layout.activity_main);

        // find the textview called "hello"
        final TextView hello = (TextView)findViewById(R.id.hello);
        // set the text on it to "Hi there !!!"
        hello.setText("Hi there!!!");

        // find the button called "button1"
        final Button button1 = (Button)findViewById(R.id.button1);

        // set the text on it to "Click me!"
        button1.setText("Click me!");

        // provide an OnClickListener for the button
        button1.setOnClickListener(new View.OnClickListener() {

            // the Android framework will call this method when the button is clicked
            @Override
            public void onClick(View v) {

                // we create an AsyncTask which makes it easy to do all network operations on a
                // background thread, and handle the results on the UI thread
                AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

                    // this method will be called from a background thread. Note that network
                    // operations are allowed in here, but access to the UI's data structures is
                    // forbidden
                    @Override
                    protected String doInBackground(Void... params) {
                        try {

                            // we create a TCP connection to the "nist.netservicesgroup.com" server's
                            // port 13 (13 is the well-known port for the DAYTIME service)
                            Socket socket = new Socket("nist.netservicesgroup.com", 13);

                            // we get the socket's input stream, through which we can read from the
                            // remote service
                            InputStream in = socket.getInputStream();

                            // we can also get the output stream, through which we can send data
                            // to the remote service (we're not using it with the DAYTIME protocol)
                            OutputStream out = socket.getOutputStream();

                            // we create a string builder, in which we will store the received bytes
                            StringBuilder sb = new StringBuilder();


                            int i;
                            // read bytes from the socket until -1 is returned (which means the end
                            // of stream, i.e. that the remote service has closed the connection)
                            while ((i = in.read()) != -1) {
                                // we append the received byte to the string we're building
                                sb.append((char) i);
                            }

                            Log.i(TAG, "Received text:" + sb.toString());

                            // close the output stream: this will implicitly call socket.close(),
                            // which will cause the input stream to be closed, as well.
                            out.close();

                            // we return the string that we have read
                            return sb.toString();


                        } catch (UnknownHostException e) {
                            String error = "Unknown host: " + e.getMessage();
                            Log.e(TAG, error);
                            e.printStackTrace();
                            return error;
                        } catch (IOException e) {
                            String error = "I/O error communicating with server: " + e.getMessage();
                            Log.e(TAG, error);
                            e.printStackTrace();
                            return error;
                        }
                    }

                    // this method is called with the return value of doInBackground, and will be
                    // running on the UI thread, so accessing the UI's data structures is safe from
                    // here
                    @Override
                    protected void onPostExecute(String s) {
                        // set the text of the textview control to the string we received from the
                        // remote service. We trim it to remove extra whitespace from the beginning
                        // and from the end
                        hello.setText(s.trim());
                    }
                };

                // tell the framework to start executing the asynctask. Note that this call returns
                // immediately, and the task will start executing on a background thread
                // asynchronously
                task.execute();
            }
        });
    }
}
