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
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    int backColor,badgeIndex, backIndex;
    Button  appN, appE, appW, appS, appC, color1, color2, color3;
    Spinner spin;
    TextView name, url;
    FloatingActionButton fab;
    int cheat=0;

    public WappDto north, south, east ,west, center;

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
        fab.animate().scaleX(0f).scaleY(0f).setDuration(0).setInterpolator(new BounceInterpolator()).start();
        fab.setAlpha(0f);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable update = new Runnable(){
            public void run(){

                fab.setAlpha(1f);
                fab.animate().scaleX(1f).scaleY(1f).setDuration(1400).setInterpolator(new BounceInterpolator()).start();
            }
        };
        handler.postDelayed(update,1000);



        cv = (CanvasView) findViewById(R.id.view);
        cv.setMainActivity(this);
        cv.setColor(Sys.getInt("color", 0xff00dddd, MainActivity.this));
        final ArrayList<String> badgesList = new ArrayList<String>();
        badgesList.add("MCH Professional");
        badgesList.add("Galactica");
        badgesList.add("Star Trek");
        badgesList.add("Star Wars");
        badgesList.add("Android");
        badgesList.add("Zelda");
        badgesList.add("Candón");
        badgesList.add("Nel");

        cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setAdapter(new BadgesAdapter(MainActivity.this,R.layout.badgerow,badgesList), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cv.setBadge(which);
                                badgeIndex=which;
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .show();
                return false;
            }
        });


        final ArrayList<String> backsList = new ArrayList<String>();
        backsList.add("Negro");
        backsList.add("Violeta");
        backsList.add("Azul");
        backsList.add("Carmesí");
        backsList.add("Verde");
        backsList.add("Papel pintado");
        backsList.add("Remolinos");
        backsList.add("Carnaval");
        backsList.add("Laura");
        backsList.add("Yune");
        ImageView mch = (ImageView) findViewById(R.id.imageViewmch);
        mch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheat++;
                if (cheat%3==0){
                    new AlertDialog.Builder(MainActivity.this)
                            .setAdapter(new BacksAdapter(MainActivity.this,R.layout.backrow,backsList,center.name.toUpperCase().equals("EMPERATRIZ")), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cv.setBack(which);
                                    backIndex=which;
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(true)
                            .show();

                }
            }
        });


        backColor = Sys.getInt("color", 0xff00dddd, MainActivity.this);
        badgeIndex = Sys.getInt("badge", 0, MainActivity.this);
        backIndex = Sys.getInt("background", 0, MainActivity.this);


        cv.setBadge(badgeIndex);

        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission( this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED    ) {
            new AlertDialog.Builder(MainActivity.this)
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET},2706);
                            }
                        })
                    .setCancelable(false)
                    .setMessage(getResources().getString(R.string.permissions))
                    .show();

        }


        TabHost host = (TabHost)findViewById(R.id.tab_host);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec(getResources().getString(R.string.backcolor));
        spec.setContent(R.id.tab_one_container);
        spec.setIndicator(getResources().getString(R.string.backcolor));
        host.addTab(spec);

        spec = host.newTabSpec(getResources().getString(R.string.shortcuts));
        spec.setContent(R.id.tab_two_container);
        spec.setIndicator(getResources().getString(R.string.shortcuts));
        host.addTab(spec);


        final ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
