package com.cpar2.aperezm1.openclass.Receivers;

import android.content.Context;
import android.content.Intent;

import com.cpar2.aperezm1.openclass.Services.SDLService;
import com.smartdevicelink.transport.*;
import com.smartdevicelink.transport.SdlRouterService;

public class SdlReceiver extends SdlBroadcastReceiver
{

    public static final String RECONNECT_LANG_CHANGE = "RECONNECT_LANG_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Must call supr if onReceive is overriden.
        super.onReceive(context, intent);
        // TODO: This method is called when the BroadcastReceiver is receivin an Intent broadcast.

//        Log.v("SDLReceiver.onReceive()","Llego al Ppio");
//        if (intent != null)
//        {
//            String action = intent.getAction();
//            if (action != null)
//            {
//                Log.v("SDLReceiver.onReceive()", "ACTION " + action);
//
//                 //if SYNC connected to phone via bluetooth, start service (which starts proxy)
//                if  (action.compareTo(BluetoothDevice.ACTION_ACL_CONNECTED) == 0)
//                {
//                    Log.d("Receiver:ACL_CONNECTED", intent.getAction());
//                    SDLService serviceInstance = SDLService.getInstance();
//                    if (serviceInstance == null)
//                    {
//                        Intent startIntent = new Intent(context, SDLService.class);
//                        startIntent.putExtras(intent);
//                        context.startService(startIntent);
//                    }
//                }
//                else if (action.equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY))
//                {
//                    Log.d("Receiver:AUDIO_NOISY", intent.getAction());
//                    // signal your service to stop playback
//                }
//                else if(action.equalsIgnoreCase(TransportConstants.START_ROUTER_SERVICE_ACTION))
//                {
//                    if (intent.getBooleanExtra(RECONNECT_LANG_CHANGE, false))
//                    {
//                        onSdlEnabled(context, intent);
//                    }
//                }
//            }
//        }


        //throw new UnsupportedOperationException("Not yet implemented");
    }

    // En este método devolvemos la clase SdlRouterService que definimos como do nothing en SldRounterService que es una extensión de la clase
    //
    @Override
    public Class<? extends SdlRouterService> defineLocalSdlRouterClass()
    {
        return com.cpar2.aperezm1.openclass.Services.SdlRouterService.class;
    }

    @Override
    public void onSdlEnabled(Context context, Intent intent)
    {
        intent.setClass(context, SDLService.class);
        context.startService(intent);
    }
}
