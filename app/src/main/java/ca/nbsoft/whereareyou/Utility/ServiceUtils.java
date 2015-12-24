package ca.nbsoft.whereareyou.Utility;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Nicolas on 2015-12-23.
 */
public class ServiceUtils {
    private static String LOG_TAG = ServiceUtils.class.getName();

    public static boolean isServiceRunning(Context ctx, String serviceClassName){
        final ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }

        return false;
    }
}