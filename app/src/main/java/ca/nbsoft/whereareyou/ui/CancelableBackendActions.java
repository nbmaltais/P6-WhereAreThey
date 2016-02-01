package ca.nbsoft.whereareyou.ui;

import android.app.Activity;
import android.content.Context;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.Utility.Utils;

/**
 * Created by Nicolas on 31/01/2016.
 */
public class CancelableBackendActions {

    static public void sendLocation( final Activity ctx, final String userId, final String message )
    {
        String text = ctx.getString(R.string.contact_detail_send_position_confirmation);

        Utils.cancelableActionSnackbar(ctx.findViewById(android.R.id.content), text, new Runnable() {
            @Override
            public void run() {
                ApiService.sendLocation(ctx, userId, message);
            }
        });
    }

    static public void requestLocation(final Activity ctx, final String userId, final String message)
    {
        String text = ctx.getString(R.string.contact_detail_request_position_confirmation);

        Utils.cancelableActionSnackbar(ctx.findViewById(android.R.id.content), text, new Runnable() {
            @Override
            public void run() {
                ApiService.requestContactLocation(ctx, userId, message);
            }
        });
    }

    static public void confirmContactRequest(final Activity ctx, final String userId )
    {
        String text = ctx.getString(R.string.confirm_contact_confirmation);

        Utils.cancelableActionSnackbar(ctx.findViewById(android.R.id.content), text, new Runnable() {
            @Override
            public void run() {
                ApiService.confirmContactRequest(ctx, userId, true);
            }
        });

    }

    static public void refuseContactRequest(final Activity ctx, final String userId )
    {
        String text = ctx.getString(R.string.refuse_contact_confirmation);

        Utils.cancelableActionSnackbar(ctx.findViewById(android.R.id.content), text, new Runnable() {
            @Override
            public void run() {
                ApiService.confirmContactRequest(ctx, userId, false);
            }
        });

    }


}
