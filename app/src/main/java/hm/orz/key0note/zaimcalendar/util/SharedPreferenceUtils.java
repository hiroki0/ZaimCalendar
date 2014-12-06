package hm.orz.key0note.zaimcalendar.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceUtils {

    private static final String LOGIN_KEY = "login";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String ACCESS_TOKEN_SECRET_KEY = "access_token_secret";

    private SharedPreferenceUtils() {
    }

    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor getDefaultSharedPreferencesEditor(Context context) {
        return getDefaultSharedPreferences(context).edit();
    }

    public static boolean isLogin(Context context) {
        return getDefaultSharedPreferences(context).getBoolean(LOGIN_KEY, false);
    }

    public static void setLoginState(Context context, boolean isLogin) {
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor(context);
        editor.putBoolean(LOGIN_KEY, isLogin);
        editor.commit();
    }

    public static String getAccessToken(Context context) {
        return getDefaultSharedPreferences(context).getString(ACCESS_TOKEN_KEY, null);
    }

    public static void setAccessToken(Context context, String token) {
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor(context);
        editor.putString(ACCESS_TOKEN_KEY, token);
        editor.commit();
    }

    public static String getAccessTokenSecret(Context context) {
        return getDefaultSharedPreferences(context).getString(ACCESS_TOKEN_SECRET_KEY, null);
    }

    public static void setAccessTokenSecret(Context context, String secret) {
        SharedPreferences.Editor editor = getDefaultSharedPreferencesEditor(context);
        editor.putString(ACCESS_TOKEN_SECRET_KEY, secret);
        editor.commit();
    }

}