package ca.nbsoft.whereareyou.ui;

import android.content.Context;
import android.widget.Toast;

import ca.nbsoft.whereareyou.ApiService;

/**
 * Created by Nicolas on 2016-01-18.
 */
public class ErrorMessages {

    static public void showErrorMessage(Context ctx,ApiService.Result resultCode )
    {
        //TODO: make usefull error messages
        Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG).show();
    }
}
