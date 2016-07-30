package emperatriz.mchprofessional;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import emperatriz.common.Sys;
import emperatriz.common.WappDto;

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
        }else  if (messageEvent.getPath().equals(Sys.COLOR_PATH)) {
            final String message = new String(messageEvent.getData());
            int c = Integer.parseInt(message);
            Sys.save("color",c,this);
        }else  if (messageEvent.getPath().equals(Sys.WEAR_APPS)) {
            final String wapps = Sys.getInstalledApps(this);
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                                @Override
                                public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                                    for (Node node : nodes.getNodes()) {
                                        Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), Sys.WEAR_APPS, wapps.getBytes());
                                        mGoogleApiClient.disconnect();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .build();
            mGoogleApiClient.connect();
        }else  if (messageEvent.getPath().equals(Sys.WEAR_URLS)) {
            final String wurls = new String(messageEvent.getData());
            ArrayList<WappDto> wurlArray = Sys.parseWapps(wurls);
            Sys.saveWapp("north",wurlArray.get(0),this);
            Sys.saveWapp("south",wurlArray.get(1),this);
            Sys.saveWapp("east",wurlArray.get(2),this);
            Sys.saveWapp("west",wurlArray.get(3),this);
        }
    }
}

