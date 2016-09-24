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
    public static int offsetX,offsetY;


    public static int getBackColor(int index){
        if (index==0) return 0xff000000;
        if (index==1) return 0xff622181;
        if (index==2) return 0xff009EE0;
        if (index==3) return 0xffA10D59;
        if (index==4) return 0xff96BF0D;
        if (index==5) return 0x004E4E4E;
        if (index==6) return 0x001A554A;
        if (index==7) return 0xffCB6AA2;
        if (index==8) return 0xff000000;
        if (index==9) return 0x00324E2F;
        return 0xff000000;
    }

    public static void drawBackground(Bitmap shadow, Bitmap shadowAmb, Bitmap background,  Bitmap badge, Paint mBackgroundPaint, Paint whitePaint){
        mBackgroundPaint.setColor(color);
        if (isInAmbientMode) {
            canvas.drawRect(offsetX+0, offsetY+0, width, height, whitePaint);
            canvas.drawBitmap(shadowAmb, offsetX+0, offsetY+0, mBackgroundPaint);
        } else {
            canvas.drawRect(offsetX+0, offsetY+0, width, height, mBackgroundPaint);
            canvas.drawBitmap(shadow, offsetX+0, offsetY+0, mBackgroundPaint);
        }

        canvas.drawBitmap(background, offsetX+0, offsetY+0, mBackgroundPaint);
        canvas.drawBitmap(badge, offsetX+240, offsetY+32, mBackgroundPaint);

    }


    public static void drawDate(Paint paint){

            paint.setColor(0xff000000);


        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);
        float l1 = paint.measureText(mTime.format("%A"));
        if (l1>165) paint.setLetterSpacing(-0.05f);

        paint.setAlpha(isInAmbientMode?102:51);
        canvas.drawText(mTime.format("%A"), offsetX+42, offsetY+57, paint);
//        canvas.drawText("MIÉRCOLES", 42, 57, paint);
        paint.setAlpha(255);
        canvas.drawText(mTime.format("%A"), offsetX+39, offsetY+54, paint);
//        canvas.drawText("MIÉRCOLES", 39, 54, paint);
        paint.setAlpha(isInAmbientMode?102:51);
        String date = mTime.format("%d %B");
        if (date.startsWith("0")){
            date = date.substring(1);
        }

        paint.setLetterSpacing(0.05f);
        float l2 = paint.measureText(date);
        if (l2>171) paint.setLetterSpacing(-0.05f);

        canvas.drawText(date, offsetX+42, offsetY+82, paint);
//        canvas.drawText("23 SEPTIEMBRE", 42, 82, paint);
        paint.setAlpha(255);
        canvas.drawText(date, offsetX+39, offsetY+79, paint);
