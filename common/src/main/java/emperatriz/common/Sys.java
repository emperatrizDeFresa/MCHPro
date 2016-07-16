package emperatriz.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Sys {

    private static Sys instance;

    public static Sys init(){
        if (instance==null)
            instance = new Sys();
        return instance;
    }

    public static final String PHONE_BATTERY_PATH = "/mch/battery";
    public static final String SUNTIMES_PATH = "/mch/suntimes";
    public static final String COLOR_PATH = "/mch/color";

    private Sys(){
    }

    public static int POLLING_INTERVAL=10;
    public static int POLLINGSUNTIMES_INTERVAL =20*60;

    public static void save(String key, String value, Context context){
        SharedPreferences preferences = context.getSharedPreferences("mchPro", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(String key, String defValue, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mchPro", context.MODE_PRIVATE);
        return preferences.getString(key, defValue);
    }

    public static void save(String key, int value, Context context){
        SharedPreferences preferences = context.getSharedPreferences("mchPro", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInt(String key, int defValue, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mchPro", context.MODE_PRIVATE);
        return preferences.getInt(key, defValue);
    }

    public static String getInstalledApps(Context ctx) {
        String ret="";
        List<PackageInfo> packs = ctx.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            ret += p.applicationInfo.loadLabel(ctx.getPackageManager()).toString()+",";
            ret += p.packageName+";";
        }
        return ret;
    }


}
