package eu.z3r0byteapps.shary.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigUtil {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public ConfigUtil(Context context) {
        sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void removePreferencesValue(String value) {
        editor.remove(value);
        editor.apply();
    }

    public Boolean getBoolean(String name) {
        return sharedPreferences.getBoolean(name, false);
    }

    public Boolean getBoolean(String name, Boolean nullValue) {
        return sharedPreferences.getBoolean(name, nullValue);
    }

    public int getInteger(String name) {
        return sharedPreferences.getInt(name, 0);
    }

    public int getInteger(String name, Integer nullValue) {
        return sharedPreferences.getInt(name, nullValue);
    }

    public String getString(String name) {
        return sharedPreferences.getString(name, "");
    }

    public String getString(String name, String nullValue) {
        return sharedPreferences.getString(name, nullValue);
    }

    public void setInteger(String name, Integer integer) {
        editor.putInt(name, integer);
        editor.apply();
    }

    public void setBoolean(String name, Boolean bol) {
        editor.putBoolean(name, bol);
        editor.apply();
    }

    public void setString(String name, String value) {
        editor.putString(name, value);
        editor.apply();
    }
}
