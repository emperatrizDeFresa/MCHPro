package emperatriz.mchprofessional;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

import emperatriz.common.DrawUtils;
import emperatriz.common.Sys;

public class CanvasView extends View {

    public int width=320;
    public int height=320;

    int backColor,badgeIndex=0;

    Context context;
    Paint timePaint,restPaint,mBackgroundPaint;
    MainActivity ma;
    Bitmap back, swr;
    ArrayList<Bitmap> badges;
    Bitmap badge, badge1, badge2, badge3, badge4,badge5;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        back = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back);
        back =  Bitmap.createScaledBitmap(back,320,320, true);

        swr = BitmapFactory.decodeResource(getResources(), R.drawable.swr50);
        swr =  Bitmap.createScaledBitmap(swr,501,660, true);

        badges = new ArrayList<Bitmap>();
        badge = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge);
        badge = Bitmap.createScaledBitmap(badge,48,48, true);
        badges.add(badge);
        badge1 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge1);
        badge1 = Bitmap.createScaledBitmap(badge1,48,48, true);
        badges.add(badge1);
        badge2 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge2);
        badge2 = Bitmap.createScaledBitmap(badge2,48,48, true);
        badges.add(badge2);
        badge3 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge3);
        badge3 = Bitmap.createScaledBitmap(badge3,48,48, true);
        badges.add(badge3);
        badge4 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge4);
        badge4 = Bitmap.createScaledBitmap(badge4,48,48, true);
        badges.add(badge4);
        badge5 = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.badge5);
        badge5 = Bitmap.createScaledBitmap(badge5,48,48, true);
        badges.add(badge5);

        Typeface font1 = Typeface.createFromAsset(c.getAssets(), "fonts/SF Square Head.ttf");
        Typeface font2 = Typeface.createFromAsset(c.getAssets(), "fonts/Square.ttf");

        timePaint = new Paint();
        timePaint.setTypeface(font1);
        timePaint.setAntiAlias(false);

        restPaint = new Paint();
        restPaint.setTypeface(font2);
        restPaint.setAntiAlias(false);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(backColor);

        DrawUtils.mTime = new Time();
        DrawUtils.offsetX=90;
        DrawUtils.offsetY=147;
        update.run();
    }

    public void setMainActivity(MainActivity ma){
        this.ma=ma;
    }

    @Override
    public void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(501, 660);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


    }

    Handler handler = new Handler(Looper.getMainLooper());
    Runnable update = new Runnable(){
        public void run(){
            invalidate();
            handler.postDelayed(this,1000);
        }
    };

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        Bitmap back = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back);
//        back =  Bitmap.createScaledBitmap(back,320,320, true);
//
//        Bitmap swr = BitmapFactory.decodeResource(getResources(), R.drawable.swr50);
//        swr =  Bitmap.createScaledBitmap(swr,501,660, true);

        mBackgroundPaint.setColor(backColor);
        canvas.drawBitmap(swr,0,0,null);
        canvas.drawRect(DrawUtils.offsetX+0, DrawUtils.offsetY+0, DrawUtils.offsetX+width, DrawUtils.offsetY+height,mBackgroundPaint );
        canvas.drawBitmap(back,DrawUtils.offsetX+0,DrawUtils.offsetY+0,null);

        DrawUtils.mTime.setToNow();
        DrawUtils.now = System.currentTimeMillis();
        DrawUtils.height = 320;
        DrawUtils.width = 320;
        DrawUtils.canvas = canvas;
        DrawUtils.isInAmbientMode = false;


        canvas.drawBitmap(badges.get(badgeIndex), DrawUtils.offsetX+240, DrawUtils.offsetY+32, mBackgroundPaint);
        DrawUtils.drawDate(restPaint);
        DrawUtils.drawHHmm(timePaint);
        DrawUtils.drawSecs(timePaint);
        DrawUtils.drawSteps("12324", restPaint);
        DrawUtils.drawWatchBattery("58", restPaint);
        DrawUtils.drawPhoneBattery("79", restPaint);
        DrawUtils.drawSunrise("7:11", restPaint);
        DrawUtils.drawSunset("22:06", restPaint);
        DrawUtils.drawShortcuts(ma.north.name,ma.south.name,ma.east.name,ma.west.name, restPaint);
    }


    public void setColor(int color){
        backColor = color;
        invalidate();
    }

    public void setBadge(int index){
        badgeIndex = index;
        invalidate();
    }

    public void clearCanvas() {
        invalidate();
    }

}
