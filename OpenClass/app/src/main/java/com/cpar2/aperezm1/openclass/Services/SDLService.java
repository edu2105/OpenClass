package com.cpar2.aperezm1.openclass.Services;



import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.cpar2.aperezm1.openclass.R;
import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetWayPointsResponse;
import com.smartdevicelink.proxy.rpc.Image;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.OnWayPointChange;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.Show;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SoftButton;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.ImageType;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SoftButtonType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class SDLService extends Service implements IProxyListenerALM
{

    // AppID an AppName are bind by Ford.
    // When an AppID is requested to Ford it will also register the AppName
    // Both must match for the App to Work.

    private static final String APP_NAME 	= "Campus Party";
    private static final String APP_ID 	    = "4055201279";
    private static final String MAIN_IMG    = "mustang.png";

    private int WELCOME_SPEECH_ID=0;
    private static final String WELCOME_SPEECH = "Welcome to Campus Party A.R. 2... - Ford AppLink Open Class";
    private int autoIncCorrId=0;
    private boolean firstTime=true;


    private int MAIN_LAYOUT_ID=0;
    private int SHOW_WELCOME_ID=0;
    private int MAIN_IMG_ID=0;

    private static final int BUTTON_ID1  = 2000;
    private static final int BUTTON_ID2  = 3000;



    SdlProxyALM proxy = null;

    private static SDLService instance = null;


    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (proxy == null)
        {
            try
            {
                // Create a new proxy using Bluetooth transport
                // The listener, AppName, whether or not it is a media app
                // and the AppID are supplied.
                proxy = new SdlProxyALM(this.getBaseContext(),this, APP_NAME, false, APP_ID);
            }
            catch (SdlException e)
            {
                //There was an error creating the proxy
                if (proxy == null)
                {
                    stopSelf();
                }
            }
        }
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy()
    {

        //Destroy the Proxy if there's a valid proxy available.
        if (proxy != null)
        {
            try
            {
                proxy.dispose();
            }
            catch (SdlException e)
            {
                e.printStackTrace();
            }
            finally
            {
                proxy = null;
            }
        }

    }

    @Override
    public void onProxyClosed(String info, Exception e, SdlDisconnectedReason reason)
    {
        stopSelf();
    }



    public SDLService()
    {
        firstTime = true;
        instance = this;
    }

    public static SDLService getInstance()
    {
        return instance;
    }

    @Override
    public void onOnHMIStatus(OnHMIStatus notification)
    {

        Log.v("OnHMIStatus","Enters OnHMIStatus " + " | " + notification.toString() + " | " + notification.getHmiLevel());
        switch(notification.getHmiLevel())
        {
            //Case when App is being used by the user and is displayed in the
            //Vehicle's head unit as main App.
            case HMI_FULL:


                if(notification.getFirstRun())
                {
                    performMainShow();
                }
//                if(firstTime)
//                {
//                    firstTime = false;
//
//                    WELCOME_SPEECH_ID= autoIncCorrId++;
//                    try
//                    {
//                        proxy.speak(WELCOME_SPEECH,WELCOME_SPEECH_ID);
//                        Log.v("HMI_FULL","Calls Welcome Speach");
//                    }
//                    catch (SdlException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
                break;

            //Case when App has been closed from the head unit's menu.
            //Vehicle's head unit as main App.
            case HMI_NONE:
                break;

            //Case when App is not in the Head unit's foreground.
            case HMI_BACKGROUND:

                if(notification.getFirstRun())
                {
                    changeLayout();
                }
                break;
        }


    }

    private void changeLayout()
    {
            MAIN_LAYOUT_ID = autoIncCorrId++;
            MAIN_IMG_ID = autoIncCorrId++;

            uploadImage(R.mipmap.mustang,MAIN_IMG,MAIN_IMG_ID,true);
            //uploadImage(R.mipmap.manolo,MAIN_IMG,MAIN_IMG_ID,true);

            try
            {
                proxy.setappicon(MAIN_IMG, autoIncCorrId++);
                //proxy.setdisplaylayout(" TEXT_AND_SOFTBUTTONS_WITH_GRAPHIC", MAIN_LAYOUT_ID);
                proxy.setdisplaylayout(" NON-MEDIA_WITH_SOFT_BUTTONS", MAIN_LAYOUT_ID);
            }
            catch (SdlException e)
            {
                e.printStackTrace();
            }
    }


    private void performMainShow()
    {

       //Set the welcome message on screen
        SHOW_WELCOME_ID = autoIncCorrId++;
        Show newShow = new Show();
        newShow.setCorrelationID(SHOW_WELCOME_ID);

        Image newImage = new Image();
        newImage.setValue(MAIN_IMG);
        newImage.setImageType(ImageType.DYNAMIC);
        newShow.setGraphic(newImage);

        newShow.setMainField1("Ford Open Class");
        newShow.setMainField2("RPM");
        newShow.setMainField3("Temperatura Interior");

        SoftButton newSB1 = new SoftButton();
        newSB1.setSoftButtonID(BUTTON_ID1);
        newSB1.setType(SoftButtonType.SBT_TEXT);
        newSB1.setText("RPM");

        SoftButton newSB2 = new SoftButton();
        newSB2.setSoftButtonID(BUTTON_ID2);
        newSB2.setType(SoftButtonType.SBT_TEXT);
        newSB2.setText("Fuel Lvl");

        Vector<SoftButton> softButtons = new Vector<SoftButton>();
        softButtons.add(newSB1);
        softButtons.add(newSB2);

        newShow.setSoftButtons(softButtons);

        WELCOME_SPEECH_ID = autoIncCorrId++;

        try
        {
            proxy.sendRPCRequest(newShow);
            proxy.speak(WELCOME_SPEECH,WELCOME_SPEECH_ID);
        }

        catch (SdlException e)
        {
            e.printStackTrace();
        }
    }


    private void uploadImage(int resource, String imageName,int correlationId, boolean isPersistent)
    {
        PutFile putFile = new PutFile();
        putFile.setFileType(FileType.GRAPHIC_PNG);
        putFile.setSdlFileName(imageName);
        putFile.setCorrelationID(correlationId);
        putFile.setPersistentFile(isPersistent);
        putFile.setSystemFile(false);
        putFile.setBulkData(contentsOfResource(resource));

        try {
            proxy.sendRPCRequest(putFile);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }


    /**
     * EN: Helper method to take resource files and turn them into byte arrays
     * @param resource Resource file id.
     * @return Resulting byte array.
     */
    private byte[] contentsOfResource(int resource)
    {
        InputStream is = null;
        try
        {
            is = getResources().openRawResource(resource);
            ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
            final int bufferSize = 4096;
            final byte[] buffer = new byte[bufferSize];
            int available;
            while ((available = is.read(buffer)) >= 0)
            {
                os.write(buffer, 0, available);
            }
            return os.toByteArray();
        }
        catch (IOException e)
        {
            Log.w("SDL Service", "Can't read icon file", e);
            return null;
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onServiceEnded(OnServiceEnded serviceEnded)
    {

    }

    @Override
    public void onServiceNACKed(OnServiceNACKed serviceNACKed) {

    }

    @Override
    public void onOnStreamRPC(OnStreamRPC notification) {

    }

    @Override
    public void onStreamRPCResponse(StreamRPCResponse response) {

    }

    @Override
    public void onError(String info, Exception e) {

    }

    @Override
    public void onGenericResponse(GenericResponse response) {

    }

    @Override
    public void onOnCommand(OnCommand notification) {

    }

    @Override
    public void onAddCommandResponse(AddCommandResponse response) {

    }

    @Override
    public void onAddSubMenuResponse(AddSubMenuResponse response) {

    }

    @Override
    public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {

    }

    @Override
    public void onAlertResponse(AlertResponse response) {

    }

    @Override
    public void onDeleteCommandResponse(DeleteCommandResponse response) {

    }

    @Override
    public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {

    }

    @Override
    public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {

    }

    @Override
    public void onPerformInteractionResponse(PerformInteractionResponse response) {

    }

    @Override
    public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse response) {

    }

    @Override
    public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {

    }

    @Override
    public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {

    }

    @Override
    public void onShowResponse(ShowResponse response) {

    }

    @Override
    public void onSpeakResponse(SpeakResponse response) {

    }

    @Override
    public void onOnButtonEvent(OnButtonEvent notification) {

    }

    @Override
    public void onOnButtonPress(OnButtonPress notification) {

    }

    @Override
    public void onSubscribeButtonResponse(SubscribeButtonResponse response) {

    }

    @Override
    public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {

    }

    @Override
    public void onOnPermissionsChange(OnPermissionsChange notification) {

    }

    @Override
    public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {

    }

    @Override
    public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse response) {

    }

    @Override
    public void onGetVehicleDataResponse(GetVehicleDataResponse response) {

    }

    @Override
    public void onOnVehicleData(OnVehicleData notification) {

    }

    @Override
    public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {

    }

    @Override
    public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {

    }

    @Override
    public void onOnAudioPassThru(OnAudioPassThru notification) {

    }

    @Override
    public void onPutFileResponse(PutFileResponse response) {

    }

    @Override
    public void onDeleteFileResponse(DeleteFileResponse response) {

    }

    @Override
    public void onListFilesResponse(ListFilesResponse response) {

    }

    @Override
    public void onSetAppIconResponse(SetAppIconResponse response) {

    }

    @Override
    public void onScrollableMessageResponse(ScrollableMessageResponse response) {

    }

    @Override
    public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {

    }

    @Override
    public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {

    }

    @Override
    public void onOnLanguageChange(OnLanguageChange notification) {

    }

    @Override
    public void onOnHashChange(OnHashChange notification) {

    }

    @Override
    public void onSliderResponse(SliderResponse response) {

    }

    @Override
    public void onOnDriverDistraction(OnDriverDistraction notification) {

    }

    @Override
    public void onOnTBTClientState(OnTBTClientState notification) {

    }

    @Override
    public void onOnSystemRequest(OnSystemRequest notification) {

    }

    @Override
    public void onSystemRequestResponse(SystemRequestResponse response) {

    }

    @Override
    public void onOnKeyboardInput(OnKeyboardInput notification) {

    }

    @Override
    public void onOnTouchEvent(OnTouchEvent notification) {

    }

    @Override
    public void onDiagnosticMessageResponse(DiagnosticMessageResponse response) {

    }

    @Override
    public void onReadDIDResponse(ReadDIDResponse response) {

    }

    @Override
    public void onGetDTCsResponse(GetDTCsResponse response) {

    }

    @Override
    public void onOnLockScreenNotification(OnLockScreenStatus notification) {

    }

    @Override
    public void onDialNumberResponse(DialNumberResponse response) {

    }

    @Override
    public void onSendLocationResponse(SendLocationResponse response) {

    }

    @Override
    public void onShowConstantTbtResponse(ShowConstantTbtResponse response) {

    }

    @Override
    public void onAlertManeuverResponse(AlertManeuverResponse response) {

    }

    @Override
    public void onUpdateTurnListResponse(UpdateTurnListResponse response) {

    }

    @Override
    public void onServiceDataACK(int dataSize) {

    }

    @Override
    public void onGetWayPointsResponse(GetWayPointsResponse response) {

    }

    @Override
    public void onSubscribeWayPointsResponse(SubscribeWayPointsResponse response) {

    }

    @Override
    public void onUnsubscribeWayPointsResponse(UnsubscribeWayPointsResponse response) {

    }

    @Override
    public void onOnWayPointChange(OnWayPointChange notification) {

    }
}
