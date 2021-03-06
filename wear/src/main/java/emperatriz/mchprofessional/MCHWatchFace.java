package emperatriz.mchprofessional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.AlarmClock;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import emperatriz.common.DrawUtils;
import emperatriz.common.Sys;

public class MCHWatchFace extends CanvasWatchFaceService implements SensorEventListener {
    private static final Typeface NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static final int MSG_UPDATE_TIME = 0;
    private final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    SensorManager mSensorManager;
    int steps=0;
    int todaySteps=0;
    boolean saveLastSteps=true;
    PowerManager.WakeLock wakeLock;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    void releaseWakelockAfter(int time) {

        Runnable task = new Runnable() {
            public void run() {
                wakeLock.release();
            }
        };
        worker.schedule(task, time, TimeUnit.SECONDS);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            steps =  (int)event.values[0];
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

                if (!Sys.getString("lastSteps","",getApplicationContext()).equals(sdf.format(new Date()))){
                    Sys.save("lastSteps", sdf.format(new Date()), MCHWatchFace.this);
                    todaySteps=steps-1;
                    todaySteps = Math.max(todaySteps,0);
                    Sys.save("todaySteps", todaySteps, MCHWatchFace.this);
                }
                Sys.save("steps", steps, MCHWatchFace.this);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MCHWatchFace.Engine> mWeakReference;

        public EngineHandler(MCHWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MCHWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    BroadcastReceiver screenBroadcast = new BroadcastReceiver() {
        //When Event is published, onReceive method is called
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            }

        }
    };

    private class Engine extends CanvasWatchFaceService.Engine  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mBackgroundPaint;
        Paint timePaint;
        Paint restPaint;
        Paint whitePaint;
        boolean mAmbient;
        Time mTime;
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        int mTapCount;
        float mXOffset;
        float mYOffset;
        boolean mLowBitAmbient;
        Node wearNode;

