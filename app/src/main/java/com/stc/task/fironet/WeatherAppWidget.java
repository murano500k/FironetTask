package com.stc.task.fironet;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WeatherWidgetConfigureActivity WeatherWidgetConfigureActivity}
 */
public class WeatherAppWidget extends AppWidgetProvider {
	private static final String TAG = "WeatherAppWidget";
	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
	                            int appWidgetId) {

		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

		String widgetText = WeatherWidgetConfigureActivity.loadWeatherTextPref(context, appWidgetId);
		String iconUrl = WeatherWidgetConfigureActivity.loadWeatherIconUrlPref(context, appWidgetId);
		if(iconUrl!=null){
			Picasso.with(context).load(Uri.parse(iconUrl)).into(new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
					Log.d(TAG, "onBitmapLoaded: ");
					views.setImageViewBitmap(R.id.appwidget_image, bitmap);
					views.setTextViewText(R.id.appwidget_text, widgetText!=null ? widgetText : context.getString(R.string.no_data));
					appWidgetManager.updateAppWidget(appWidgetId, views);
				}

				@Override
				public void onBitmapFailed(Drawable errorDrawable) {
					Log.e(TAG, "onBitmapFailed: " );
					views.setImageViewResource(R.id.appwidget_image, android.R.drawable.ic_dialog_alert);
				}

				@Override
				public void onPrepareLoad(Drawable placeHolderDrawable) {

				}
			});
		}


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
			WeatherWidgetConfigureActivity.deleteWeatherPrefData(context, appWidgetId);
		}
	}

	@Override
	public void onEnabled(Context context) {
		if(WeatherWidgetConfigureActivity.hasSavedLocation(context)) {
			WeatherWidgetConfigureActivity.scheduleJob(context);
		}
	}

	@Override
	public void onDisabled(Context context) {
		WeatherWidgetConfigureActivity.cancelJob(context);
	}
}

