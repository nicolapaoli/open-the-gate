package it.nicolapaoli.gate.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import it.nicolapaoli.gate.R;
import it.nicolapaoli.gate.activities.DialActivity;

public class WidgetCallProvider extends AppWidgetProvider {
    public static final String TOAST_ACTION = "TOAST_ACTION";
    public static final String EXTRA_ITEM = "EXTRA_ITEM";

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; ++i){
            Intent intent = new Intent(context, DialActivity.class);

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.appwidget_call);

            PendingIntent toastPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            rv.setOnClickPendingIntent(R.id.widget_gate_icon, toastPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
