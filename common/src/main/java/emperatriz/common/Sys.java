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
    public static final String WEAR_APPS = "/mch/wapps";
    public static final String WEAR_URLS = "/mch/wurls";

    public static String TIMER = "com.android.timer";
    public static String ALARM = "com.android.alarm";

    public static WappDto NORTH_DEFAULT = new WappDto("TIMER",TIMER);
    public static WappDto SOUTH_DEFAULT = new WappDto("CALCULATOR","com.google.android.calculator");
    public static WappDto EAST_DEFAULT = new WappDto("LIGHT","com.google.android.clockwork.flashlight");
    public static WappDto WEST_DEFAULT = new WappDto("ALARM",ALARM);




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

    public static void save(String key, long value, Context context){
        SharedPreferences preferences = context.getSharedPreferences("mchPro", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getLong(String key, long defValue, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mchPro", context.MODE_PRIVATE);
        return preferences.getLong(key, defValue);
    }

    public static void saveWapp(String key, WappDto dto, Context context){
        SharedPreferences preferences = context.getSharedPreferences("mchPro", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, dto.name+";"+dto.url);
        editor.commit();
    }

    public static WappDto getWapp(String key, WappDto defValue, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("mchPro", context.MODE_PRIVATE);
        String pair =  preferences.getString(key, defValue.name+";"+defValue.url);
        WappDto dto = new WappDto();
        dto.name = pair.split(";")[0];
        dto.url = pair.split(";")[1];
        return dto;

    }

    public static String getInstalledApps(Context ctx) {
        String ret="TIMER,"+TIMER+";ALARM,"+ALARM+";";
        List<PackageInfo> packs = ctx.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {

            PackageInfo p = packs.get(i);
            if (ctx.getPackageManager().getLaunchIntentForPackage(p.packageName) != null) {
                ret += p.applicationInfo.loadLabel(ctx.getPackageManager()).toString() + ",";
                ret += p.packageName + ";";
            }
        }
        return ret;
    }

    public static ArrayList<WappDto> parseWapps(String wapps){
        ArrayList<WappDto> ret = new ArrayList<WappDto>();
        try{
            String[] pairs = wapps.split(";");
            for(String pair : pairs){
                WappDto dto = new WappDto();
                dto.name = pair.split(",")[0];
                dto.url = pair.split(",")[1];
                if (true||!dto.name.equals(dto.url)){
                    ret.add(dto);
                }
            }
        }catch (Exception ex){

        }
        return ret;
    }


}