        private List<Bitmap> badges;
        private Bitmap badge;
        private Bitmap badge1;
        private Bitmap badge2;
        private Bitmap badge3;
        private Bitmap badge4;
        private Bitmap badge5,badge6, badge7;
        private List<Bitmap> bells;
        private Bitmap bell;
        private Bitmap bell1;
        private Bitmap bell2;
        private Bitmap bell3;
        private Bitmap bell4;
        private Bitmap bell5;
        private Bitmap bell6;
        private Bitmap bell7;
        private Bitmap bell8;
        private Bitmap bell9;
        private List<Bitmap> backs;
        private Bitmap back;
        private Bitmap back1;
        private Bitmap back2;
        private Bitmap back3;
        private Bitmap back4,back5,back6,back7,back8,back9;
        private Bitmap shadow;
        private Bitmap shadowAmb;
        private GoogleApiClient mGoogleApiClient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MCHWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setHotwordIndicatorGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                    .setStatusBarGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());
            Resources resources = MCHWatchFace.this.getResources();
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);

            mBackgroundPaint = new Paint();

            Typeface font1 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/SF Square Head.ttf");
            Typeface font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Square.ttf");

            timePaint = new Paint();
            timePaint.setTypeface(font1);
            timePaint.setAntiAlias(false);

            restPaint = new Paint();
            restPaint.setTypeface(font2);
            restPaint.setAntiAlias(false);

            whitePaint = new Paint();
            whitePaint.setColor(0xffffffff);


            shadow = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.shadow);
            shadowAmb = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.backamb);

            float scale = ((float) 320) / (float) shadow.getWidth();
            shadow = Bitmap.createScaledBitmap(shadow,(int) (shadow.getWidth() * scale),(int) (shadow.getHeight() * scale), true);
            shadowAmb = Bitmap.createScaledBitmap(shadowAmb,(int) (shadowAmb.getWidth() * scale),(int) (shadowAmb.getHeight() * scale), true);

            backs = new ArrayList<Bitmap>();
            back = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back);
            back = Bitmap.createScaledBitmap(back,(int) (back.getWidth() * scale),(int) (back.getHeight() * scale), true);
            backs.add(back);
            back1 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back1);
            back1 = Bitmap.createScaledBitmap(back1,(int) (back1.getWidth() * scale),(int) (back1.getHeight() * scale), true);
            backs.add(back1);
            back2 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back2);
            back2 = Bitmap.createScaledBitmap(back2,(int) (back2.getWidth() * scale),(int) (back2.getHeight() * scale), true);
            backs.add(back2);
            back3 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back3);
            back3 = Bitmap.createScaledBitmap(back3,(int) (back3.getWidth() * scale),(int) (back3.getHeight() * scale), true);
            backs.add(back3);
            back4 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back4);
            back4 = Bitmap.createScaledBitmap(back4,(int) (back4.getWidth() * scale),(int) (back4.getHeight() * scale), true);
            backs.add(back4);
            back5 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back5);
            back5 = Bitmap.createScaledBitmap(back5,(int) (back5.getWidth() * scale),(int) (back5.getHeight() * scale), true);
            backs.add(back5);
            back6 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back6);
            back6 = Bitmap.createScaledBitmap(back6,(int) (back6.getWidth() * scale),(int) (back6.getHeight() * scale), true);
            backs.add(back6);
            back7 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back7);
            back7 = Bitmap.createScaledBitmap(back7,(int) (back7.getWidth() * scale),(int) (back7.getHeight() * scale), true);
            backs.add(back7);
            back8 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back8);
            back8 = Bitmap.createScaledBitmap(back8,(int) (back8.getWidth() * scale),(int) (back8.getHeight() * scale), true);
            backs.add(back8);
            back9 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back9);
            back9 = Bitmap.createScaledBitmap(back9,(int) (back9.getWidth() * scale),(int) (back9.getHeight() * scale), true);
            backs.add(back9);
            //shadowAmb = Bitmap.createScaledBitmap(shadowAmb,(int) (shadowAmb.getWidth() * scale),(int) (shadowAmb.getHeight() * scale), true);

            bells = new ArrayList<Bitmap>();
            bell = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell);
            bell = Bitmap.createScaledBitmap(bell,(int) (bell.getWidth() * scale),(int) (bell.getHeight() * scale), true);
            bells.add(bell);
            bell1 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell1);
            bell1 = Bitmap.createScaledBitmap(bell1,(int) (bell1.getWidth() * scale),(int) (bell1.getHeight() * scale), true);
            bells.add(bell1);
            bell2 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell2);
            bell2 = Bitmap.createScaledBitmap(bell2,(int) (bell2.getWidth() * scale),(int) (bell2.getHeight() * scale), true);
            bells.add(bell2);
            bell3 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell3);
            bell3 = Bitmap.createScaledBitmap(bell3,(int) (bell3.getWidth() * scale),(int) (bell3.getHeight() * scale), true);
            bells.add(bell3);
            bell4 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell4);
            bell4 = Bitmap.createScaledBitmap(bell4,(int) (bell4.getWidth() * scale),(int) (bell4.getHeight() * scale), true);
            bells.add(bell4);
            bell5 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell5);
            bell5 = Bitmap.createScaledBitmap(bell5,(int) (bell5.getWidth() * scale),(int) (bell5.getHeight() * scale), true);
            bells.add(bell5);
            bell6 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell6);
            bell6 = Bitmap.createScaledBitmap(bell6,(int) (bell6.getWidth() * scale),(int) (bell6.getHeight() * scale), true);
            bells.add(bell6);
            bell7 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell6);
            bell7 = Bitmap.createScaledBitmap(bell7,(int) (bell7.getWidth() * scale),(int) (bell7.getHeight() * scale), true);
            bells.add(bell7);
            bell8 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell8);
            bell8 = Bitmap.createScaledBitmap(bell8,(int) (bell8.getWidth() * scale),(int) (bell8.getHeight() * scale), true);
            bells.add(bell8);
            bell9 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.bell9);
            bell9 = Bitmap.createScaledBitmap(bell9,(int) (bell9.getWidth() * scale),(int) (bell9.getHeight() * scale), true);
            bells.add(bell9);

            badges = new ArrayList<Bitmap>();
            badge = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge);
            badge = Bitmap.createScaledBitmap(badge,(int) (badge.getWidth() * scale),(int) (badge.getHeight() * scale), true);
            badges.add(badge);
            badge1 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge1);
            badge1 = Bitmap.createScaledBitmap(badge1,(int) (badge1.getWidth() * scale),(int) (badge1.getHeight() * scale), true);
            badges.add(badge1);
            badge2 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge2);
            badge2 = Bitmap.createScaledBitmap(badge2,(int) (badge2.getWidth() * scale),(int) (badge2.getHeight() * scale), true);
            badges.add(badge2);
            badge3 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge3);
            badge3 = Bitmap.createScaledBitmap(badge3,(int) (badge3.getWidth() * scale),(int) (badge3.getHeight() * scale), true);
            badges.add(badge3);
            badge4 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge4);
            badge4 = Bitmap.createScaledBitmap(badge4,(int) (badge4.getWidth() * scale),(int) (badge4.getHeight() * scale), true);
            badges.add(badge4);
            badge6 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge6);
            badge6 = Bitmap.createScaledBitmap(badge6,(int) (badge6.getWidth() * scale),(int) (badge6.getHeight() * scale), true);
            badges.add(badge6);
            badge5 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge5);
            badge5 = Bitmap.createScaledBitmap(badge5,(int) (badge5.getWidth() * scale),(int) (badge5.getHeight() * scale), true);
            badges.add(badge5);
            badge7 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge7);
            badge7 = Bitmap.createScaledBitmap(badge7,(int) (badge7.getWidth() * scale),(int) (badge7.getHeight() * scale), true);
            badges.add(badge7);

            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .build();
            mGoogleApiClient.connect();

            DrawUtils.mTime = new Time();
            mTime = new Time();

            mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
            Sensor mStepCountSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            mSensorManager.registerListener(MCHWatchFace.this, mStepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);
            getApplicationContext().registerReceiver(screenBroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

//            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
//
//            String lastSteps = Sys.getString("lastSteps", "", MCHWatchFace.this);
//            if (!lastSteps.equals(sdf.format(new Date()))){
//                saveLastSteps=true;
//            }

            if (visible) {
                registerReceiver();
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();

            } else {
                unregisterReceiver();
            }

            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MCHWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MCHWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = MCHWatchFace.this.getResources();
            boolean isRound = insets.isRound();
            mXOffset = resources.getDimension(isRound
                    ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            float textSize = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);

//            mTextPaint.setTextSize(textSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
//                    timePaint.setAntiAlias(!inAmbientMode);
//                    restPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            if (!inAmbientMode){
                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

                wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"WatchFaceWakelockTag"); // note WakeLock spelling

                wakeLock.acquire();

                releaseWakelockAfter(10);
            }
            updateTimer();
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            Resources resources = MCHWatchFace.this.getResources();
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    mTapCount++;
//                    Log.i("MCH Professional","Tap "+mTapCount+" "+x+","+y);
                    if (Calendar.getInstance().getTimeInMillis()-Sys.getLong("lastTap", 0,getApplicationContext())<200) {
                        if ((x > 80 && x < 240) && (y > 0 && y < 80)){ // NORTH
                            launch(Sys.getWapp("north",Sys.NORTH_DEFAULT,getApplicationContext()).url);
                        } else if ((x > 80 && x < 240) && (y > 240 && y < 320)){ // SOUTH
                            launch(Sys.getWapp("south",Sys.SOUTH_DEFAULT,getApplicationContext()).url);
                        } else if ((x > 240 && x < 320) && (y > 80 && y < 240)){ // EAST
                            launch(Sys.getWapp("east",Sys.EAST_DEFAULT,getApplicationContext()).url);
                        } else if ((x > 0 && x < 80) && (y > 80 && y < 240)){ // WEST
                            launch(Sys.getWapp("west",Sys.WEST_DEFAULT,getApplicationContext()).url);
                        }else if ((x > 80 && x < 240) && (y > 80 && y < 240)){ // CENTER
                            launch(Sys.getWapp("center",Sys.EAST_DEFAULT,getApplicationContext()).url);
                        }
                    }
                    Sys.save("lastTap",Calendar.getInstance().getTimeInMillis(),getApplicationContext());

                    break;
            }
            invalidate();
        }

        public void launch (String url){
//            Log.i("MCH Professional","launch "+url);
            if (url.equals(Sys.TIMER)){
                Intent i= new Intent(AlarmClock.ACTION_SET_TIMER);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                getApplicationContext().startActivity(i);
            } else  if (url.equals(Sys.ALARM)){
                Intent i= new Intent(AlarmClock.ACTION_SET_ALARM);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                getApplicationContext().startActivity(i);
            } else {
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(url);
                if (intent!=null){
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),";_;",Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            pollPhoneBattery(false);
            pollSuntimes(false);

            mTime.setToNow();

            DrawUtils.mTime.setToNow();
            DrawUtils.now = System.currentTimeMillis();
            DrawUtils.height = bounds.height();
            DrawUtils.width = bounds.width();
            DrawUtils.canvas = canvas;
            DrawUtils.isInAmbientMode = isInAmbientMode();
            DrawUtils.color = Sys.getInt("color", 0xff00dddd, getApplicationContext());
            DrawUtils.offsetX=0;
            DrawUtils.offsetY=0;

            int notifications = getNotificationCount();
            SharedPreferences preferences = getSharedPreferences("mchPro", MODE_PRIVATE);
            String phoneBattery = Sys.getString("battery","81",getApplicationContext()) + "";

            Intent batteryIntent = getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            String watchBattery = Math.round(level*100 / (float)scale)+"";
            todaySteps = Sys.getInt("todaySteps", 0, MCHWatchFace.this);
            steps = Sys.getInt("steps", 0, MCHWatchFace.this);
            if (steps-todaySteps<0){
                Sys.save("todaySteps", steps, MCHWatchFace.this);
                todaySteps=steps;
            }

            DrawUtils.drawBackground(shadow, shadowAmb, backs.get(Sys.getInt("background",0,getApplicationContext())), badges.get(Sys.getInt("badge",0,getApplicationContext())),mBackgroundPaint, whitePaint);
            DrawUtils.drawDate(restPaint);
            DrawUtils.drawHHmm(timePaint);
            DrawUtils.drawSecs(timePaint);
            DrawUtils.drawSteps((steps-todaySteps)+"", restPaint);
            DrawUtils.drawWatchBattery(watchBattery, restPaint);
            DrawUtils.drawPhoneBattery(phoneBattery, restPaint);
            DrawUtils.drawSunrise(Sys.getString("sunrise","--:--",getApplicationContext()), restPaint);
            DrawUtils.drawSunset(Sys.getString("sunset","--:--",getApplicationContext()), restPaint);
            DrawUtils.drawUnread(notifications, bells, restPaint);
            DrawUtils.drawShortcuts(Sys.getWapp("north",Sys.NORTH_DEFAULT,getApplicationContext()).name,
                                    Sys.getWapp("south",Sys.SOUTH_DEFAULT,getApplicationContext()).name,
                                    Sys.getWapp("east",Sys.EAST_DEFAULT,getApplicationContext()).name,
                                    Sys.getWapp("west",Sys.WEST_DEFAULT,getApplicationContext()).name, DrawUtils.getBackColor(Sys.getInt("background",0,getApplicationContext())), restPaint);

        }

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        private void pollPhoneBattery(boolean force) {
            long nextPoll = Long.parseLong(Sys.getString("nextPoll","0",getApplicationContext()));
//            Log.i("MCH Professional",Calendar.getInstance().getTimeInMillis()+">"+nextPoll);
            boolean canPoll = Calendar.getInstance().getTimeInMillis()>nextPoll;
            if (canPoll || force){
                Sys.save("nextPoll", (Calendar.getInstance().getTimeInMillis()+Sys.POLLING_INTERVAL*60*1000)+"",getApplicationContext());
                if( wearNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
                    Log.i("MCH Professional","Sending message");
                    Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, wearNode.getId(), Sys.PHONE_BATTERY_PATH, null).setResultCallback(

                            new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                                    if (sendMessageResult.getStatus().isSuccess()) {
                                        Log.i("MCH Professional","Message sent");
                                    }else{
                                    }
                                }
                            }
                    );
                }else{
                    mGoogleApiClient.connect();
                }
            }
        }



        private void pollSuntimes(boolean force) {
            long nextPoll = Long.parseLong(Sys.getString("nextPollSun","0",getApplicationContext()));
            boolean canPoll = Calendar.getInstance().getTimeInMillis()>nextPoll;
            if (canPoll || force) {
                Sys.save("nextPollSun", (Calendar.getInstance().getTimeInMillis()+Sys.POLLINGSUNTIMES_INTERVAL*60*1000) + "", getApplicationContext());
                if (wearNode != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    //
                    Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, wearNode.getId(), Sys.SUNTIMES_PATH, null).setResultCallback(

                            new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                                    if (sendMessageResult.getStatus().isSuccess()) {
                                        //Sys.save("lastPolledAll", Calendar.getInstance().getTimeInMillis() + "", getApplicationContext());
                                    }
                                }
                            }
                    );
                } else {
                    mGoogleApiClient.connect();
                }
            }
        }

        @Override
        public void onConnected(Bundle bundle) {
//            Toast.makeText(getApplicationContext(),"Connected!", Toast.LENGTH_SHORT).show();
            Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                @Override
                public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                    for (Node node : nodes.getNodes()) {
                        wearNode = node;
                    }
                }
            });
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }
    }
}