//        canvas.drawText("23 SEPTIEMBRE", 39, 79, paint);
    }

    public static void drawHHmm(Paint paint){

        paint.setColor(0xff000000);
        paint.setTextSize(70);
        paint.setLetterSpacing(0.08f);

        paint.setAlpha(isInAmbientMode?102:51);

        String time = String.format("%d:%02d", mTime.hour, mTime.minute);
        if (time.length()==4){
            time = "0"+time;
        }

        String hh = time.split(":")[0];
        String mm = time.split(":")[1];
        canvas.drawText(hh, offsetX+40, offsetY+158, paint);
        canvas.drawText(":", offsetX+157, offsetY+158, paint);
        canvas.drawText(mm, offsetX+172, offsetY+158, paint);
        paint.setAlpha(255);
        canvas.drawText(hh, offsetX+37, offsetY+155, paint);
        canvas.drawText(":", offsetX+153, offsetY+155, paint);
        canvas.drawText(":", offsetX+154, offsetY+155, paint);
        canvas.drawText(mm, offsetX+169, offsetY+155, paint);
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
            paint.setAlpha(isInAmbientMode?102:51);
            canvas.drawText(secs, offsetX+219, offsetY+199, paint);
            paint.setAlpha(255);
            canvas.drawText(secs, offsetX+216, offsetY+196, paint);
        }

    }




    public static void drawSteps(String value, Paint paint){

        paint.setColor(0xff000000);

        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);

        paint.setAlpha(isInAmbientMode?102:51);
        canvas.drawText(value, offsetX+72, offsetY+209, paint);
        paint.setAlpha(255);
        canvas.drawText(value, offsetX+69, offsetY+206, paint);
    }

    public static void drawPhoneBattery(String value, Paint paint){
        value = value.replace("100","99");
        paint.setColor(0xff000000);
        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);

        float w = paint.measureText(value);
        paint.setAlpha(isInAmbientMode?102:51);
        canvas.drawText(value, offsetX+263-w, offsetY+247, paint);
        paint.setAlpha(255);
        canvas.drawText(value, offsetX+260-w, offsetY+244, paint);
    }

    public static void drawWatchBattery(String value, Paint paint){
        value = value.replace("100","99");
        paint.setColor(0xff000000);
        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);
        float w = paint.measureText(value);
        paint.setAlpha(isInAmbientMode?102:51);
        canvas.drawText(value, offsetX+263-w, offsetY+285, paint);
        paint.setAlpha(255);
        canvas.drawText(value, offsetX+260-w, offsetY+282, paint);

    }

    public static void drawSunrise(String value, Paint paint){
        if (value.startsWith("0")){
            value = value.substring(1);
        }
        paint.setColor(0xff000000);
        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);

        paint.setAlpha(isInAmbientMode?102:51);
        canvas.drawText(value, offsetX+72, offsetY+247, paint);
        paint.setAlpha(255);
        canvas.drawText(value, offsetX+69, offsetY+244, paint);
    }

    public static void drawSunset(String value, Paint paint){
        if (value.startsWith("0")){
            value = value.substring(1);
        }
        paint.setColor(0xff000000);
        paint.setTextSize(24);
        paint.setLetterSpacing(0.05f);


        paint.setAlpha(isInAmbientMode?102:51);
        canvas.drawText(value, offsetX+72, offsetY+285, paint);
        paint.setAlpha(255);
        canvas.drawText(value, offsetX+69, offsetY+282, paint);
    }

    public static void drawUnread(int value, List<Bitmap> bells, Paint paint){

        paint.setColor(0xff000000);
        paint.setTextSize(53);
        paint.setLetterSpacing(0.05f);
        if (value>0){
            value = value>9?0:value;
            paint.setAlpha(isInAmbientMode?102:51);
            canvas.drawBitmap(bells.get(value), offsetX+135, offsetX+195, paint);
            paint.setAlpha(255);
            canvas.drawBitmap(bells.get(value), offsetX+132, offsetX+192, paint);
        }

    }

    public static void drawShortcuts(String north, String south, String east, String west, int backColor, Paint paint) {

        paint.setTextSize(23);
        paint.setLetterSpacing(-0.05f);

        float w = paint.measureText(north);
        paint.setColor(backColor);
        paint.setStyle(Paint.Style.FILL);
        //if (w%2==1) w++;
        canvas.drawRect(offsetX+width/2-w/2-4,offsetY+0,offsetX+width/2+w/2+4,offsetY+22,paint);
        paint.setColor(0xffffffff);
        canvas.drawText(north, offsetX+width/2-w/2, offsetY+19, paint);

        w = paint.measureText(south);
        paint.setColor(backColor);
        paint.setStyle(Paint.Style.FILL);
//        if (w%2==1) w++;
        canvas.drawRect(offsetX+width/2-w/2-4,offsetY+298,offsetX+width/2+w/2+4,offsetY+height,paint);
        paint.setColor(0xffffffff);
        canvas.drawText(south, offsetX+width/2-w/2, offsetY+317, paint);

        canvas.rotate(-90,offsetX+width/2,offsetY+height/2);

        w = paint.measureText(west);
        paint.setColor(backColor);
        paint.setStyle(Paint.Style.FILL);
//        if (w%2==1) w++;
        canvas.drawRect(offsetX+width/2-w/2-4,offsetY+0,offsetX+width/2+w/2+4,offsetY+22,paint);
        paint.setColor(0xffffffff);
        canvas.drawText(west, offsetX+width/2-w/2, offsetY+19, paint);

        w = paint.measureText(east);
        paint.setColor(backColor);
        paint.setStyle(Paint.Style.FILL);
//        if (w%2==1) w++;
        canvas.drawRect(offsetX+width/2-w/2-4,offsetY+298,offsetX+width/2+w/2+4,offsetY+height,paint);
        paint.setColor(0xffffffff);
        canvas.drawText(east, offsetX+width/2-w/2, offsetY+317, paint);

        canvas.rotate(90,offsetX+width/2,offsetY+height/2);

    }
}
