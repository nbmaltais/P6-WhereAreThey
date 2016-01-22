package ca.nbsoft.whereareyou.ui;

import android.content.Context;
import android.widget.Toast;

import ca.nbsoft.whereareyou.ApiService;
import ca.nbsoft.whereareyou.R;

/**
 * Created by Nicolas on 2016-01-18.
 */
public class ErrorMessages {

    static public void showErrorMessage(Context ctx,ApiService.Result resultCode )
    {
        switch(resultCode.getResultCode())
        {
            case ApiService.Result.RESULT_SUCCESS:
                break;
            case ApiService.Result.RESULT_ERROR_NO_NETWORK:
                Toast.makeText(ctx, R.string.error_message_no_network, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(ctx, R.string.error_message_generic, Toast.LENGTH_LONG).show();
                break;
        }

        //TODO: make usefull error messages

    }
}
