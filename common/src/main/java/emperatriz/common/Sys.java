package emperatriz.common;

import android.content.Context;
import android.content.SharedPreferences;

public class Sys {

    private static Sys instance;

    public static Sys init(){
        if (instance==null)
            instance = new Sys();
        return instance;
    }

    public static final String PHONE_BATTERY_PATH = "/mch/battery";
    public static final String SUNTIMES_PATH = "/mch/suntimes";

    private Sys(){
    }

    public static int POLLING_INTERVAL=10;
    public static int POLLINGSUNTIMES_INTERVAL =12*60;

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


}
