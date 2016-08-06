package emperatriz.mchprofessional;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import emperatriz.common.Sys;

/**
 * Created by ramon on 11/04/16.
 */
public class ListenerSun extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks{

    private GoogleApiClient mGoogleApiClient;
    private String mPeerId;
    private String batteryPct="";

    boolean connected=false;


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
    if (messageEvent.getPath().equals(Sys.SUNTIMES_PATH)){
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .build();
            mGoogleApiClient.connect();
            try{
                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
                    Location lastlocation = locationManager.getLastKnownLocation(locationProvider);
                    SunTimes st = new SunTimes();
                    st.execute("http://api.sunrise-sunset.org/json?lat="+lastlocation.getLatitude()+"&lng="+lastlocation.getLongitude()+"&date=today&formatted=0");
                }
            }catch (Exception ex){

            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        connected=true;
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, text.getBytes() ).await();
                }
                mGoogleApiClient.disconnect();
            }
        }).start();
    }


    @Override
    public void onConnectionSuspended(int i) {
        connected=false;
    }

    public class SunTimes extends AsyncTask<String, Long, String> {
        String sunrise;
        String sunset;
        protected String doInBackground(String... urls) {
            try {
                if ( ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET ) == PackageManager.PERMISSION_GRANTED ) {
                    return HttpRequest.get(urls[0]).accept("application/json").body();
                }
                return null;
            } catch (Exception exception) {
                return null;
            }
        }

        protected void onPostExecute(String response) {
            try{
                String toTimeZone = "CET";
                String fromTimeZone = "UTC";
                JSONObject jObject = new JSONObject(response);
                JSONObject results = jObject.getJSONObject("results");
                String sunrise1 = results.getString("sunrise").replace("+00:00","");
                String sunset1 = results.getString("sunset").replace("+00:00","");;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
                Date sr = dateFormat.parse(sunrise1);
                Date ss = dateFormat.parse(sunset1);
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
                //dateFormat2.setTimeZone(TimeZone.getTimeZone(toTimeZone));
                dateFormat2.setTimeZone(TimeZone.getDefault());
                sunrise = dateFormat2.format(sr);
                sunset = dateFormat2.format(ss);
                sendMessage(Sys.SUNTIMES_PATH,sunrise+"|"+sunset);
            }catch (Exception ex){

            }

        }
    }

}
