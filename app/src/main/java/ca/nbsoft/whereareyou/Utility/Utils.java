package ca.nbsoft.whereareyou.Utility;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ca.nbsoft.whereareyou.R;

/**
 * Created by Nicolas on 2015-12-24.
 */
public class Utils {

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
