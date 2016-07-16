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

import emperatriz.common.DrawUtils;
import emperatriz.common.Sys;

public class CanvasView extends View {

    public int width=320;
    public int height=320;

    int backColor;

    Context context;
    Paint timePaint,restPaint,mBackgroundPaint;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

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
        update.run();
    }

    @Override
    public void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(320, 320);
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

        Bitmap back = BitmapFactory.decodeResource(getResources(), emperatriz.common.R.drawable.back);
        back =  Bitmap.createScaledBitmap(back,320,320, true);

        mBackgroundPaint.setColor(backColor);
        canvas.drawRect(0, 0, width, height,mBackgroundPaint );
        canvas.drawBitmap(back,0,0,null);

        DrawUtils.mTime.setToNow();
        DrawUtils.now = System.currentTimeMillis();
        DrawUtils.height = 320;
        DrawUtils.width = 320;
        DrawUtils.canvas = canvas;
        DrawUtils.isInAmbientMode = false;


        DrawUtils.drawDate(restPaint);
        DrawUtils.drawHHmm(timePaint);
        DrawUtils.drawSecs(timePaint);
        DrawUtils.drawSteps("12324", restPaint);
        DrawUtils.drawWatchBattery("58%", restPaint);
        DrawUtils.drawPhoneBattery("79%", restPaint);
        DrawUtils.drawSunrise("7:11", restPaint);
        DrawUtils.drawSunset("22:06", restPaint);
    }


    public void setColor(int color){
        backColor = color;
        invalidate();
    }

    public void clearCanvas() {
        invalidate();
    }

}
