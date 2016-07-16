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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import emperatriz.common.Sys;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, View.OnClickListener, ColorPicker.OnColorChangedListener {

    private GoogleApiClient mGoogleApiClient;
    int backColor;

    CanvasView cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cv = (CanvasView) findViewById(R.id.view);
        cv.setColor(Sys.getInt("color", 0xff00dddd, MainActivity.this));



        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission( this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED    ) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET},2706);
        }


        TabHost host = (TabHost)findViewById(R.id.tab_host);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("Color de fondo");
        spec.setContent(R.id.tab_one_container);
        spec.setIndicator("Color de fondo");
        host.addTab(spec);

        spec = host.newTabSpec("Accesos directos");
        spec.setContent(R.id.tab_two_container);
        spec.setIndicator("Accesos directos");
        host.addTab(spec);


        ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        picker.addSVBar(svBar);
        picker.setColor(Sys.getInt("color", 0xff00dddd, MainActivity.this));
        picker.setOldCenterColor(picker.getColor());
        picker.setOnColorChangedListener(this);
        picker.setShowOldCenterColor(false);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
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

                Sys.save("color",backColor,MainActivity.this);
                int c = Integer.parseInt(backColor+"");
                sendMessage(Sys.COLOR_PATH,backColor+"");
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void sendMessage( final String path, final String text ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, text.getBytes() ).await();
                    mGoogleApiClient.disconnect();
                }

            }
        }).start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Sys.save("color",backColor,MainActivity.this);
        int c = Integer.parseInt(backColor+"");
        sendMessage(Sys.COLOR_PATH,backColor+"");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "Reloj no conectado", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {


    }


    @Override
    public void onColorChanged(int color) {
        backColor=color;
        cv.setColor(color);
    }
}
