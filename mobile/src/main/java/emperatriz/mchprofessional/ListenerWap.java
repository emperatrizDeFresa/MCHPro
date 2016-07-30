package emperatriz.mchprofessional;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import emperatriz.common.Sys;

/**
 * Created by ramon on 11/04/16.
 */
public class ListenerWap extends WearableListenerService{

    private String mPeerId;
    private String batteryPct="";
    String sunrise;
    String sunset;
    boolean connected=false;


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(Sys.WEAR_APPS)) {
            final String message = new String(messageEvent.getData());
            Sys.save("wapps",message,this);
        }
    }









}