//        SVBar svBar = (SVBar) findViewById(R.id.svbar);
//        picker.addSVBar(svBar);
        SaturationBar sBar = (SaturationBar) findViewById(R.id.sbar);
        picker.addSaturationBar(sBar);
        ValueBar vBar = (ValueBar) findViewById(R.id.vbar);
        picker.addValueBar(vBar);
        picker.setColor(Sys.getInt("color", 0xff00dddd, MainActivity.this));
        picker.setOldCenterColor(picker.getColor());
        picker.setOnColorChangedListener(this);
        picker.setOnColorSelectedListener(this);
        picker.setShowOldCenterColor(false);


        north = Sys.getWapp("north",Sys.NORTH_DEFAULT,this);
        south = Sys.getWapp("south",Sys.SOUTH_DEFAULT,this);
        east = Sys.getWapp("east",Sys.EAST_DEFAULT,this);
        west = Sys.getWapp("west",Sys.WEST_DEFAULT,this);
        center = Sys.getWapp("center",Sys.EAST_DEFAULT,this);

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
        appC = (Button)findViewById(R.id.appC);
        appC.setText(center.name);
        appC.setOnClickListener(this);

        ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this),true);
        if (wappsArray.size()==0){
            UpdateWapps uw = new UpdateWapps();
            uw.execute();
        }

        color1 = (Button) findViewById(R.id.colorGreen);
        color1.setBackgroundColor(Sys.getInt("color1",0xff9aff98, this));
        color1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Sys.save("color1",picker.getColor(),MainActivity.this);
                v.setBackgroundColor(Sys.getInt("color1",0xff9aff98, MainActivity.this));
                return true;
            }
        });
        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
                picker.setColor(buttonColor.getColor());
            }
        });
        color2 = (Button) findViewById(R.id.colorBlue);
        color2.setBackgroundColor(Sys.getInt("color2",0xff00dddd, this));
        color2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Sys.save("color2",picker.getColor(),MainActivity.this);
                v.setBackgroundColor(Sys.getInt("color2",0xff00dddd, MainActivity.this));
                return true;
            }
        });
        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
                picker.setColor(buttonColor.getColor());
            }
        });
        color3 = (Button) findViewById(R.id.colorOrange);
        color3.setBackgroundColor(Sys.getInt("color3",0xffffb504, this));
        color3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Sys.save("color3",picker.getColor(),MainActivity.this);
                v.setBackgroundColor(Sys.getInt("color3",0xffffb504, MainActivity.this));
                return true;
            }
        });
        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable buttonColor = (ColorDrawable) v.getBackground();
                picker.setColor(buttonColor.getColor());
            }
        });

        ImageView info = (ImageView)findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(getResources().getString(R.string.info))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

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
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        String batteryPct = Math.round(level*100 / (float)scale)+"";
        sendMessage(Sys.PHONE_BATTERY_PATH, batteryPct,false);
        //Toast.makeText(MainActivity.this,String.format("#%06X", (0xFFFFFF & backColor)), Toast.LENGTH_SHORT).show();
        Sys.save("color",backColor,MainActivity.this);
        sendMessage(Sys.WEAR_BADGE,badgeIndex+"",false);
        Sys.save("badge",badgeIndex,MainActivity.this);
        sendMessage(Sys.WEAR_BACK,backIndex+"",false);
        Sys.save("background",backIndex,MainActivity.this);
        int c = Integer.parseInt(backColor+"");
        sendMessage(Sys.COLOR_PATH,backColor+"",false);
        String wurls = "";
        WappDto n = Sys.getWapp("north",Sys.NORTH_DEFAULT,this);
        WappDto s = Sys.getWapp("south",Sys.SOUTH_DEFAULT,this);
        WappDto e = Sys.getWapp("east",Sys.EAST_DEFAULT,this);
        WappDto w = Sys.getWapp("west",Sys.WEST_DEFAULT,this);
        WappDto ce = Sys.getWapp("center",Sys.EAST_DEFAULT,this);
        wurls += n.name+","+n.url+";";
        wurls += s.name+","+s.url+";";
        wurls += e.name+","+e.url+";";
        wurls += w.name+","+w.url+";";
        wurls += ce.name+","+ce.url;
        sendMessage(Sys.WEAR_URLS,wurls,false);
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
                sendMessage(Sys.SUNTIMES_PATH,sunrise+"|"+sunset,true);
            }catch (Exception ex){

            }

        }
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
            Sys.saveWapp("center",center,this);
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.notConnected), Toast.LENGTH_SHORT).show();
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
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    north = new WappDto(name.getText().toString().toUpperCase(),url.getText().toString().replace(" ",""));
                    appN.setText(north.name);
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.getWapps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    UpdateWapps uw = new UpdateWapps();
                    uw.execute();
                }
            });
            alertDialog.show();
            name = (TextView) dialogView.findViewById(R.id.name);
            url = (TextView) dialogView.findViewById(R.id.url);
            url.setEnabled(false);
            spin = (Spinner) dialogView.findViewById(R.id.spinner);
            ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this),true);

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
                    if (dto.name.equals(getApplicationContext().getResources().getString(R.string.custom))){
                        url.setText(dto.url.replace(" ",""));
                        url.setEnabled(true);
                        url.setHint(getApplicationContext().getResources().getString(R.string.hint));
                        url.setHintTextColor(0xff999999);
                    }else{
                        url.setEnabled(false);
                        url.setHint("");
                        url.setText(dto.url);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (north.name.equals(getResources().getString(R.string.custom).toUpperCase())){
                wappsArray.get(wappsArray.size()-1).url = north.url;
                spin.setSelection(wappsArray.size()-1);
            }else{
                spin.setSelection(wappsArray.indexOf(north));
            }
        }else if (v==appS){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.wurl, null);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    south = new WappDto(name.getText().toString().toUpperCase(),url.getText().toString().replace(" ",""));
                    appS.setText(south.name);
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.getWapps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    UpdateWapps uw = new UpdateWapps();
                    uw.execute();
                }
            });
            alertDialog.show();
            name = (TextView) dialogView.findViewById(R.id.name);
            url = (TextView) dialogView.findViewById(R.id.url);
            url.setEnabled(false);
            spin = (Spinner) dialogView.findViewById(R.id.spinner);
            ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this),true);
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
                    if (dto.name.equals(getApplicationContext().getResources().getString(R.string.custom))){
                        url.setText(dto.url.replace(" ",""));
                        url.setEnabled(true);
                        url.setHint(getApplicationContext().getResources().getString(R.string.hint));
                        url.setHintTextColor(0xff999999);
                    }else{
                        url.setEnabled(false);
                        url.setHint("");
                        url.setText(dto.url);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (south.name.equals(getResources().getString(R.string.custom).toUpperCase())){
                wappsArray.get(wappsArray.size()-1).url = south.url;
                spin.setSelection(wappsArray.size()-1);
            }else{
                spin.setSelection(wappsArray.indexOf(south));
            }
        }else if (v==appE){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.wurl, null);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    east = new WappDto(name.getText().toString().toUpperCase(),url.getText().toString().replace(" ",""));
                    appE.setText(east.name);
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.getWapps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    UpdateWapps uw = new UpdateWapps();
                    uw.execute();
                }
            });
            alertDialog.show();
            name = (TextView) dialogView.findViewById(R.id.name);
            url = (TextView) dialogView.findViewById(R.id.url);
            url.setEnabled(false);
            spin = (Spinner) dialogView.findViewById(R.id.spinner);
            ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this),true);
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
                    if (dto.name.equals(getApplicationContext().getResources().getString(R.string.custom))){
                        url.setText(dto.url.replace(" ",""));
                        url.setEnabled(true);
                        url.setHint(getApplicationContext().getResources().getString(R.string.hint));
                        url.setHintTextColor(0xff999999);
                    }else{
                        url.setEnabled(false);
                        url.setHint("");
                        url.setText(dto.url);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (east.name.equals(getResources().getString(R.string.custom).toUpperCase())){
                wappsArray.get(wappsArray.size()-1).url = east.url;
                spin.setSelection(wappsArray.size()-1);
            }else{
                spin.setSelection(wappsArray.indexOf(east));
            }
        }else if (v==appW){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.wurl, null);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    west = new WappDto(name.getText().toString().toUpperCase(),url.getText().toString().replace(" ",""));
                    appW.setText(west.name);
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.getWapps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    UpdateWapps uw = new UpdateWapps();
                    uw.execute();
                }
            });
            alertDialog.show();
            name = (TextView) dialogView.findViewById(R.id.name);
            url = (TextView) dialogView.findViewById(R.id.url);
            url.setEnabled(false);
            spin = (Spinner) dialogView.findViewById(R.id.spinner);
            ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this),true);
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
                    if (dto.name.equals(getApplicationContext().getResources().getString(R.string.custom))){
                        url.setText(dto.url.replace(" ",""));
                        url.setEnabled(true);
                        url.setHint(getApplicationContext().getResources().getString(R.string.hint));
                        url.setHintTextColor(0xff999999);
                    }else{
                        url.setEnabled(false);
                        url.setHint("");
                        url.setText(dto.url);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (west.name.equals(getResources().getString(R.string.custom).toUpperCase())){
                wappsArray.get(wappsArray.size()-1).url = west.url;
                spin.setSelection(wappsArray.size()-1);
            }else{
                spin.setSelection(wappsArray.indexOf(west));
            }

        }else if (v==appC){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.wurl, null);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    center = new WappDto(name.getText().toString().toUpperCase(),url.getText().toString().replace(" ",""));
                    appC.setText(center.name);
                }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getResources().getString(R.string.getWapps), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    UpdateWapps uw = new UpdateWapps();
                    uw.execute();
                }
            });
            alertDialog.show();
            name = (TextView) dialogView.findViewById(R.id.name);
            url = (TextView) dialogView.findViewById(R.id.url);
            url.setEnabled(false);
            spin = (Spinner) dialogView.findViewById(R.id.spinner);
            ArrayList<WappDto> wappsArray = Sys.parseWapps(Sys.getString("wapps","",this),true);
            spin.setAdapter(new WappSpinnerAdapter(this,R.layout.wapprow,wappsArray));
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    WappDto dto = (WappDto) spin.getSelectedItem();
                    if (!dto.equals(center)) {
                        name.setText(dto.name);
                    }else{
                        name.setText(center.name);
                    }
                    if (dto.name.equals(getApplicationContext().getResources().getString(R.string.custom))){
                        url.setText(dto.url.replace(" ",""));
                        url.setEnabled(true);
                        url.setHint(getApplicationContext().getResources().getString(R.string.hint));
                        url.setHintTextColor(0xff999999);
                    }else{
                        url.setEnabled(false);
                        url.setHint("");
                        url.setText(dto.url);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            if (center.name.equals(getResources().getString(R.string.custom).toUpperCase())){
                wappsArray.get(wappsArray.size()-1).url = center.url;
                spin.setSelection(wappsArray.size()-1);
            }else{
                spin.setSelection(wappsArray.indexOf(center));
            }
        }

    }




    @Override
    public void onColorChanged(int color) {
        backColor=color;
        cv.setColor(color);
    }

    @Override
    public void onColorSelected(int color) {
//        backColor=color;
//        cv.setColor(color);
    }

    class UpdateWapps extends AsyncTask<Void, Integer, String>
    {
        @Override
        protected void onPreExecute (){
            Toast.makeText(MainActivity.this,getApplication().getResources().getString(R.string.gettingWapps), Toast.LENGTH_SHORT).show();
            //Sys.init().showDialog(getApplication().getResources().getString(R.string.gettingWapps),MainActivity.this);
        }

        @Override
        protected String doInBackground(Void...arg0) {
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
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //Sys.init().hideDialog(MainActivity.this);
        }
    }
}
