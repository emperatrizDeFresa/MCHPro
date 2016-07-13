package emperatriz.mchprofessional;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import emperatriz.common.Sys;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, View.OnClickListener{

    private GoogleApiClient mGoogleApiClient;
    private String batteryPct="";
    private boolean connected=false;
    FloatingActionButton fab;
    String sunrise;
    String sunset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPct = Math.round(level*100 / (float)scale)+"";

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Error, not connected!", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        mGoogleApiClient.connect();


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);



        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission( this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED    ) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET},2706);
        }

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
    public void onConnected(Bundle bundle) {
        connected=true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        connected=false;
    }


    @Override
    public void onClick(View v) {
        if (v==fab){
            if (connected){
                sendMessage(Sys.PHONE_BATTERY_PATH, batteryPct);
                try{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET},
                            2706);
                    LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                    String locationProvider = LocationManager.NETWORK_PROVIDER;
                    if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
                        Location lastlocation = locationManager.getLastKnownLocation(locationProvider);
                        SunTimes st = new SunTimes();
                        st.execute("http://api.sunrise-sunset.org/json?lat="+lastlocation.getLatitude()+"&lng="+lastlocation.getLongitude()+"&date=today&formatted=0");
                    }
                }catch (Exception ex){

                }

            }
            else
                    Toast.makeText(getApplicationContext(),
                            "Watch not connected", Toast.LENGTH_SHORT).show();
        }
    }

    public class SunTimes extends AsyncTask<String, Long, String> {
        protected String doInBackground(String... urls) {
            try {
                if ( ContextCompat.checkSelfPermission( MainActivity.this, Manifest.permission.INTERNET ) == PackageManager.PERMISSION_GRANTED ) {
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
                dateFormat2.setTimeZone(TimeZone.getTimeZone(toTimeZone));
                sunrise = dateFormat2.format(sr);
                sunset = dateFormat2.format(ss);
                sendMessage(Sys.SUNTIMES_PATH,sunrise+"|"+sunset);
            }catch (Exception ex){

            }

        }
    }
}
