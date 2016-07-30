package emperatriz.mchprofessional;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import emperatriz.common.Sys;
import emperatriz.common.WappDto;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, View.OnClickListener, ColorPicker.OnColorChangedListener, ColorPicker.OnColorSelectedListener {

    private GoogleApiClient mGoogleApiClient;
    int backColor;
    Button  appN, appE, appW, appS;
    ImageButton wapps;
    Spinner spin;
    TextView name, url;
    FloatingActionButton fab;

    public WappDto north, south, east ,west;

    CanvasView cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);


        cv = (CanvasView) findViewById(R.id.view);
        cv.setMainActivity(this);
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
        picker.setOnColorSelectedListener(this);
        picker.setShowOldCenterColor(false);


        north = Sys.getWapp("north",Sys.NORTH_DEFAULT,this);
        south = Sys.getWapp("south",Sys.SOUTH_DEFAULT,this);
        east = Sys.getWapp("east",Sys.EAST_DEFAULT,this);
        west = Sys.getWapp("west",Sys.WEST_DEFAULT,this);

        wapps = (ImageButton) findViewById(R.id.wapps);
        wapps.setOnClickListener(this);
        appN = (Button)findViewById(R.id.appN);
        appN.setText(north.name);
        appN.setOnClickListener(this);
        appS = (Button) findViewById(R.id.appS);
        appS.setText(south.name);
        appS.setOnClickListener(this);
        appE = (Button)findViewById(R.id.appE);
        appE.setText(east.name);
        appE.setOnClickListener(this);
        appW = (Button)findViewById(R.id.appW);
        appW.setText(west.name);
        appW.setOnClickListener(this);



    }


    private void sendMessage( final String path, final String text, final boolean disconnect ) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, text.getBytes() ).await();
                }
                if (disconnect) mGoogleApiClient.disconnect();
            }
        }).start();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Sys.save("color",backColor,MainActivity.this);
        int c = Integer.parseInt(backColor+"");
        sendMessage(Sys.COLOR_PATH,backColor+"",false);
        String wurls = "";
        WappDto n = Sys.getWapp("north",Sys.NORTH_DEFAULT,this);
        WappDto s = Sys.getWapp("south",Sys.SOUTH_DEFAULT,this);
        WappDto e = Sys.getWapp("east",Sys.EAST_DEFAULT,this);
        WappDto w = Sys.getWapp("west",Sys.WEST_DEFAULT,this);
        wurls += n.name+","+n.url+";";
        wurls += s.name+","+s.url+";";
        wurls += e.name+","+e.url+";";
        wurls += w.name+","+w.url;
        sendMessage(Sys.WEAR_URLS,wurls,true);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), "=O= !", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        if (v==fab) {
            Sys.saveWapp("north",north,this);
            Sys.saveWapp("south",south,this);
            Sys.saveWapp("east",east,this);
            Sys.saveWapp("west",west,this);
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
        } else if (v==wapps){
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            sendMessage(Sys.WEAR_APPS,"",true);
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .build();
            mGoogleApiClient.connect();

        } else if (v==appN){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.wurl, null);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    north = new WappDto(name.getText().toString().toUpperCase(),url.getText().toString());
                    appN.setText(north.name);
                }
            });
            alertDialog.show();
            name = (TextView) dialogView.findViewById(R.id.name);
            url = (TextView) dialogView.findViewById(R.id.url);
            url.setEnabled(false);
            spin = (Spinner) dialogView.findViewById(R.id.spinner);
            ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this));
            spin.setAdapter(new WappSpinnerAdapter(this,R.layout.wapprow,wappsArray));
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    WappDto dto = (WappDto) spin.getSelectedItem();
                    if (!dto.equals(north)) {
                        name.setText(dto.name);
                    }else{
                        name.setText(north.name);
                    }
                    url.setText(dto.url);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spin.setSelection(wappsArray.indexOf(north));
        }else if (v==appS){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.wurl, null);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    south = new WappDto(name.getText().toString().toUpperCase(),url.getText().toString());
                    appS.setText(south.name);
                }
            });
            alertDialog.show();
            name = (TextView) dialogView.findViewById(R.id.name);
            url = (TextView) dialogView.findViewById(R.id.url);
            url.setEnabled(false);
            spin = (Spinner) dialogView.findViewById(R.id.spinner);
            ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this));
            spin.setAdapter(new WappSpinnerAdapter(this,R.layout.wapprow,wappsArray));
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    WappDto dto = (WappDto) spin.getSelectedItem();
                    if (!dto.equals(south)) {
                        name.setText(dto.name);
                    }else{
                        name.setText(south.name);
                    }
                    url.setText(dto.url);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spin.setSelection(wappsArray.indexOf(south));
        }else if (v==appE){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.wurl, null);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    east = new WappDto(name.getText().toString().toUpperCase(),url.getText().toString());
                    appE.setText(east.name);
                }
            });
            alertDialog.show();
            name = (TextView) dialogView.findViewById(R.id.name);
            url = (TextView) dialogView.findViewById(R.id.url);
            url.setEnabled(false);
            spin = (Spinner) dialogView.findViewById(R.id.spinner);
            ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this));
            spin.setAdapter(new WappSpinnerAdapter(this,R.layout.wapprow,wappsArray));
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    WappDto dto = (WappDto) spin.getSelectedItem();
                    if (!dto.equals(east)) {
                        name.setText(dto.name);
                    }else{
                        name.setText(east.name);
                    }
                    url.setText(dto.url);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spin.setSelection(wappsArray.indexOf(east));
        }else if (v==appW){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.wurl, null);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    west = new WappDto(name.getText().toString().toUpperCase(),url.getText().toString());
                    appW.setText(west.name);
                }
            });
            alertDialog.show();
            name = (TextView) dialogView.findViewById(R.id.name);
            url = (TextView) dialogView.findViewById(R.id.url);
            url.setEnabled(false);
            spin = (Spinner) dialogView.findViewById(R.id.spinner);
            ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this));
            spin.setAdapter(new WappSpinnerAdapter(this,R.layout.wapprow,wappsArray));
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    WappDto dto = (WappDto) spin.getSelectedItem();
                    if (!dto.equals(west)) {
                        name.setText(dto.name);
                    }else{
                        name.setText(west.name);
                    }
                    url.setText(dto.url);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spin.setSelection(wappsArray.indexOf(west));
        }

    }




    @Override
    public void onColorChanged(int color) {
//        backColor=color;
//        cv.setColor(color);
    }

    @Override
    public void onColorSelected(int color) {
        backColor=color;
        cv.setColor(color);
    }
}
