package ca.nbsoft.whereareyou.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Nicolas on 2015-11-29.
 */
public class PreferenceUtils {

    static final String ACCOUNT_NAME = "ACCOUNT_NAME";
    static final String SENT_TOKEN_TO_SERVER = "SENT_TOKEN_TO_SERVER";
    private static final String USER_ID = "USER_ID";
    private static final String NOTIFICATION_SOUND = "notifications_sound";

    static private SharedPreferences getPrivatePreferences(Context ctx)
    {
        return ctx.getSharedPreferences("Private", Context.MODE_PRIVATE);
    }

    static public void setSentRegistrationToBackend( Context ctx, boolean sent)
    {
        SharedPreferences sharedPreferences = getPrivatePreferences(ctx);
        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, sent).apply();
    }

    static public boolean getSentRegistrationToBackend( Context ctx )
    {
        SharedPreferences sharedPreferences = getPrivatePreferences(ctx);
        return sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER,false);
    }

    static public void setAccountName(Context ctx, String accountName) {
        SharedPreferences sharedPreferences = getPrivatePreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCOUNT_NAME, accountName);
        editor.commit();
    }

    static public String getAccountName(Context ctx)
    {
        SharedPreferences sharedPreferences = getPrivatePreferences(ctx);
        return sharedPreferences.getString(ACCOUNT_NAME,null);
    }


    public static void setUserId(Context ctx, String userId) {
        SharedPreferences sharedPreferences = getPrivatePreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    static public String getUserId(Context ctx)
    {
        SharedPreferences sharedPreferences = getPrivatePreferences(ctx);
        return sharedPreferences.getString(USER_ID,null);
    }

    public static boolean getPlayNotificationSound(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getBoolean(NOTIFICATION_SOUND, false);
    }
}
