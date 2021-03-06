package ca.nbsoft.whereareyou.Utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ca.nbsoft.whereareyou.R;

/**
 * Created by Nicolas on 2015-12-24.
 */
public class Utils {

    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    static public void closeKeyboard(Activity activity)
    {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void cancelableActionSnackbar(View v, String text, final Runnable action)
    {
        Snackbar snackbar = Snackbar.make(v, text, Snackbar.LENGTH_SHORT);
        doCancelableActionSnackbar(snackbar,action);

    }

    public static void cancelableActionSnackbar(View v, @StringRes int string, final Runnable action)
    {
        Snackbar snackbar = Snackbar.make(v, string, Snackbar.LENGTH_SHORT);
        doCancelableActionSnackbar(snackbar,action);

    }

    static private void doCancelableActionSnackbar( Snackbar snackbar,   final Runnable action)
    {
        snackbar.setAction(R.string.contact_detail_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });

        snackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    action.run();
                }
            }
        });

        snackbar.show();
    }
}
