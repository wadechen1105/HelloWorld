package com.example.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.tool.R;

/**
 * Implementation of App Widget functionality.
 */
public class ViewFilpAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = ViewFilpAppWidgetProvider.class.getSimpleName();

    public static final String NEXT_ACTION = "com.example.tool.NEXT";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            Log.i(TAG, "onUpdate, app id : " + id);
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.view_filp_app_widget_provider);

            // Specify the service to provide data for the collection widget.
            // Note that we need to
            // embed the appWidgetId via the data otherwise it will be ignored.
            final Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(R.id.page_flipper, intent);

            // Bind the click intent for the next button on the widget
            final Intent nextIntent = new Intent(context,
                    ViewFilpAppWidgetProvider.class);
            nextIntent.setAction(ViewFilpAppWidgetProvider.NEXT_ACTION);
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            final PendingIntent nextPendingIntent = PendingIntent
                    .getBroadcast(context, 0, nextIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.next, nextPendingIntent);

            appWidgetManager.updateAppWidget(id, rv);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        final ComponentName cn = new ComponentName(context,
                ViewFilpAppWidgetProvider.class);

        if (action.equals(NEXT_ACTION)) {
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.view_filp_app_widget_provider);

            rv.showNext(R.id.page_flipper);
            Log.i(TAG, "onReceive");

            mgr.partiallyUpdateAppWidget(
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID), rv);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                    R.id.page_flipper);
        }

        super.onReceive(context, intent);
    }
}

