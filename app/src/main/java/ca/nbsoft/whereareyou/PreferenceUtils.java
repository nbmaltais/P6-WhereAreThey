package ca.nbsoft.whereareyou;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Nicolas on 2015-11-29.
 */
public class PreferenceUtils {

    static final String ACCOUNT_NAME = "ACCOUNT_NAME";
    static final String SENT_TOKEN_TO_SERVER = "SENT_TOKEN_TO_SERVER";

    static public void setSentRegistrationToBackend( Context ctx, boolean sent)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, sent).apply();
    }

    static public boolean getSentRegistrationToBackend( Context ctx )
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER,false);
    }

    static public void setAccountName(Context ctx, String accountName) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCOUNT_NAME, accountName);
        editor.commit();
    }

    static public String getAccountName(Context ctx)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getString(ACCOUNT_NAME,null);
    }



}