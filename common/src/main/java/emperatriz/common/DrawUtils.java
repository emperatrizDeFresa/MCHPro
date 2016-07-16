package emperatriz.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.format.Time;

import java.util.List;

/**
 * Created by ramon on 2/07/16.
 */
public class DrawUtils {
    public static int height,width;
    public static Canvas canvas;
    public static boolean isInAmbientMode;
    public static Time mTime;
    public static int color;
    public static long now;

    public static void drawBackground(Bitmap mBackgroundBitmap, Bitmap mBackgroundBitmapAmb,Paint mBackgroundPaint, Paint whitePaint){
        mBackgroundPaint.setColor(color);
        if (isInAmbientMode) {
            canvas.drawRect(0, 0, width, height, whitePaint);
            canvas.drawBitmap(mBackgroundBitmapAmb, 0, 0, mBackgroundPaint);
        } else {
            canvas.drawRect(0, 0, width, height, mBackgroundPaint);
            canvas.drawBitmap(mBackgroundBitmap, 0, 0, mBackgroundPaint);
        }


    }


    public static void drawDate(Paint paint){

            paint.setColor(0xff000000);


        paint.setTextSize(24);
//        paint.setLetterSpacing(-0.05f);

        paint.setAlpha(51);
        canvas.drawText(mTime.format("%A"), 42, 57, paint);
//        canvas.drawText("MIÉRCOLES", 42, 57, paint);
        paint.setAlpha(255);
        canvas.drawText(mTime.format("%A"), 39, 54, paint);
//        canvas.drawText("MIÉRCOLES", 39, 54, paint);
        paint.setAlpha(51);
        String date = mTime.format("%d %B");
        if (date.startsWith("0")){
            date = date.substring(1);
        }
        canvas.drawText(date, 42, 82, paint);
//        canvas.drawText("23 SEPTIEMBRE", 42, 82, paint);
        paint.setAlpha(255);
        canvas.drawText(date, 39, 79, paint);
//        canvas.drawText("23 SEPTIEMBRE", 39, 79, paint);
    }

    public static void drawHHmm(Paint paint){

        paint.setColor(0xff000000);
        paint.setTextSize(70);
        paint.setLetterSpacing(0.08f);

        paint.setAlpha(51);

        String time = String.format("%d:%02d", mTime.hour, mTime.minute);
        if (time.length()==4){
            time = "0"+time;
        }

        String hh = time.split(":")[0];
        String mm = time.split(":")[1];
        canvas.drawText(hh, 40, 158, paint);
        canvas.drawText(":", 157, 158, paint);
        canvas.drawText(mm, 172, 158, paint);
        paint.setAlpha(255);
        canvas.drawText(hh, 37, 155, paint);
        canvas.drawText(":", 153, 155, paint);
        canvas.drawText(":", 154, 155, paint);
        canvas.drawText(mm, 169, 155, paint);
    }

    public static void drawSecs(Paint paint){

        String secs = String.format("%02d", mTime.second);

                paint.setTextSize(37);
        paint.setLetterSpacing(0.20f);

        if (isInAmbientMode) {
            paint.setColor(0xff999999);

            //canvas.drawText("--", 216, 196, paint);
        }else{
            paint.setColor(0xff000000);
            paint.setAlpha(51);
            canvas.drawText(secs, 219, 199, paint);
            paint.setAlpha(255);
            canvas.drawText(secs, 216, 196, paint);
        }

    }




    public static void drawSteps(String value, Paint paint){

        paint.setColor(0xff000000);

        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);

        paint.setAlpha(51);
        canvas.drawText(value, 72, 209, paint);
        paint.setAlpha(255);
        canvas.drawText(value, 69, 206, paint);
    }

    public static void drawPhoneBattery(String value, Paint paint){
        value = value.replace("100","99");
        paint.setColor(0xff000000);
        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);

        float w = paint.measureText(value);
        paint.setAlpha(51);
        canvas.drawText(value, 284-w, 247, paint);
        paint.setAlpha(255);
        canvas.drawText(value, 281-w, 244, paint);
    }

    public static void drawWatchBattery(String value, Paint paint){
        value = value.replace("100","99");
        paint.setColor(0xff000000);
        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);
        float w = paint.measureText(value);
        paint.setAlpha(51);
        canvas.drawText(value, 284-w, 285, paint);
        paint.setAlpha(255);
        canvas.drawText(value, 281-w, 282, paint);

    }

    public static void drawSunrise(String value, Paint paint){
        if (value.startsWith("0")){
            value = value.substring(1);
        }
        paint.setColor(0xff000000);
        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);

        paint.setAlpha(51);
        canvas.drawText(value, 72, 247, paint);
        paint.setAlpha(255);
        canvas.drawText(value, 69, 244, paint);
    }

    public static void drawSunset(String value, Paint paint){
        if (value.startsWith("0")){
            value = value.substring(1);
        }
        paint.setColor(0xff000000);
        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);


        paint.setAlpha(51);
        canvas.drawText(value, 72, 285, paint);
        paint.setAlpha(255);
        canvas.drawText(value, 69, 282, paint);
    }

    public static void drawUnread(int value, List<Bitmap> bells, Paint paint){

        paint.setColor(0xff000000);
        paint.setTextSize(53);
        paint.setLetterSpacing(0.05f);
        if (value>0){
            value = value>9?0:value;
            canvas.drawBitmap(bells.get(value), 132, 192, paint);
        }

    }
}
