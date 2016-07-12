package emperatriz.mchprofessional;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

import emperatriz.common.Sys;

/**
 * Created by ramon on 16/04/16.
 */
public class MessageListener extends WearableListenerService{



    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(Sys.PHONE_BATTERY_PATH)) {
            final String message = new String(messageEvent.getData());
            Sys.save("battery",message,this);
        } else  if (messageEvent.getPath().equals(Sys.SUNTIMES_PATH)) {
            final String message = new String(messageEvent.getData());
            Sys.save("sunrise",message.substring(0,5),this);
            Sys.save("sunset",message.substring(6,11),this);
        }
    }
}

