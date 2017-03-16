package com.stc.weatherapp.demo.widget;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.appwidget.AppWidgetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stc.weatherapp.demo.R;
import com.stc.weatherapp.demo.json.WeatherDataForLocation;
import com.stc.weatherapp.demo.net.WeatherHelper;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.stc.weatherapp.demo.widget.WeatherWidgetConfigureActivity.EXTRA_WIDGET_ID;
import static com.stc.weatherapp.demo.widget.WeatherWidgetConfigureActivity.QUERY_LAT;
import static com.stc.weatherapp.demo.widget.WeatherWidgetConfigureActivity.QUERY_LON;

/**
 * Created by artem on 3/3/17.
 */


public class WeatherJobService extends JobService {
	private static final String TAG = "WeatherJobService";
	@Override
	public boolean onStartJob(JobParameters params) {
		String lat = params.getExtras().getString(QUERY_LAT);
		String lon = params.getExtras().getString(QUERY_LON);
		int widgetId= params.getExtras().getInt(EXTRA_WIDGET_ID);

		WeatherHelper weatherHelper=new WeatherHelper();
		Observable.fromCallable(new Callable<WeatherDataForLocation>() {
			@Override
			public WeatherDataForLocation call() throws Exception {
				return weatherHelper.loadWeather(lat,lon);
			}
		}).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
		.subscribe(new Observer<WeatherDataForLocation>() {
			@Override
			public void onSubscribe(Disposable d) {
				Log.d(TAG, "onSubscribe: ");
			}

			@Override
			public void onNext(WeatherDataForLocation weatherDataForLocation) {
				Log.d(TAG, "onNext: "+weatherDataForLocation);
				String iconUrl = weatherHelper.getIconUrl(weatherDataForLocation.getWeather());
				String city = weatherDataForLocation.getName();
				String weatherText=weatherHelper.buildWeatherText(weatherDataForLocation.getMain(), city);
				if(weatherText!=null && iconUrl!=null) updateWidget(weatherText, iconUrl, widgetId);
				else Log.e(TAG, "onNext: no data" );
			}

			@Override
			public void onError(Throwable e) {
				Log.e(TAG, "onError: ",e );
				WeatherJobService.this.onStopJob(null);
			}

			@Override
			public void onComplete() {
				Log.d(TAG, "onComplete: ");
				WeatherJobService.this.onStopJob(null);
			}
		});
		return true;
	}

	private void updateWidget(String weatherText, String iconUrl, int widgetId) {
		Log.d(TAG, "updateWidget: text = "+weatherText);
		Log.d(TAG, "updateWidget: icon_url = "+iconUrl);
		AppWidgetManager awm=(AppWidgetManager)getSystemService(APPWIDGET_SERVICE);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.new_app_widget);
		if(iconUrl!=null){
			Picasso.with(this).load(Uri.parse(iconUrl)).into(new Target() {
				@Override
				public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
					Log.d(TAG, "onBitmapLoaded: ");
					views.setImageViewBitmap(R.id.appwidget_image, bitmap);
					views.setTextViewText(R.id.appwidget_text, weatherText!=null ? weatherText : getString(R.string.no_data));
					awm.updateAppWidget(widgetId, views);
					onStopJob(null);
				}

				@Override
				public void onBitmapFailed(Drawable errorDrawable) {
					Log.e(TAG, "onBitmapFailed: " );
					views.setImageViewResource(R.id.appwidget_image, android.R.drawable.ic_dialog_alert);
					views.setTextViewText(R.id.appwidget_text, weatherText!=null ? weatherText : getString(R.string.no_data));
					awm.updateAppWidget(widgetId, views);
				}

				@Override
				public void onPrepareLoad(Drawable placeHolderDrawable) {

				}
			});
		}
	}



	@Override
	public boolean onStopJob(JobParameters params) {
		return false;
	}

}
