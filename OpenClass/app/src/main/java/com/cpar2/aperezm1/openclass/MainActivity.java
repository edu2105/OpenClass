package com.cpar2.aperezm1.openclass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cpar2.aperezm1.openclass.Receivers.SdlReceiver;
import com.cpar2.aperezm1.openclass.Services.SDLService;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static
    {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTextViews();

        SdlReceiver.queryForConnectedService(this);

        //Hacemos Query Connextion. Pero además
        //Le forzamos una instancia del servicio cuando empieza el activity
        //Si no está para empezarlo y que lo vea Sync.
        SDLService serviceInstance = SDLService.getInstance();

        if (serviceInstance == null)
        {
            Intent proxyIntent = new Intent(this, SDLService.class);
            startService(proxyIntent);
        }


    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    /**
     * Method to initialize the main message on the screen
     */
    private void initTextViews() {
        TextView welcomeTextView = (TextView) findViewById(R.id.welcome_text_view);
        welcomeTextView.setText(R.string.welcome_text);

        TextView campusTextView = (TextView) findViewById(R.id.appLink_text_view);
        campusTextView.setText(R.string.campus_party_text);
    }

}
