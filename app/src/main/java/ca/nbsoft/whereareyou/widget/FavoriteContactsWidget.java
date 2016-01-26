package ca.nbsoft.whereareyou.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import ca.nbsoft.whereareyou.R;
import ca.nbsoft.whereareyou.ui.contact.ContactDetailActivity;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link FavoriteContactsWidgetConfigureActivity FavoriteContactsWidgetConfigureActivity}
 */
public class FavoriteContactsWidget extends AppWidgetProvider {


    static private PendingIntent getOnClickPendingIntent(Context context, int reqId, String userId)
    {
        Intent intent = ContactDetailActivity.getStartActivityIntent(context, userId);
        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(reqId, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        SharedPreferences sharedPreferences = FavoriteContactsWidgetConfigureActivity.getSharedPreferences(context);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_contacts_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);


        String name1 = sharedPreferences.getString(FavoriteContactsWidgetConfigureActivity.nameKey(0,appWidgetId),"-Not set-");
        String name2 = sharedPreferences.getString(FavoriteContactsWidgetConfigureActivity.nameKey(1, appWidgetId), "-Not set-");
        String name3 = sharedPreferences.getString(FavoriteContactsWidgetConfigureActivity.nameKey(2,appWidgetId),"-Not set-");

        String userId1 = sharedPreferences.getString(FavoriteContactsWidgetConfigureActivity.userIdKey(0, appWidgetId),null);
        String userId2 = sharedPreferences.getString(FavoriteContactsWidgetConfigureActivity.userIdKey(1, appWidgetId),null);
        String userId3 = sharedPreferences.getString(FavoriteContactsWidgetConfigureActivity.userIdKey(2,appWidgetId), null);


        views.setTextViewText(R.id.appwidget_contact1,name1);
        views.setTextViewText(R.id.appwidget_contact2,name2);
        views.setTextViewText(R.id.appwidget_contact3, name3);

        views.setViewVisibility(R.id.appwidget_contact1, userId1!= null ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.appwidget_contact2, userId2!=null ? View.VISIBLE : View.GONE);
        views.setViewVisibility(R.id.appwidget_contact3, userId3!=null ? View.VISIBLE : View.GONE);

        if(userId1!=null) {
            views.setOnClickPendingIntent(R.id.appwidget_contact1, getOnClickPendingIntent(context,11,userId1));
        }
        if(userId2!=null) {
            views.setOnClickPendingIntent(R.id.appwidget_contact2, getOnClickPendingIntent(context,12, userId2));
        }
        if(userId3!=null) {
            views.setOnClickPendingIntent(R.id.appwidget_contact3, getOnClickPendingIntent(context,13, userId3));
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            FavoriteContactsWidgetConfigureActivity.deletePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private int getWidgetHeightFromOptions(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            // Value returned is in dp, but we'll convert it to pixels to match the other height
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  context.getResources().getDimensionPixelSize(R.dimen.widget_default_height);
    }
}

